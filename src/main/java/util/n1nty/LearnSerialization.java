package util.n1nty;

import java.io.*;


class Inner implements Serializable {
    private int innerV;

    public Inner(int innerV) {
        this.innerV = innerV;
    }

    public int getInnerV() {
        return this.innerV;
    }

    private void readObject(ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        System.out.println("after inner defaultReadObject, "+this.innerV);
    }

}

class Parent implements Serializable {
    private int age = 100;

    public void setAge(int age) {
        this.age = age;
    }


    private void readObject(ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        System.out.println("after parent defaultReadObject");

        System.out.println("read again in parent.readobject");
        in.readObject();
        // System.out.println(in.readObject());
        // System.out.println(in.readUTF());
    }

    private void writeObject(ObjectOutputStream out) throws Exception {
        out.defaultWriteObject();
        out.writeObject(new Inner(199));
//		out.writeUTF("test write");
        //out.writeObject("test write");
    }

    public int getAge() {
        return this.age;
    }
}

class TestSer extends Parent implements Serializable {
    private String id;
    private Inner inner;

    public TestSer(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }


    public Inner getInner() {
        return this.inner;
    }

    private void readObject(ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        System.out.println("after TestSer defaultReadObject");
    }
}

public class LearnSerialization{
    public static void main(String[] args) throws Exception {

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/ser")));



        TestSer obj = new TestSer("id1");
        obj.setAge(-10);

        out.writeObject(obj);

        out.writeObject(new TestSer("id2"));

        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));

        System.out.println("read ser1");
        TestSer ser1 = (TestSer)in.readObject();

        System.out.println(ser1.getId());
        System.out.println(ser1.getAge());

        System.out.println();
        System.out.println("read ser2");
        TestSer ser2 = (TestSer)in.readObject();

        System.out.println(ser2.getId());
        System.out.println(ser2.getAge());
    }
}