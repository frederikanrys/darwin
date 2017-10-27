package be.telenet.edev.aem.cpt.core.models.config.impl;

import be.telenet.edev.aem.cpt.api.config.Label;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.inject.Inject;

@Model(adaptables = Resource.class, adapters = Label.class)
public class LabelImpl extends PackageConfigItemImpl implements Label {

    @Inject
    @Optional
    private Boolean languageCopies;

    @Inject
    @Optional
    private Boolean liveCopies;

    @Override
    public boolean getLanguageCopies() {
        return BooleanUtils.isTrue(languageCopies);
    }

    @Override
    public boolean getLiveCopies() {
        return BooleanUtils.isTrue(liveCopies);
    }

    @Override
    public String toString() {
        return "LabelImpl{" +
                "languageCopies=" + languageCopies +
                ", liveCopies=" + liveCopies +
                "} " + super.toString();
    }
}
