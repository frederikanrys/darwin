package be.telenet.edev.aem.cpt.api.packages;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.List;

public interface Package {

    @Nonnull
    String getName();

    @Nonnull
    String getVersionConstraints();

    @CheckForNull
    Calendar getLastModified();

    @CheckForNull
    String getLastModifiedBy();

    long getLastModifiedAsLong();

    @Nonnull
    List<String> getContent();
}
