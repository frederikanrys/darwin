package be.telenet.edev.aem.cpt.core.servlets;

import be.telenet.edev.aem.cpt.api.PackageService;
import be.telenet.edev.aem.cpt.api.PackagingException;
import be.telenet.edev.aem.cpt.api.config.PackageConfig;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/content-package/build",
                SLING_SERVLET_METHODS + "=GET"
        }
)
public class PackageBuildServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(PackageBuildServlet.class);

    @Reference
    private PackageService packageService;

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            ResourceResolver resourceResolver = request.getResourceResolver();

            RequestParameter parameter = request.getRequestParameter("packageConfigPath");
            if (parameter == null) {
                LOG.warn("Could not find package config path parameter for request");
                response.setStatus(404);
                return;
            }

            String packageConfigPath = parameter.getString();
            Resource packageConfigResource = resourceResolver.getResource(packageConfigPath);
            if (packageConfigResource == null) {
                LOG.warn("Could not find the package config resource for path: {}", packageConfigPath);
                response.setStatus(404);
                return;
            }

            PackageConfig packageConfig = packageConfigResource.adaptTo(PackageConfig.class);
            if (packageConfig == null) {
                LOG.warn("Could not adapt existing package config resource to package config: {}", packageConfigPath);
                response.setStatus(500);
                return;
            }

            packageService.buildPackage(resourceResolver, packageConfig);
            response.setStatus(200);
            LOG.debug("Exported package config: {}", packageConfig);
        } catch (PackagingException e) {
            LOG.error("Could not export package", e);
            response.getWriter().write(e.getMessage());
            response.getWriter().close();
            response.setStatus(500);
        }
    }

}
