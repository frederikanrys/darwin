package be.telenet.edev.aem.cpt.api.config;

import org.apache.sling.api.resource.Resource;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;

public interface PackageConfig {

    @CheckForNull
    Calendar getCreated();

    @CheckForNull
    String getCreatedBy();

    @CheckForNull
    Calendar getLastModified();

    @CheckForNull
    String getLastModifiedBy();

    @Nonnull
    String getTitle();

    @Nonnull
    Resource getResource();

    @Nonnull
    List<Page> getPages();

    @Nonnull
    List<Label> getLabels();

    @Nonnull
    List<Tag> getTags();

    @Nonnull
    SortedSet<Resource> getContent();

}
