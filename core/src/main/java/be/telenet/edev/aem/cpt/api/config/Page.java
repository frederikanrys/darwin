package be.telenet.edev.aem.cpt.api.config;

import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.SortedSet;

public interface Page extends PackageConfigItem {

    boolean getSubPages();

    boolean getLanguageCopies();

    boolean getLiveCopies();

    SortedSet<Resource> getReferences();

    List<String> getExcludedPaths();
}
