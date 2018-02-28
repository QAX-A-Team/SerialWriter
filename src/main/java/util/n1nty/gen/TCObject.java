package util.n1nty.gen;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.io.ObjectStreamConstants.*;

public class TCObject extends ReferencableObject implements SerializedElement {

    private Serialization ser;

    public TCObject(Serialization ser) {
        this.ser = ser;
    }

    private List<ClassDescData> descData = new ArrayList<ClassDescData>();

    public int size() {
        return this.descData.size();
    }

    public TCObject addClassDescData(TCClassDesc desc, ObjectData data) throws Exception {
        return addClassDescData(desc, data, false);
    }
    public TCObject addClassDescData(TCClassDesc desc, ObjectData data, boolean ignoreEquality) throws Exception {

        if (!ignoreEquality) {
            if (desc.getFieldsCount() != data.size()) {
                throw new  Exception("not enough fields/data, fields count: "+desc.getFieldsCount() + ", data count: " +data.size());
            }
        }
        data.setSer(this.ser);
        this.descData.add(new ClassDescData(desc, data));
        return this;
    }

    protected void writeHeader(DataOutputStream out, HandleContainer handles) throws Exception {
        out.writeByte(TC_OBJECT);
    }

    protected void writeClassDescs(DataOutputStream out, HandleContainer handles) throws Exception {
        // 写入 class desc

        try {
            for (int i = 0; i < this.descData.size(); i++) {
                ClassDescData d = this.descData.get(i);
                d.getDesc().write(out, handles);
            }
            out.writeByte(TC_NULL);
        } catch (Exception e) {
            if (!e.getMessage().equals("stop")) {
                e.printStackTrace();
            }
        }
    }

    protected void writeClassData(DataOutputStream out, HandleContainer handles) throws Exception {
        // 写入 class data
        for (int i = this.descData.size() - 1; i >= 0 ; i--) {
            ClassDescData d = this.descData.get(i);
            d.getData().write(out, handles);

            if (d.getDesc().hasWriteObject()) {
                // class data 写完了，如果有 block data 还没有被真正写入，则将其写入
                this.ser.writeBlockData(out, handles);

                // 重写了 writeObject
                out.writeByte(TC_ENDBLOCKDATA);
            }
        }
    }

    public void doWrite(DataOutputStream out, HandleContainer handles) throws Exception {

        this.writeHeader(out, handles);
        this.writeClassDescs(out, handles);
        // 对象的计数在 class data 之前，所以在写入任何 data 前，先对 tcobject 进行计数
        handles.putHandle(this.getHandleObject());
        this.writeClassData(out, handles);
    }


    private static class ClassDescData {

        private TCClassDesc desc;
        private TCObject.ObjectData data;

        public ClassDescData(TCClassDesc desc, TCObject.ObjectData data) {
            this.desc = desc;
            this.data = data;
        }

        public TCClassDesc getDesc() {
            return desc;
        }

        public TCObject.ObjectData getData() {
            return data;
        }
    }



    public static class ObjectData implements SerializedElement {

        private class Data {
            private boolean block;
            private Object data;

            public Data(boolean block, Object data) {
                this.block = block;
                this.data = data;
            }
        }
        private List<Data> data = new ArrayList<Data>();
        private Serialization ser;

        public void setSer(Serialization ser) {
            this.ser = ser;
        }

        public int size() {
            return this.data.size();
        }


        public void write(DataOutputStream out, HandleContainer handles) throws Exception {
            for (Data d:
                    this.data) {
//                this.treatObject(out, obj, handles);
                this.ser.treatObject(out, d.data, handles, d.block);
            }
        }

        public ObjectData addData(Object obj) {
            /**
             * 为成员添加属性值的时候，或者添加在 writeObject 方法里面利用 objout.writeObject 方法写入的对象时，应该使用此方法
             * */
            this.addData(obj, false);
            return this;
        }

        public ObjectData addData(Object obj, boolean block) {
            /**
             * 添加 TC_BLOCKDATA，主要用于添加那些在 writeObject 方法里面利用 writeInt 等方法写入的自定义的数据
             * */
            this.data.add(new Data(block, obj));
            return this;
        }
    }
}
