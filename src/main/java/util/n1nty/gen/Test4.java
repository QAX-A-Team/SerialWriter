package util.n1nty.gen;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class Test4 {

    public static class Parent implements Serializable {
        protected int parentAge;
        private static final long serialVersionUID = 100L;
    }

    public static  class Child extends Parent implements  Serializable {
        private int childAge;
        private static final long serialVersionUID = 200L;

        @Override
        public String toString() {
            return "Child{" +
                    "parentAge=" + parentAge +
                    ", childAge=" + childAge +
                    '}';
        }
    }

    public static class Child2 extends Parent implements Serializable {
        private String name;
        private static final long serialVersionUID = 300L;


        @Override
        public String toString() {
            return "Child2{" +
                    "parentAge=" + parentAge +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


    public static void test1() throws Exception {
        Serialization ser = new Serialization();

        TCClassDesc childDesc = new TCClassDesc("util.n1nty.gen.Test4$Child");
        childDesc.addField(new TCClassDesc.Field("childAge", int.class));
        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(10);

        TCClassDesc parentDesc = new TCClassDesc("util.n1nty.gen.Test4$Parent");
        parentDesc.addField(new TCClassDesc.Field("parentAge", int.class));
        TCObject.ObjectData pdata = new TCObject.ObjectData();
        pdata.addData(30);

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(childDesc, data);
        obj.addClassDescData(parentDesc, pdata);

        ser.addObject(obj);


        TCClassDesc child2Desc = new TCClassDesc("util.n1nty.gen.Test4$Child2");
        TCObject.ObjectData child2Data = new TCObject.ObjectData();
        TCObject obj2 = new TCObject(ser);
        obj2.addClassDescData(child2Desc, child2Data);
        obj2.addClassDescData(parentDesc, pdata);

        ser.addObject(obj2);

        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        Child c = (Child) in.readObject();
        Child2 c2 = (Child2) in.readObject();

        System.out.println(c);
        System.out.println(c2);

    }

    public static void test2() throws Exception {
        Serialization ser = new Serialization();

        TCClassDesc childDesc = new TCClassDesc("util.n1nty.gen.Test4$Child");
        childDesc.addField(new TCClassDesc.Field("childAge", int.class));
        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(10);

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(childDesc, data);

        ser.addObject(obj);
        ser.addObject(obj);



        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));


        System.out.println(in.readObject() == in.readObject());
    }

    private static class xx implements  Serializable {
        private static final long serialVersionUID = 300L;

        private void readObject(ObjectInputStream in) throws Exception {
            in.defaultReadObject();
            System.out.println("xx.readObject");
        }
    }
    private static class x implements  Serializable {
        private static final long serialVersionUID = 100L;

        private void writeObject(ObjectOutputStream out) throws Exception {
            out.defaultWriteObject();
            out.writeObject(new xx());
        }

        private void readObject(ObjectInputStream in) throws Exception {
            in.defaultReadObject();

            System.out.println("x.readobject 退出");
        }
    }

    public static void test3() throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/ser")));
        out.writeObject(new x());
        out.close();
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        System.out.println(in.readObject());
    }

    private static class Handler implements InvocationHandler, Serializable {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//            System.out.println(proxy);
            System.out.println(method);
            return 1;
        }
    }
    public static void test4() throws Exception {

        Serialization ser = new Serialization();

        TCObject obj = new TCObject(ser);

        TCProxyClassDesc proxyClassDesc = new TCProxyClassDesc();
        proxyClassDesc.addInterface(Map.class);
        proxyClassDesc.addInterface(Comparator.class);

        obj.addClassDescData(proxyClassDesc, new TCObject.ObjectData());

        TCClassDesc desc = new TCClassDesc("java.lang.reflect.Proxy");
        desc.addField(new TCClassDesc.Field("h", InvocationHandler.class));
        obj.addClassDescData(desc, new TCObject.ObjectData().addData(new Handler()));


        ser.addObject(obj);

        ser.write("/tmp/ser");




//        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/ser")));
//        out.writeObject(Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Map.class, Comparator.class}, new Handler()));
//        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));

        Comparator c = (Comparator) in.readObject();
        System.out.println(c.compare(null, null));

    }

    private static class test5C implements Serializable {
        private Date date;

        public test5C(Date date) {
            this.date = date;
        }

        private void writeObject(ObjectOutputStream out) throws Exception {
            out.defaultWriteObject();
//            out.writeObject(this.date);
        }
    }
    public static void test5() throws Exception {
        Date d = new Date();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/ser")));
        out.writeObject(d);
        out.writeObject(new test5C(d));
        out.close();
    }

    public static void main(String[] args) throws Exception {
//        test1();
//        test2();
//        test3();
//        test4();
        test5();
    }
}
