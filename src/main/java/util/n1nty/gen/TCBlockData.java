package util.n1nty.gen;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static java.io.ObjectStreamConstants.TC_BLOCKDATA;

public class TCBlockData implements SerializedElement {

    private DataOutputStream out;
    private ByteArrayOutputStream byteOut;

    public TCBlockData() {
        this.byteOut = new ByteArrayOutputStream();
        this.out = new DataOutputStream(this.byteOut);
    }

    public void append(Object data) throws Exception {

        // todo 支持的类型不是太全
        if (data instanceof Integer) {
            this.out.writeInt((Integer)data);
        } else if (data instanceof Short) {
            this.out.writeShort((Short)data);
        } else if (data instanceof Long) {
            this.out.writeLong((Long)data);
        } else if (data instanceof Byte) {
            this.out.writeByte((Byte)data);
        } else if (data instanceof Character) {
            this.out.writeChar((Character)data);
        } else if (data instanceof  char[]) {
            this.out.writeChars(new String((char[])data));
        } else if (data instanceof String) {
            this.out.writeUTF((String)data);
        } else if (data instanceof Float) {
            this.out.writeFloat((Float) data);
        } else if (data instanceof Double) {
            this.out.writeDouble((Double)data);
        } else if (data instanceof Boolean) {
            this.out.writeBoolean((Boolean)data);
        }
    }

    public void write(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(TC_BLOCKDATA);
        byte[] data = this.byteOut.toByteArray();
        out.writeByte(data.length);
        out.write(data);
        this.byteOut.close();
        this.out.close();
    }
}
