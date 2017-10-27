package be.telenet.edev.aem.cpt.core.models.config.impl;


import be.telenet.edev.aem.cpt.api.config.PackageConfigItem;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Model(adaptables = Resource.class, adapters = PackageConfigItem.class)
public class PackageConfigItemImpl implements PackageConfigItem {

    @Inject
    private String path;

    @Inject
    @Optional
    private Boolean activate;

    @Self
    private Resource resource;

    @Nonnull
    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public Resource getTargetResource() {
        return resource.getResourceResolver().getResource(path);
    }

    @Nonnull
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean getActivate() {
        return BooleanUtils.isTrue(activate);
    }

    @Override
    public String toString() {
        return "PackageConfigItemImpl{" +
                "resource=" + resource +
                ", path='" + path + '\'' +
                ", activate=" + activate +
                '}';
    }
}
