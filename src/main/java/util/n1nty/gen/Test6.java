package util.n1nty.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Test6 {

    private static class a implements Serializable {
        private static final long serialVersionUID = 200L;

        private void readObject(ObjectInputStream input) {
            System.out.println("a.readObject");
        }
    }
    private static class xx implements Serializable {
        private static final long serialVersionUID = 100L;
    }
    public static void main(String[] args) throws Exception {

        Serialization ser = new Serialization();
        TCObject obj = new TCObject(ser);

        TCClassDesc desc = new TCClassDesc("util.n1nty.gen.Test6$xx");
        desc.addField(new TCClassDesc.Field("fake", a.class));

        obj.addClassDescData(desc, new TCObject.ObjectData().addData(new a()));

        ser.addObject(obj);

        ser.write("/tmp/ser");

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser")));
//        in.readObject();
        System.out.println(in.readObject());

    }
}
