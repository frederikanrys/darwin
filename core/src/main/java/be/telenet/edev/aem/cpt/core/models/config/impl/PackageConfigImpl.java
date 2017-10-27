package be.telenet.edev.aem.cpt.core.models.config.impl;

import be.telenet.edev.aem.cpt.api.config.*;
import be.telenet.edev.aem.cpt.api.settings.GlobalSettings;
import be.telenet.edev.aem.cpt.core.services.impl.PackageConfigReferenceProvider;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Model(adaptables = Resource.class, adapters = PackageConfig.class)
public class PackageConfigImpl implements PackageConfig {

    @Inject
    @Named("jcr:created")
    @Optional
    private Calendar created;

    @Inject
    @Named("jcr:createdBy")
    @Optional
    private String createdBy;

    @Inject
    @Named("jcr:lastModified")
    @Optional
    private Calendar lastModified;

    @Inject
    @Named("jcr:lastModifiedBy")
    @Optional
    private String lastModifiedBy;

    @Inject
    @Named("jcr:title")
    private String title;

    @Inject
    @Optional
    private List<Page> pages;

    @Inject
    @Optional
    private List<Label> labels;

    @Inject
    @Optional
    private List<Tag> tags;

    @Inject
    private PackageConfigReferenceProvider packageConfigReferenceProvider;

    @Self
    private GlobalSettings globalSettings;

    @Self
    private Resource resource;

    private SortedSet<Resource> content;

    @Override
    public Calendar getCreated() {
        return created;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Calendar getLastModified() {
        return lastModified == null ? getCreated() : lastModified;
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy == null ? getCreatedBy() : lastModifiedBy;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return title;
    }

    @Nonnull
    @Override
    public Resource getResource() {
        return resource;
    }

    @Nonnull
    @Override
    public List<Page> getPages() {
        if (pages == null) {
            return new ArrayList<>();
        }
        return pages;
    }

    @Nonnull
    @Override
    public List<Label> getLabels() {
        if (labels == null) {
            return new ArrayList<>();
        }
        return labels;
    }

    @Nonnull
    @Override
    public List<Tag> getTags() {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags;
    }

    @Nonnull
    @Override
    public SortedSet<Resource> getContent() {
        if (content == null) {
            content = new TreeSet<>(Comparator.comparing(Resource::getPath));
            content.addAll(getPages().stream().map(PackageConfigItem::getTargetResource).filter(Objects::nonNull).collect(Collectors.toList()));
            content.addAll(getLabels().stream().map(PackageConfigItem::getTargetResource).filter(Objects::nonNull).collect(Collectors.toList()));
            content.addAll(getTags().stream().map(PackageConfigItem::getTargetResource).filter(Objects::nonNull).collect(Collectors.toList()));
            content.addAll(filterContent(packageConfigReferenceProvider.findReferences(this)));
        }
        return content;
    }

    private List<Resource> filterContent(Collection<Resource> content) {
        return content.stream()
                .filter(r -> globalSettings.getExcludedPaths().stream().noneMatch(excludedPath -> r.getPath().contains(excludedPath)))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "PackageConfigImpl{" +
                ", resource=" + resource +
                '}';
    }
}
