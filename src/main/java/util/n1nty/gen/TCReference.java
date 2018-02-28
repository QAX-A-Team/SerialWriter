package util.n1nty.gen;

import java.io.DataOutputStream;

import static java.io.ObjectStreamConstants.TC_REFERENCE;
import static java.io.ObjectStreamConstants.baseWireHandle;

public class TCReference implements SerializedElement {

    private int handle;

    public TCReference(int handle) {
        this.handle = handle;
    }

    /*
    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }
*/
    public void write(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(TC_REFERENCE);
        out.writeInt( baseWireHandle + this.handle);
    }
}
