package util.n1nty.gen;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.io.ObjectStreamConstants.*;

public class TCClassDesc extends ReferencableObject implements SerializedElement {


    private String className;
    private long serialVersionUID;
    private byte classDescFlags;
    private List<Field> fields = new ArrayList<Field>();

    protected TCClassDesc() {}

    public int getFieldsCount() {
        return this.fields.size();
    }

    public TCClassDesc(String className) throws Exception {
        this(className, -1, (byte)0x0);
    }

    public TCClassDesc(String className, long serialVersionUID) throws Exception {
        this(className, serialVersionUID, (byte)0x0);
    }

    public TCClassDesc(String className, byte classDescFlags) throws Exception {
        this(className, -1, classDescFlags);
    }

    public TCClassDesc(String className, long serialVersionUID, byte classDescFlags) throws Exception {
        this.className = className;

        this.serialVersionUID = serialVersionUID != -1 ? serialVersionUID : this.getSerialVersionUID();
        this.classDescFlags = classDescFlags != 0x0 ? classDescFlags : this.getClassDescFlags();
    }

    private long getSerialVersionUID() throws Exception {
        Class cls = Class.forName(this.className);
        java.lang.reflect.Field f = cls.getDeclaredField("serialVersionUID");
        if (f == null) {
            return -1;
        }
        f.setAccessible(true);

        return Long.valueOf(f.get(null).toString());
    }

    private byte getClassDescFlags() throws Exception {
        Class cls = Class.forName(this.className);
        byte b = 0x0;

        if (Serializable.class.isAssignableFrom(cls)) {
            b |= SC_SERIALIZABLE;
        }

        try {
            if (cls.getDeclaredMethod("writeObject", ObjectOutputStream.class) != null) {
                b |= SC_WRITE_METHOD;
            }
        } catch (Exception ex){}

        return b;
    }

    public boolean hasWriteObject() {
        return (this.classDescFlags & SC_WRITE_METHOD) != 0;
    }

    public TCClassDesc addField(Field field) {
        this.fields.add(field);
        return this;
    }

    @Override
    public void write(DataOutputStream out, HandleContainer handles) throws Exception {
        if (handles.getHandle(this) != -1) {

            TCReference reference = new TCReference(handles.getHandle(this));
            reference.write(out, handles);

            throw new Exception("stop"); // 一个丑陋的实现
        }
        super.write(out, handles);
    }

    public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(TC_CLASSDESC);
        out.writeUTF(this.className);
        out.writeLong(this.serialVersionUID);
        out.writeByte(this.classDescFlags);
        out.writeShort(this.fields.size());

        // class desc 的计数在 fields 之前，所以在写入任何 field 时，要先对 class desc 进行计数
        handles.putHandle(this.getHandleObject());

        for (Field field:
                this.fields
//             this.sortFields()
                ) {
            field.write(out, handles);
        }

        out.writeByte(TC_ENDBLOCKDATA);
    }


    public static class Field implements SerializedElement {

        private String name;
        private Class type;

        public Field(String name, Class type) {
            this.name = name;
            this.type = type;
        }

        private byte getTypeByte() throws Exception {
            // todo 支持的类型不全
            Map<Class, Byte> bytes = new HashMap<Class, Byte>();
            bytes.put(byte.class, (byte)0x42);
            bytes.put(char.class, (byte)0x43);
            bytes.put(double.class, (byte)0x44);
            bytes.put(float.class, (byte)0x46);
            bytes.put(int.class, (byte)0x49);
            bytes.put(long.class, (byte)0x4a);
            bytes.put(short.class, (byte)0x53);
            bytes.put(boolean.class, (byte)0x5a);
//            bytes.put(Object.class, (byte)0x4c);

            Byte b = bytes.get(this.type);
            if (b == null) {
                b = Byte.valueOf((byte)0x4c);
            }
            return b.byteValue();
        }

        public void write(DataOutputStream out, HandleContainer handles) throws Exception {
            byte b = this.getTypeByte();
            out.writeByte(b);
            out.writeUTF(this.name);

            if (b == 0x4c) {
                // 成员类型是对象，写入 class name 等信息
                TCString s = TCString.getInstance((char)b+this.type.getName().replace('.','/')+";");
                s.write(out, handles);
            }

        }
    }
}
