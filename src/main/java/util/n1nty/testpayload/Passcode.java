package util.n1nty.testpayload;

import java.io.ObjectInputStream;
import java.io.Serializable;

public class Passcode implements Serializable {
    private static final long serialVersionUID = 100L;
    private String passcode;

    public Passcode(String passcode) {
        this.passcode = passcode;
    }
    private void readObject(ObjectInputStream input) throws Exception {
        input.defaultReadObject();

        if (!this.passcode.equals("root")) {
            throw new Exception("pass code is not correct");
        }
    }
}
