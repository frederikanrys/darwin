package be.telenet.edev.aem.cpt.core.models.config.impl;

import be.telenet.edev.aem.cpt.api.config.Tag;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, adapters = Tag.class)
public class TagImpl extends PackageConfigItemImpl implements Tag {

    @Override
    public String toString() {
        return "TagImpl{} " + super.toString();
    }

}
