package util.n1nty.gen;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static java.io.ObjectStreamConstants.STREAM_MAGIC;
import static java.io.ObjectStreamConstants.STREAM_VERSION;

public class Serialization {

    private List<Data> objects = new ArrayList<Data>();
    private Object handle; // ObjectOutputStream$HandleTable 类的对象
    private TCBlockData blockData;

    public Serialization() {
        try {

            ObjectOutputStream output = new ObjectOutputStream(new ByteOutputStream());
            Field f = output.getClass().getDeclaredField("handles");
            f.setAccessible(true);
            this.handle = f.get(output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addObject(Object obj) throws Exception {
        this.addObject(obj, false);
    }
    public void addObject(Object obj, boolean block) throws Exception {
        if (obj instanceof TCObject) {
            TCObject tco = (TCObject) obj;
            if (tco.size() == 0) {
                throw new Exception("no class_desc/data in TCObject");
            }
        }
        this.objects.add(new Data(block, obj));
    }

    public void write(String path) throws Exception {
        write(new File(path));
    }

    public void write(File path) throws Exception {
        write(new FileOutputStream(path));
    }

    public void write(OutputStream o) throws Exception {
        if (this.objects.size() == 0) {
            throw new Exception("no objects in serialization");
        }
        DataOutputStream out = new DataOutputStream(o);
        out.writeShort(STREAM_MAGIC);
        out.writeShort(STREAM_VERSION);

        HandleContainer handles = new HandleContainer(this.handle);
//        for (Object obj:
//             this.objects) {
//
//            if (obj instanceof SerializedElement) {
//                this.writeBlockData(out, handles);
//                ((SerializedElement)obj).write(out, handles);
//            }
//            else {
//                this.treatObject(out, obj, handles, true);
//            }
//        }

        for (Data data:
                this.objects) {
            if (!data.block) {
                this.writeBlockData(out, handles);
            }

            Object obj = data.data;
            if (obj instanceof SerializedElement) {
                ((SerializedElement)obj).write(out, handles);
            } else {
                this.treatObject(out, obj, handles, data.block);
            }
        }

        // 如果有剩下的还没写入的 block ，则将它们写入
        this.writeBlockData(out, handles);
        out.close();
    }


//    public void treatObject(DataOutputStream out, Object obj, HandleContainer handles) throws Exception {
//        this.treatObject(out, obj, handles, false);
//    }

    protected void writeBlockData(DataOutputStream out, HandleContainer handles) throws Exception {
        if (this.blockData != null) {
            this.blockData.write(out, handles);
            this.blockData = null;
        }
    }

    public void treatObject(DataOutputStream out, Object obj, HandleContainer handles, boolean blockData)
            throws Exception {

        /**
         *  下面这一小段代码主要处理手动利用 objout.writeXXX 方法（writeObject 除外）向流中写入的 block 数据
         *  比如说：
         private void writeObject(ObjectOutputStream out) throws Exception {
             out.defaultWriteObject();
             out.writeByte(100);
             out.writeChar('1');
             out.writeChars("23");
             out.writeUTF("test");
         }
         * */
        if (blockData) {
            if (this.blockData == null) {
                this.blockData = new TCBlockData();
            }
            this.blockData.append(obj);
            return;
        } else {
            this.writeBlockData(out, handles);
        }


        /**
         * 下面的代码主要处理 object data，也就是序列化的对象成员的值，或者利用 objout.writeObject 方法写入的对象
         * */
        if (obj instanceof Byte) {
            out.writeByte((Byte) obj);
        } else if (obj instanceof Short) {
            out.writeShort((Short) obj);
        } else if (obj instanceof Integer) {
            out.writeInt((Integer) obj);
        } else if (obj instanceof Long) {
            out.writeLong((Long) obj);
        }  else if (obj instanceof Float) {
            out.writeFloat((Float)obj);
        }  else if (obj instanceof Double) {
            out.writeDouble((Double)obj);
        }  else if (obj instanceof Character) {
            out.writeChar((Character)obj);
        } else if (obj instanceof String || obj instanceof TCString) {
            TCString s = obj instanceof TCString ? (TCString) obj : TCString.getInstance(obj.toString());
            s.write(out, handles);
        } else if (obj instanceof TCObject) {
            TCObject o = (TCObject) obj;
            o.write(out, handles);
        } else {
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            ObjectOutputStream objout = this.getPatchedOutputStream(byteout);
            TCJavaObject o = new TCJavaObject(obj, byteout, objout);
            o.write(out, handles);
        }
    }

    private static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        if (Modifier.isFinal(f.getModifiers())) {
            //reset final field
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        }
        f.set(obj, value);
    }

    private ObjectOutputStream getPatchedOutputStream(ByteArrayOutputStream out) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        setFieldValue(oos, "handles", this.handle);
        return oos;
    }


    // todo 这个类与 ObjectData 类的 Data 类是完全一样的，以后可能需要合并
    private class Data {
        private boolean block;
        private Object data;

        public Data(boolean block, Object data) {
            this.block = block;
            this.data = data;
        }
    }
}
