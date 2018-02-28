package util.n1nty.gen;

import java.io.DataOutputStream;

import static java.io.ObjectStreamConstants.TC_NULL;

public class TCNull implements SerializedElement {
    public void write(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(TC_NULL);
    }
}
