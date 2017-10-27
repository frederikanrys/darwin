package be.telenet.edev.aem.cpt.api.exporter;

import be.telenet.edev.aem.cpt.api.PackagingContext;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.annotation.versioning.ConsumerType;

import javax.annotation.Nonnull;


/**
 * Package exporter. The exporters are sorted by the service.ranking specified by the component.
 * A default exporter with ranking 0 is provided by the core bundle
 */
@ConsumerType
public interface PackageExporter {

    /**
     * Executes the package exporter implementation
     */
    void execute(@Nonnull PackagingContext packagingContext, @Nonnull ResourceResolver resourceResolver) throws PackagingExportException;

}

