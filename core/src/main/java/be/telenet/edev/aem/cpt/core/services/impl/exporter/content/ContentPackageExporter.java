package be.telenet.edev.aem.cpt.core.services.impl.exporter.content;

import be.telenet.edev.aem.cpt.api.PackagingContext;
import be.telenet.edev.aem.cpt.api.config.PackageConfig;
import be.telenet.edev.aem.cpt.api.exporter.PackageExporter;
import be.telenet.edev.aem.cpt.api.exporter.PackagingExportException;
import org.apache.jackrabbit.vault.fs.api.PathFilterSet;
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageDefinition;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@Component(
        property = {
                org.osgi.framework.Constants.SERVICE_RANKING + "=0"
        }
)
public class ContentPackageExporter implements PackageExporter {

    private static final Logger LOG = LoggerFactory.getLogger(ContentPackageExporter.class);

    @Override
    public void execute(@Nonnull PackagingContext packagingContext, @Nonnull ResourceResolver resourceResolver) throws PackagingExportException {
        try {
            LOG.debug("Starting content package exporter for {}", packagingContext.getJcrPackage().getNode().getPath());
            PackageConfig packageConfig = packagingContext.getPackageConfig();
            JcrPackage jcrPackage = packagingContext.getJcrPackage();
            DefaultWorkspaceFilter filters = getDefaultWorkspaceFilter(packageConfig);
            JcrPackageDefinition jcrPackageDefinition = jcrPackage.getDefinition();
            jcrPackageDefinition.setFilter(filters, true);
            LOG.debug("Finished activation package exporter {}", packagingContext.getJcrPackage().getNode().getPath());
        } catch (Exception e) {
            throw new PackagingExportException("Exception occured while trying to make a package", e);
        }
    }


    private DefaultWorkspaceFilter getDefaultWorkspaceFilter(PackageConfig packageConfig) {
        LOG.trace("Adding filters...");
        DefaultWorkspaceFilter filters = new DefaultWorkspaceFilter();
        boolean nonExisting = false;
        for (Resource resource : packageConfig.getContent()) {
            if (resource != null) {
                filters.add(new PathFilterSet(resource.getPath()));
                LOG.trace("Adding Path Filter Set: {}", resource.getPath());
            } else {
                nonExisting = true;
            }
        }
        if (nonExisting) {
            LOG.debug("Package config {} contained paths that do not exist in the repository", packageConfig);
        }
        return filters;
    }
}
