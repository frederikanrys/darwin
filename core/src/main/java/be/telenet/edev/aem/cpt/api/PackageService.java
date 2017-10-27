package be.telenet.edev.aem.cpt.api;

import be.telenet.edev.aem.cpt.api.config.PackageConfig;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.annotation.versioning.ProviderType;

import javax.annotation.Nonnull;

@ProviderType
public interface PackageService {

    @Nonnull
    JcrPackage buildPackage(@Nonnull ResourceResolver resourceResolver, @Nonnull PackageConfig packageConfig) throws PackagingException;
}
