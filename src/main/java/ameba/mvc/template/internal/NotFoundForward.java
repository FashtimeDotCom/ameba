package ameba.mvc.template.internal;

import ameba.mvc.template.TemplateException;
import groovy.lang.Singleton;
import org.glassfish.jersey.server.mvc.Viewable;
import org.glassfish.jersey.server.mvc.spi.AbstractTemplateProcessor;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;
import org.jvnet.hk2.annotations.Optional;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.OutputStream;
import java.io.Reader;
import java.text.ParseException;

/**
 * 404 跳转到模板
 *
 * @author 张立鑫 IntelligentCode
 * @since 2013-08-27
 */
@Provider
@Singleton
public class NotFoundForward implements ExtendedExceptionMapper<NotFoundException> {

    @Inject
    private javax.inject.Provider<UriInfo> uriInfo;

    private AbstractTemplateProcessor<Boolean> templateProcessor;
    private ThreadLocal<String> templatePath = new ThreadLocal<String>();

    @Inject
    public NotFoundForward(final Configuration config, @Optional final ServletContext servletContext) {
        this.templateProcessor = new AmebaTemplateProcessor<Boolean>(config, servletContext, HttlViewProcessor.CONFIG_SUFFIX, HttlViewProcessor.getExtends(config)) {

            @Override
            protected TemplateException createException(ParseException e) {
                return null;
            }

            @Override
            protected Boolean resolve(String templatePath) throws Exception {
                return true;
            }

            @Override
            protected Boolean resolve(Reader reader) throws Exception {
                return true;
            }

            @Override
            public String getTemplateFile(Boolean templateReference) {
                return null;
            }

            @Override
            public void writeTemplate(Boolean templateReference, Viewable viewable, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws Exception {

            }
        };
    }

    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.ok(Viewables.newDefaultViewable(templatePath.get())).build();
    }

    private String getCurrentPath() {
        return "/" + uriInfo.get().getPath();
    }

    @Override
    public boolean isMappable(NotFoundException exception) {
        String path = getCurrentPath();
        //受保护目录,不允许直接访问
        if (path.startsWith(AmebaTemplateProcessor.PROTECTED_DIR)) return false;
        try {
            Boolean has = templateProcessor.resolve(path, (MediaType) null);
            if (has == null || !has) {
                path = path + "/index";
                has = templateProcessor.resolve(path, (MediaType) null);
            }
            templatePath.set(path);
            return has != null && has;
        } catch (Exception e) {
            return false;
        }
    }
}