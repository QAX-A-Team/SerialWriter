package util.n1nty.gen;

import java.io.DataOutputStream;

public interface SerializedElement {

    public void write(DataOutputStream out, HandleContainer handles) throws Exception;
}
