package util.n1nty.gen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandleContainer {

    private Object handle;
    private static Method lookup;
    private static Method assign;

    public HandleContainer(Object handle) {
        this.handle = handle;
    }

    static {
        try {
            Class cls = Class.forName("java.io.ObjectOutputStream$HandleTable");
            assign = cls.getDeclaredMethod("assign", Object.class);
            assign.setAccessible(true);

            lookup = cls.getDeclaredMethod("lookup", Object.class);
            lookup.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getHandle(Object obj) {
        try {
            return ((Integer)lookup.invoke(this.handle, obj)).intValue();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void putHandle(Object obj) {
        if (getHandle(obj) == -1) {
            try {
                assign.invoke(this.handle, obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    private int baseHandle = baseWireHandle;
    private Map<Object, Integer> handles = new HashMap<Object, Integer>();

    public int getHandle(Object key) {
        return handles.get(key) != null ? handles.get(key).intValue() : -1;
    }

    public void putHandle(Object obj) {
        if (!handles.containsKey(obj)) {
            this.handles.put(obj,  this.baseHandle ++);
        }

    }*/
}
