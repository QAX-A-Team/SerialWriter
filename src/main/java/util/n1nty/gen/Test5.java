package util.n1nty.gen;

import java.io.*;
import java.util.Date;

public class Test5 {

    public static class Child implements Serializable {
        private void writeObject(ObjectOutputStream out) throws Exception {
            out.defaultWriteObject();
            out.writeObject(new Date());
            out.writeInt(0);
            out.writeByte(100);
            out.writeChar('1');
            out.writeChars("23");
            out.writeUTF("test");
        }

        private void readObject(ObjectInputStream in) throws Exception {
            in.defaultReadObject();
            in.readObject(); // date
            System.out.println(in.readInt()); // 0
            System.out.println(in.readByte()); //  100
            System.out.println(in.readChar()); // 1
            System.out.println(in.readChar()); // 2
            System.out.println(in.readChar()); // 3

            System.out.println(in.readUTF());
        }
    }



    public static void main(String[] args) throws Exception {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/ser")));
        out.writeObject(null);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
        in.readObject();




//        System.out.println(in.readObject());
    }
}
