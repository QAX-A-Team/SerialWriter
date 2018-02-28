package util.n1nty.gen;

import java.io.*;
import java.util.Date;
import java.util.HashMap;

import static java.io.ObjectStreamConstants.SC_SERIALIZABLE;
import static java.io.ObjectStreamConstants.SC_WRITE_METHOD;

public class Test2 {

    public static class Dummy implements Serializable {
        private static final long serialVersionUID = 400L;
        private void readObject(ObjectInputStream input) {
            System.out.println("dummy.readObject");
        }
    }

    public static class Parent implements  Serializable {

        private static final long serialVersionUID = 100L;

        public String parentName;
        public int parentAge;

        private void readObjectNoData() throws ObjectStreamException {
            System.out.println("no data");
        }

        @Override
        public String toString() {
            return "Parent{" +
                    "parentName='" + parentName + '\'' +
                    ", parentAge=" + parentAge +
                    '}';
        }
    }

    public static class Child extends Parent implements Serializable {
        private static final long serialVersionUID = 200L;
        public String childName;
        public int childAge;
    }

    public static void testNoParentData() throws Exception {
        Serialization ser = new Serialization();

        TCClassDesc desc = new TCClassDesc("util.n1nty.gen.Test2$Child", (byte)(SC_SERIALIZABLE | SC_WRITE_METHOD));
        desc.addField(new TCClassDesc.Field("childAge", int.class));
        desc.addField(new TCClassDesc.Field("childName", String.class));

        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(18);
        data.addData("n1nty");

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(desc, data);


        ser.addObject(obj);
        ser.write(new FileOutputStream(new File("/tmp/ser")));

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        Child c = (Child) in.readObject();
        System.out.println(c.childAge);
        System.out.println(c.childName);
    }

    public static void testWithParentData() throws Exception {

        Serialization ser = new Serialization();

        TCClassDesc childDesc = new TCClassDesc("util.n1nty.gen.Test2$Child", (byte)(SC_SERIALIZABLE | SC_WRITE_METHOD));
        childDesc.addField(new TCClassDesc.Field("childAge", int.class));
        childDesc.addField(new TCClassDesc.Field("childName", String.class));

        TCObject.ObjectData childData = new TCObject.ObjectData();
        childData.addData(18);
        childData.addData("n1nty");

        TCClassDesc parentDesc = new TCClassDesc("util.n1nty.gen.Test2$Parent");
        parentDesc.addField(new TCClassDesc.Field("parentAge", int.class));
        parentDesc.addField(new TCClassDesc.Field("parentName", String.class));

        TCObject.ObjectData parentData = new TCObject.ObjectData();
        parentData.addData(180);
        parentData.addData("n1nty-parent");

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(childDesc, childData);
        obj.addClassDescData(parentDesc, parentData);



        ser.addObject(obj);
        ser.write(new FileOutputStream(new File("/tmp/ser")));

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        Child c = (Child) in.readObject();
        System.out.println(c.childAge);
        System.out.println(c.childName);

        Parent p = c;
        System.out.println(p.parentAge);
        System.out.println(p.parentName);
    }

    public static TCObject makeDummyTCObject(Serialization ser) throws Exception {
        TCClassDesc desc = new TCClassDesc("util.n1nty.gen.Test2$Dummy");
        TCObject.ObjectData data = new TCObject.ObjectData();

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(desc, data);

        return obj;
    }

    public static void testDummyField() throws Exception {
        Serialization ser = new Serialization();

        TCClassDesc desc = new TCClassDesc("util.n1nty.gen.Test2$Parent");

        desc.addField(new TCClassDesc.Field("parentAge", int.class));
        desc.addField(new TCClassDesc.Field("parentName", String.class));
        desc.addField(new TCClassDesc.Field("dummy", Dummy.class)); // 假成员

        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(18);
        data.addData("n1nty");
        data.addData(makeDummyTCObject(ser)); // 设置假成员的值

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(desc, data);


        ser.addObject(obj);

        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        Parent p = (Parent) in.readObject();
        System.out.println(p);

    }


    public static void testFakeWriteMethod() throws Exception {
        Serialization ser = new Serialization();

        TCClassDesc desc = new TCClassDesc("util.n1nty.gen.Test2$Parent", (byte)(SC_SERIALIZABLE | SC_WRITE_METHOD));
        desc.addField(new TCClassDesc.Field("parentAge", int.class));
        desc.addField(new TCClassDesc.Field("parentName", String.class));

        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(18);
        data.addData("n1nty");
        data.addData(makeDummyTCObject(ser)); // 直接插入 dummy object，而不是做为一个假的成员插入，SC_WRITE_METHOD 标记在这里起到了重要的作用

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(desc, data, true);


        ser.addObject(obj);

        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        Parent p = (Parent) in.readObject();
        System.out.println(p);
//        System.out.println(in.readObject());

    }

    public static void testMultipleObjects() throws Exception {
        Serialization ser = new Serialization();

        ser.addObject(40, true);
        ser.addObject(100, true);

        ser.addObject("wofffffff");
        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        System.out.println(in.readInt());
        System.out.println(in.readInt());
        System.out.println(in.readObject());

    }

    public static void testMultipleObject2() throws Exception {
        Serialization ser = new Serialization();


        ser.addObject(new Date());
        ser.addObject(new HashMap());

        ser.addObject(11, true);
        ser.addObject("wokkkkkk", true);
        ser.addObject("ha".toCharArray(), true);

        ser.addObject(new Date());
        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        System.out.println(in.readObject()); // date
        System.out.println(in.readObject()); // hashmap
        System.out.println(in.readInt()); // 11
        System.out.println(in.readUTF()); // wokkkkkk


        System.out.println(in.readChar()); // h
        System.out.println(in.readChar()); // a

        System.out.println(in.readObject()); // date
    }


    public static void main(String[] args) throws Exception {
//
        testNoParentData();
        System.out.println("-----------------");
        testWithParentData();
//
        testDummyField();
//
        testFakeWriteMethod();
//
//
        testMultipleObjects();
        testMultipleObject2();
    }

}
