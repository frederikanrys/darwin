package be.telenet.edev.aem.cpt.core.models.settings.impl;


import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;


@Component(service = AdapterFactory.class,
        property = {
                AdapterFactory.ADAPTER_CLASSES + "=GlobalSettings",
                AdapterFactory.ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.Resource"
        })
public class GlobalSettingsAdapterFactory implements AdapterFactory {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalSettingsAdapterFactory.class);

    public static final String GLOBAL_SETTINGS_PATH = "/etc/content-packages/settings";

    @Override
    public final <AdapterType> AdapterType getAdapter(@Nonnull Object adaptable, @Nonnull Class<AdapterType> type) {

        // Ensure the adaptable object is of an appropriate type
        if (!(adaptable instanceof Resource)) {
            LOG.warn("Always log when a object cannot be adapted.");
            return null;
        }

        ResourceResolver resourceResolver = ((Resource) adaptable).getResourceResolver();
        Resource resource = resourceResolver.getResource(GLOBAL_SETTINGS_PATH);
        if (resource == null) {
            LOG.warn("Could not get settings resource of content packages tool.");
            return null;
        }

        return (AdapterType) new GlobalSettingsImpl(resource);
    }
}
