package util.n1nty.gen;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;

public class TCJavaObject extends ReferencableObject implements SerializedElement {

    @Override
    public Object getHandleObject() {
        return this.obj;
    }

    private ObjectOutputStream objOut;
    private ByteArrayOutputStream byteOut;
    private Object obj;

    public TCJavaObject(Object obj, ByteArrayOutputStream byteOut, ObjectOutputStream objOut) {
        this.obj = obj;
        this.byteOut = byteOut;
        this.objOut = objOut;
    }

    public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
        ObjectOutputStream oos = this.objOut;
        oos.writeObject(this.obj);
//        oos.close(); 这里不能 close，否则会清空所有的 handle
        out.write(this.byteOut.toByteArray(), 4, this.byteOut.size() - 4); // 4 = skip the header
    }
}
