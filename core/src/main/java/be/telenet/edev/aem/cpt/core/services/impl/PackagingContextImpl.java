package be.telenet.edev.aem.cpt.core.services.impl;

import be.telenet.edev.aem.cpt.api.PackagingContext;
import be.telenet.edev.aem.cpt.api.config.PackageConfig;
import org.apache.jackrabbit.vault.packaging.JcrPackage;

import javax.annotation.Nonnull;

public class PackagingContextImpl implements PackagingContext {

    private final PackageConfig packageConfig;
    private final JcrPackage jcrPackage;

    PackagingContextImpl(PackageConfig packageConfig, JcrPackage jcrPackage) {
        this.packageConfig = packageConfig;
        this.jcrPackage = jcrPackage;
    }

    @Nonnull
    @Override
    public JcrPackage getJcrPackage() {
        return jcrPackage;
    }

    @Nonnull
    @Override
    public PackageConfig getPackageConfig() {
        return packageConfig;
    }


}
