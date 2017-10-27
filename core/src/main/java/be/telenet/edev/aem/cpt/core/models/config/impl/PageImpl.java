package be.telenet.edev.aem.cpt.core.models.config.impl;

import be.telenet.edev.aem.cpt.api.config.Page;
import be.telenet.edev.aem.cpt.core.services.impl.PackageConfigReferenceProvider;
import be.telenet.edev.aem.cpt.api.settings.GlobalSettings;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Model(adaptables = Resource.class, adapters = Page.class)
public class PageImpl extends PackageConfigItemImpl implements Page {

    @Inject
    @Optional
    private Boolean subPages;

    @Inject
    @Optional
    private Boolean languageCopies;

    @Inject
    @Optional
    private Boolean liveCopies;

    @Inject
    @Optional
    private List<String> excludedPaths;

    @Inject
    private PackageConfigReferenceProvider packageConfigReferenceProvider;

    @Self
    private GlobalSettings globalSettings;

    private SortedSet<Resource> references;

    @Override
    public boolean getSubPages() {
        return BooleanUtils.isTrue(subPages);
    }

    @Override
    public boolean getLanguageCopies() {
        return BooleanUtils.isTrue(languageCopies);
    }

    @Override
    public boolean getLiveCopies() {
        return BooleanUtils.isTrue(liveCopies);
    }

    @Override
    public Resource getTargetResource() {
        if (subPages != null && subPages) {
            return super.getTargetResource();
        } else {
            // We only want to include the actual page if they don't want sub pages.
            return super.getTargetResource().getChild(JcrConstants.JCR_CONTENT);
        }
    }

    @Override
    public SortedSet<Resource> getReferences() {
        if (references == null) {
            references = filterReferences(packageConfigReferenceProvider.findReferencesOfPage(this));
        }
        return references;
    }

    private SortedSet<Resource> filterReferences(SortedSet<Resource> referencesOfPage) {
        SortedSet<Resource> result = referencesOfPage;
        if (excludedPaths != null) {
            // filtering the page excluded paths
            result = result.stream()
                    .filter(r -> excludedPaths.stream().noneMatch(excludedPath -> excludedPath.equals(r.getPath())))
                    .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Resource::getPath))));
        }
        // filtering the global settings excluded paths
        result = result.stream()
                .filter(r -> globalSettings.getExcludedPaths().stream().noneMatch(excludedPath -> excludedPath.equals(r.getPath())))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Resource::getPath))));

        return result;
    }

    @Override
    public List<String> getExcludedPaths() {
        return excludedPaths;
    }

    @Override
    public String toString() {
        return "PageImpl{" +
                "subPages=" + subPages +
                ", languageCopies=" + languageCopies +
                ", liveCopies=" + liveCopies +
                "} " + super.toString();
    }
}
