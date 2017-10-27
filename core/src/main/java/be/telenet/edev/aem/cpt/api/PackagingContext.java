package be.telenet.edev.aem.cpt.api;

import be.telenet.edev.aem.cpt.api.config.PackageConfig;
import org.apache.jackrabbit.vault.packaging.JcrPackage;

import javax.annotation.Nonnull;

public interface PackagingContext {

    @Nonnull
    JcrPackage getJcrPackage();

    @Nonnull
    PackageConfig getPackageConfig();

}
