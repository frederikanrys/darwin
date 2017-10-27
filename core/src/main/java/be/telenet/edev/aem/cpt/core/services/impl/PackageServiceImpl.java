package be.telenet.edev.aem.cpt.core.services.impl;

import be.telenet.edev.aem.cpt.api.PackageService;
import be.telenet.edev.aem.cpt.api.PackagingContext;
import be.telenet.edev.aem.cpt.api.PackagingException;
import be.telenet.edev.aem.cpt.api.config.PackageConfig;
import be.telenet.edev.aem.cpt.api.exporter.PackageExporter;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@Component
@Designate(ocd = PackageServiceImpl.Config.class)
public class PackageServiceImpl implements PackageService {

    private static final Logger LOG = LoggerFactory.getLogger(PackageServiceImpl.class);

    private static final Object EXPORTER_LOCK = new Object();
    private static final Object IMPORTER_LOCK = new Object();

    @ObjectClassDefinition(name = "Author content package service")
    @interface Config {

        @AttributeDefinition(description = "Package group name to be used when creating a content package")
        String package_group_name() default "content-packages";
    }

    private Map<Integer, PackageExporter> exporterMap = new TreeMap<>();

    @Reference
    private Packaging packaging;

    private Config config;

    @Activate
    protected void activate(Config config) {
        this.config = config;
    }

    @Nonnull
    @Override
    public JcrPackage buildPackage(@Nonnull ResourceResolver resourceResolver, @Nonnull PackageConfig packageConfig) throws PackagingException {
        try {
            LOG.debug("Starting export package for package config: {}", packageConfig);
            JcrPackageManager jcrPackageManager = packaging.getPackageManager(resourceResolver.adaptTo(Session.class));
            PackagingContext packagingContext = createPackagingContext(resourceResolver, jcrPackageManager, packageConfig);

            for (PackageExporter packageExporter : exporterMap.values()) {
                LOG.trace("Using package exporter: {} for package config: {}", packageExporter, packageConfig);
                packageExporter.execute(packagingContext, resourceResolver);
            }

            assemblePackage(jcrPackageManager, packagingContext);
            LOG.debug("Assembled package for package config: {}", packageConfig);

            return packagingContext.getJcrPackage();
        } catch (RepositoryException | IOException e) {
            LOG.error("Could not create packaging context", e);
            throw new PackagingException("Could not create packaging context for package config: " + packageConfig);
        }
    }

    private void assemblePackage(JcrPackageManager jcrPackageManager, PackagingContext packagingContext) throws PackagingException {
        try {
            //TODO add a listener to get a progress of the build (see XMLEscapingProgressListener)
            jcrPackageManager.assemble(packagingContext.getJcrPackage(), null);
        } catch (PackageException | RepositoryException | IOException e) {
            LOG.error("Could not assemble package for package config: {}", packagingContext.getPackageConfig());
            throw new PackagingException("Could not assemble package", e);
        }
    }

    private PackagingContext createPackagingContext(ResourceResolver resourceResolver, JcrPackageManager jcrPackageManager, PackageConfig packageConfig) throws RepositoryException, IOException {
        JcrPackage jcrPackage = getJcrPackage(resourceResolver, jcrPackageManager, packageConfig);
        PackagingContext packagingContext = new PackagingContextImpl(packageConfig, jcrPackage);
        LOG.debug("Created package context for package config: {}, jcr package: {}", packageConfig.getTitle(), jcrPackage.getNode().getPath());
        return packagingContext;
    }

    private JcrPackage getJcrPackage(ResourceResolver resourceResolver, JcrPackageManager jcrPackageManager, PackageConfig packageConfig) throws RepositoryException, IOException {
        String packageName = packageConfig.getTitle() + ".zip";
        String packagePath = config.package_group_name() + "/" + packageName;
        LOG.trace("Package path: {}", packagePath);
        Node packages = jcrPackageManager.getPackageRoot();
        if (packages.hasNode(packagePath)) {
            packages.getNode(packagePath).remove();
            resourceResolver.commit();
        }
        return jcrPackageManager.create(config.package_group_name(), packageName, null);
    }

    @SuppressWarnings("unused")
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected void bindPackageExporter(final PackageExporter packageExporter, Map<String, Object> props) {
        synchronized (EXPORTER_LOCK) {
            Object prop = props.get(org.osgi.framework.Constants.SERVICE_RANKING);
            if (prop != null) {
                Integer ranking = getInteger(prop, packageExporter);
                if (ranking != null) {
                    LOG.info("Adding package exporter service: {} with ranking: {}", packageExporter, ranking);
                    exporterMap.put(ranking, packageExporter);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    protected void unbindPackageExporter(final PackageExporter packageExporter, Map<String, Object> props) {
        synchronized (EXPORTER_LOCK) {
            Object prop = props.get(org.osgi.framework.Constants.SERVICE_RANKING);
            if (prop != null) {
                Integer ranking = getInteger(prop, packageExporter);
                if (ranking != null) {
                    exporterMap.remove(ranking, packageExporter);
                    LOG.info("Removing package exporter service: {} with ranking: {}", packageExporter, ranking);
                }
            }
        }
    }


    private Integer getInteger(Object prop, Object service) {
        if (prop instanceof Integer) {
            return (Integer) prop;
        } else if (prop instanceof String) {
            try {
                return Integer.parseInt((String) prop);
            } catch (NumberFormatException e) {
                LOG.warn("Could not parse service rank property of service :{} ", service);
            }
        }
        return null;
    }

}
