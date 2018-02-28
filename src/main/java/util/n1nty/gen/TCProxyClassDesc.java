package util.n1nty.gen;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.io.ObjectStreamConstants.TC_ENDBLOCKDATA;
import static java.io.ObjectStreamConstants.TC_PROXYCLASSDESC;

public class TCProxyClassDesc extends TCClassDesc implements SerializedElement {

    private List<Class> interfaces = new ArrayList<Class>();

    public TCProxyClassDesc addInterface(Class cls) {
        this.interfaces.add(cls);
        return this;
    }

    public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(TC_PROXYCLASSDESC);
        out.writeInt(this.interfaces.size());

        for (Class intf:
             this.interfaces) {
            out.writeUTF(intf.getName());
        }

        out.writeByte(TC_ENDBLOCKDATA);
    }
}
