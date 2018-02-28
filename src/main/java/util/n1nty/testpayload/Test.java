package util.n1nty.testpayload;

import util.n1nty.gen.Serialization;
import util.n1nty.gen.TCClassDesc;
import util.n1nty.gen.TCObject;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static java.io.ObjectStreamConstants.SC_SERIALIZABLE;
import static java.io.ObjectStreamConstants.SC_WRITE_METHOD;

public class Test {

    public static void test1() throws Exception {
        Passcode ins = new Passcode("root");
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/ser")));
        out.writeObject(ins);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
//        in.readObject();
        System.out.println(in.readObject());
    }


    public static void test2() throws Exception {
        Serialization ser = new Serialization();

        Passcode passcode = new Passcode("wrong passcode");

        TCClassDesc desc = new TCClassDesc("util.n1nty.testpayload.WrapperClass", (byte)(SC_SERIALIZABLE | SC_WRITE_METHOD));
        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(passcode);

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(desc, data, true);

        ser.addObject(obj);
        ser.addObject(passcode);

        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        in.readObject();
        System.out.println(in.readObject());
        // a
    }

    public static void test3() throws Exception {
        Serialization ser = new Serialization();
        Constructor ctor = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler").getDeclaredConstructor(Class.class, Map.class);
        ctor.setAccessible(true);

        Object handler = ctor.newInstance(String.class, new HashMap());


        TCClassDesc desc = new TCClassDesc("util.n1nty.testpayload.WrapperClass", (byte)(SC_SERIALIZABLE | SC_WRITE_METHOD));
        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(handler);

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(desc, data, true);

        ser.addObject(obj);
        ser.addObject(handler);

        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
//        in.readObject();
        System.out.println(in.readObject());
        System.out.println(in.readObject());

    }

    public static void main(String[] args) throws Exception {
        test2();
    }
}
