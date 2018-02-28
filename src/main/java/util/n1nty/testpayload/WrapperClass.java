package util.n1nty.testpayload;

import java.io.ObjectInputStream;
import java.io.Serializable;

public class WrapperClass implements Serializable {

    private static final long serialVersionUID = 200L;

    private void readObject(ObjectInputStream input) throws Exception {
        input.defaultReadObject();
        try {
            input.readObject();
        } catch (Exception e) {
            System.out.println("WrapperClass.readObject: input.readObject error");
        }
    }
}
