package be.telenet.edev.aem.cpt.core.models.settings.impl;

import be.telenet.edev.aem.cpt.api.settings.GlobalSettings;
import org.apache.sling.api.resource.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GlobalSettingsImpl implements GlobalSettings {

    private List<String> excludedPaths;

    private final Resource resource;

    GlobalSettingsImpl(Resource resource) {
        this.resource = resource;
    }

    @Override
    public List<String> getExcludedPaths() {
        if (excludedPaths == null) {
            excludedPaths = Arrays.asList(Optional.ofNullable(resource.getValueMap().get("excludedPaths", String[].class)).orElse(new String[0]));
        }
        return excludedPaths;
    }

}
