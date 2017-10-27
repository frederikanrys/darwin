package be.telenet.edev.aem.cpt.core.services.impl;

import be.telenet.edev.aem.cpt.api.config.PackageConfig;
import be.telenet.edev.aem.cpt.api.config.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.reference.Reference;
import com.day.cq.wcm.api.reference.ReferenceProvider;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Component(service = PackageConfigReferenceProvider.class)
public class PackageConfigReferenceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PackageConfigReferenceProvider.class);

    @org.osgi.service.component.annotations.Reference
    private volatile List<ReferenceProvider> referenceProviders;


    public SortedSet<Resource> findReferences(PackageConfig packageConfig) {
        LOG.debug("Searching for the references for package config: {}", packageConfig);
        ResourceResolver resourceResolver = packageConfig.getResource().getResourceResolver();
        SortedSet<Resource> allReferences = new TreeSet<>(Comparator.comparing(Resource::getPath));
        addReferences(packageConfig.getPages(), allReferences, resourceResolver);
        LOG.debug("Found {} references for package config: {}", allReferences.size(), packageConfig);
        return allReferences;
    }

    public SortedSet<Resource> findReferencesOfPage(Page page) {
        LOG.debug("Searching for the references for a page: {}", page);
        ResourceResolver resourceResolver = page.getResource().getResourceResolver();
        SortedSet<Resource> pageReferences = new TreeSet<>(Comparator.comparing(Resource::getPath));
        addPageReferences(page, pageReferences, resourceResolver);
        LOG.debug("Found {} references for page: {}", pageReferences.size(), page);
        return pageReferences;
    }

    private void addReferences(List<Page> pages, SortedSet<Resource> allReferences, ResourceResolver resourceResolver) {
        for (Page page : pages) {
            addPageReferences(page, allReferences, resourceResolver);
        }
    }

    private void addPageReferences(Page page, SortedSet<Resource> allReferences, ResourceResolver resourceResolver) {
        Resource resource = page.getTargetResource();
        LOG.trace("Adding all references for page: {}, including sub pages: {}", page.getPath());
        addReferencesForResource(page, resource, allReferences, resourceResolver);
    }

    private void addReferencesForResource(Page page, Resource resource, SortedSet<Resource> allReferences, ResourceResolver resourceResolver) {
        if (resource != null) {
            for (ReferenceProvider referenceProvider : this.referenceProviders) {
                LOG.trace("Adding all references for resource: {} and reference provider:{}", resource.getPath(), referenceProvider);
                allReferences.addAll(referenceProvider.findReferences(resource)
                        .stream()
                        .map(Reference::getResource)
                        .collect(Collectors.toList()));
            }
            if (page.getSubPages()) {
                addReferencesForSubpagesOfResource(page, resource, allReferences, resourceResolver);
            }
        }
    }

    private void addReferencesForSubpagesOfResource(Page page, Resource resource, SortedSet<Resource> allReferences, ResourceResolver resourceResolver) {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            com.day.cq.wcm.api.Page containingPage = pageManager.getContainingPage(resource);

            if (containingPage != null) {
                Iterator<com.day.cq.wcm.api.Page> iterator = containingPage.listChildren();
                while (iterator.hasNext()) {
                    com.day.cq.wcm.api.Page childPage = iterator.next();
                    Resource childResource = resourceResolver.getResource(childPage.getPath());
                    if (childResource != null) {
                        allReferences.add(childPage.getContentResource());
                        addReferencesForResource(page, childResource, allReferences, resourceResolver);
                    }
                }
            }
        }
    }
}
