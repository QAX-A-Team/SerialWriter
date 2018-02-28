package util.n1nty.gen;

import java.io.DataOutputStream;

public abstract class ReferencableObject {


    public  Object getHandleObject() {
        return this;
    }

    public void write(DataOutputStream out, HandleContainer handles) throws Exception {
        if (handles.getHandle(this.getHandleObject()) != -1) {
            TCReference reference = new TCReference(handles.getHandle(this.getHandleObject()));
            reference.write(out, handles);
        } else {
            this.doWrite(out, handles);
            handles.putHandle(this.getHandleObject());
        }
    }



    public abstract void doWrite(DataOutputStream out, HandleContainer handles) throws Exception;
}
