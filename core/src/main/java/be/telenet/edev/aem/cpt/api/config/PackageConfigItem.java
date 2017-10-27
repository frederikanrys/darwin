package be.telenet.edev.aem.cpt.api.config;

import org.apache.sling.api.resource.Resource;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface PackageConfigItem {

    @Nonnull
    Resource getResource();

    @Nonnull
    String getPath();

    @CheckForNull
    Resource getTargetResource();

    boolean getActivate();

}
