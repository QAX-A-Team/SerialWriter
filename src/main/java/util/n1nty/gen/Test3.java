package util.n1nty.gen;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static java.io.ObjectStreamConstants.SC_SERIALIZABLE;
import static java.io.ObjectStreamConstants.SC_WRITE_METHOD;

public class Test3 {

    public static class Child implements Serializable {
        private static final long serialVersionUID = 101L;
        private int age = 10;
        private String addr;

        private void readObject(ObjectInputStream input) throws Exception {
            input.defaultReadObject();
            System.out.println(input.readInt());

            Map m1 = (Map) input.readObject();
            Map m2 = (Map) input.readObject();

            Map m3 = (Map) input.readObject();

            System.out.println(m1 == m2);
            System.out.println(m1);

            System.out.println(m3);
        }

        @Override
        public String toString() {
            return "Child{" +
                    "age=" + age +
                    ", addr='" + addr + '\'' +
                    '}';
        }
    }

    public static void testCustomReadObject() throws Exception {
        Serialization ser = new Serialization();

        TCClassDesc desc = new TCClassDesc("util.n1nty.gen.Test3$Child", (byte)(SC_SERIALIZABLE | SC_WRITE_METHOD));

        desc.addField(new TCClassDesc.Field("age", int.class));
        desc.addField(new TCClassDesc.Field("addr", String.class));

        TCObject.ObjectData data = new TCObject.ObjectData();
        data.addData(101);
        data.addData("addr1");

        data.addData(99, true);

        Map map = new HashMap();
        map.put("1","1");
        map.put("2", "2");
        data.addData(map);
        data.addData(map);

        Map map2 = new HashMap();
        map2.put("1", "1haha");
        map2.put("2", "2haha");
        data.addData(map2);

        TCObject obj = new TCObject(ser);
        obj.addClassDescData(desc, data, true);
        ser.addObject(obj);


        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
//        in.readObject();
        System.out.println(in.readObject());
    }

    public static void testMap() throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/sser")));
        Map map = new HashMap();
        map.put("1","1");
        map.put("2", "2");
        out.writeObject(map);
        out.writeObject(map);
        out.close();
    }

//
//    public static void testMap2() throws Exception {
//        Serialization ser = new Serialization();
//        Map map = new HashMap();
//        map.put("1","1");
//        map.put("2", "2");
//        ser.addObject();
//        ser.addObject(map);
//
//        ser.write("/tmp/ser");
//
//        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
//        in.readObject();
//        System.out.println(in.readObject());
//    }
    public static void main(String[] args) throws Exception {

        testMap();
        testCustomReadObject();
    }
}
