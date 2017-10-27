package be.telenet.edev.aem.cpt.packages.services.impl;

import be.telenet.edev.aem.cpt.api.PackageService;
import be.telenet.edev.aem.cpt.api.PackagingContext;
import be.telenet.edev.aem.cpt.api.config.PackageConfig;
import be.telenet.edev.aem.cpt.api.exporter.PackageExporter;
import be.telenet.edev.aem.cpt.core.services.impl.PackageServiceImpl;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PackageServiceImplTest {

    @Rule
    public AemContext context = new AemContext();

    private ResourceResolver resourceResolver;
    private PackageService packageService;

    @Mock
    private PackageExporter packageExporterRankingMinusOne;
    @Mock
    private PackageExporter packageExporterRankingZero;
    @Mock
    private PackageExporter packageExporterRankingOne;

    @Mock
    private JcrPackageManager jcrPackageManager;

    @Mock
    private JcrPackage jcrPackage;

    private static final String PACKAGE_GROUP = "content-package";

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        resourceResolver = context.resourceResolver();

        // Mocking the jcr package manager
        Packaging packaging = mock(Packaging.class);
        context.registerService(Packaging.class, packaging);
        when(packaging.getPackageManager(resourceResolver.adaptTo(Session.class))).thenReturn(jcrPackageManager);
        Node packages = mock(Node.class);
        when(packages.hasNode(any(String.class))).thenReturn(false);
        when(jcrPackageManager.getPackageRoot()).thenReturn(packages);


        // Mocking the jcr package
        when(jcrPackageManager.create(any(String.class), any(String.class), any(String.class))).then(invocation -> {
            Node node = mock(Node.class);
            when(jcrPackage.getNode()).thenReturn(node);
            when(node.getName()).thenReturn((String) invocation.getArguments()[1]);
            when(node.getPath()).thenReturn((String) invocation.getArguments()[0] + "/" + invocation.getArguments()[1]);
            return jcrPackage;
        });
        Node packageNode = mock(Node.class);
        when(jcrPackage.getNode()).thenReturn(packageNode);
        when(packageNode.getPath()).thenReturn("/etc/packages/path");

        // register the exporters
        context.registerService(PackageExporter.class, packageExporterRankingMinusOne, map("service.ranking", "-1"));
        context.registerService(PackageExporter.class, packageExporterRankingZero, map("service.ranking", "0"));
        context.registerService(PackageExporter.class, packageExporterRankingOne, map("service.ranking", "1"));


        packageService = context.registerInjectActivateService(new PackageServiceImpl(), map("package.group.name", PACKAGE_GROUP));
    }

    @Test
    public void testExportPackageHappensInCorrectOrder() throws Exception {
        PackageConfig packageConfig = mock(PackageConfig.class);
        InOrder inOrder = inOrder(packageExporterRankingMinusOne, packageExporterRankingZero, packageExporterRankingOne);

        packageService.buildPackage(resourceResolver, packageConfig);
        verify(packageExporterRankingMinusOne, times(1)).execute(any(PackagingContext.class), any(ResourceResolver.class));
        verify(packageExporterRankingZero, times(1)).execute(any(PackagingContext.class), any(ResourceResolver.class));
        verify(packageExporterRankingOne, times(1)).execute(any(PackagingContext.class), any(ResourceResolver.class));
        inOrder.verify(packageExporterRankingMinusOne).execute(any(PackagingContext.class), any(ResourceResolver.class));
        inOrder.verify(packageExporterRankingZero).execute(any(PackagingContext.class), any(ResourceResolver.class));
        inOrder.verify(packageExporterRankingOne).execute(any(PackagingContext.class), any(ResourceResolver.class));
    }

    @Test
    public void testExportPackageCreatesCorrectName() throws Exception {
        PackageConfig packageConfig = mock(PackageConfig.class);
        String packageName = "package";
        when(packageConfig.getTitle()).thenReturn(packageName);

        JcrPackage jcrPackage = packageService.buildPackage(resourceResolver, packageConfig);
        assertEquals(jcrPackage.getNode().getName(), packageName + ".zip");
        assertEquals(jcrPackage.getNode().getPath(), PACKAGE_GROUP + "/" + packageName + ".zip");
    }


    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> map(Object... args) {
        Map<K, V> res = new HashMap<>();
        K key = null;
        for (Object arg : args) {
            if (key == null) {
                key = (K) arg;
            } else {
                res.put(key, (V) arg);
                key = null;
            }
        }
        return res;
    }
}