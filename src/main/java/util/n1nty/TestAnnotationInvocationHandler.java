package util.n1nty;

import util.Reflections;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;


interface Interface1 {}

class Class1 implements Serializable {
    private Interface1 face1;

    public void setFace1(Interface1 face1) {
        this.face1 = face1;
    }

    public Interface1 getFace1() {
        return face1;
    }
}

class Class2 implements Serializable {

    private void readObject(ObjectInputStream input) throws Exception {
        input.defaultReadObject();

        try {
            input.readObject();
        } catch (Exception ex) {
            System.out.println();
        }
    }
}

public class TestAnnotationInvocationHandler {

    public static void main(String[] args) throws Exception {

        InvocationHandler handler = (InvocationHandler) Reflections.getFirstCtor("sun.reflect.annotation.AnnotationInvocationHandler").newInstance(Class.class, new HashMap());
        Interface1 face1 = (Interface1) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Interface1.class}, handler);

        Class1 ins1 = new Class1();
        ins1.setFace1(face1);

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("/tmp/ser_invocation")));
        out.writeObject(ins1);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("/tmp/ser_invocation")));
        System.out.println(in.readObject());;

        /*
        *
STREAM_MAGIC - 0xac ed
STREAM_VERSION - 0x00 05
Contents
  TC_OBJECT - 0x73
    TC_CLASSDESC - 0x72
      className
        Length - 17 - 0x00 11
        Value - util.n1nty.Class1 - 0x7574696c2e6e316e74792e436c61737331
      serialVersionUID - 0xc5 53 65 82 49 a4 5d db
      newHandle 0x00 7e 00 00
      classDescFlags - 0x02 - SC_SERIALIZABLE
      fieldCount - 1 - 0x00 01
      Fields
        0:
          Object - L - 0x4c
          fieldName
            Length - 5 - 0x00 05
            Value - face1 - 0x6661636531
          className1
            TC_STRING - 0x74
              newHandle 0x00 7e 00 01
              Length - 23 - 0x00 17
              Value - Lutil/n1nty/Interface1; - 0x4c7574696c2f6e316e74792f496e74657266616365313b
      classAnnotations
        TC_ENDBLOCKDATA - 0x78
      superClassDesc
        TC_NULL - 0x70
    newHandle 0x00 7e 00 02
    classdata
      util.n1nty.Class1
        values
          face1
            (object)
              TC_OBJECT - 0x73
                TC_PROXYCLASSDESC - 0x7d
                  newHandle 0x00 7e 00 03
                  Interface count - 1 - 0x00 00 00 01
                  proxyInterfaceNames
                    0:
                      Length - 21 - 0x00 15
                      Value - util.n1nty.Interface1 - 0x7574696c2e6e316e74792e496e7465726661636531
                  classAnnotations
                    TC_ENDBLOCKDATA - 0x78
                  superClassDesc
                    TC_CLASSDESC - 0x72
                      className
                        Length - 23 - 0x00 17
                        Value - java.lang.reflect.Proxy - 0x6a6176612e6c616e672e7265666c6563742e50726f7879
                      serialVersionUID - 0xe1 27 da 20 cc 10 43 cb
                      newHandle 0x00 7e 00 04
                      classDescFlags - 0x02 - SC_SERIALIZABLE
                      fieldCount - 1 - 0x00 01
                      Fields
                        0:
                          Object - L - 0x4c
                          fieldName
                            Length - 1 - 0x00 01
                            Value - h - 0x68
                          className1
                            TC_STRING - 0x74
                              newHandle 0x00 7e 00 05
                              Length - 37 - 0x00 25
                              Value - Ljava/lang/reflect/InvocationHandler; - 0x4c6a6176612f6c616e672f7265666c6563742f496e766f636174696f6e48616e646c65723b
                      classAnnotations
                        TC_ENDBLOCKDATA - 0x78
                      superClassDesc
                        TC_NULL - 0x70
                newHandle 0x00 7e 00 06
                classdata
                  java.lang.reflect.Proxy
                    values
                      h
                        (object)
                          TC_OBJECT - 0x73
                            TC_CLASSDESC - 0x72
                              className
                                Length - 50 - 0x00 32
                                Value - sun.reflect.annotation.AnnotationInvocationHandler - 0x73756e2e7265666c6563742e616e6e6f746174696f6e2e416e6e6f746174696f6e496e766f636174696f6e48616e646c6572
                              serialVersionUID - 0x55 ca f5 0f 15 cb 7e a5
                              newHandle 0x00 7e 00 07
                              classDescFlags - 0x02 - SC_SERIALIZABLE
                              fieldCount - 2 - 0x00 02
                              Fields
                                0:
                                  Object - L - 0x4c
                                  fieldName
                                    Length - 12 - 0x00 0c
                                    Value - memberValues - 0x6d656d62657256616c756573
                                  className1
                                    TC_STRING - 0x74
                                      newHandle 0x00 7e 00 08
                                      Length - 15 - 0x00 0f
                                      Value - Ljava/util/Map; - 0x4c6a6176612f7574696c2f4d61703b
                                1:
                                  Object - L - 0x4c
                                  fieldName
                                    Length - 4 - 0x00 04
                                    Value - type - 0x74797065
                                  className1
                                    TC_STRING - 0x74
                                      newHandle 0x00 7e 00 09
                                      Length - 17 - 0x00 11
                                      Value - Ljava/lang/Class; - 0x4c6a6176612f6c616e672f436c6173733b
                              classAnnotations
                                TC_ENDBLOCKDATA - 0x78
                              superClassDesc
                                TC_NULL - 0x70
                            newHandle 0x00 7e 00 0a
                            classdata
                              sun.reflect.annotation.AnnotationInvocationHandler
                                values
                                  memberValues
                                    (object)
                                      TC_OBJECT - 0x73
                                        TC_CLASSDESC - 0x72
                                          className
                                            Length - 17 - 0x00 11
                                            Value - java.util.HashMap - 0x6a6176612e7574696c2e486173684d6170
                                          serialVersionUID - 0x05 07 da c1 c3 16 60 d1
                                          newHandle 0x00 7e 00 0b
                                          classDescFlags - 0x03 - SC_WRITE_METHOD | SC_SERIALIZABLE
                                          fieldCount - 2 - 0x00 02
                                          Fields
                                            0:
                                              Float - F - 0x46
                                              fieldName
                                                Length - 10 - 0x00 0a
                                                Value - loadFactor - 0x6c6f6164466163746f72
                                            1:
                                              Int - I - 0x49
                                              fieldName
                                                Length - 9 - 0x00 09
                                                Value - threshold - 0x7468726573686f6c64
                                          classAnnotations
                                            TC_ENDBLOCKDATA - 0x78
                                          superClassDesc
                                            TC_NULL - 0x70
                                        newHandle 0x00 7e 00 0c
                                        classdata
                                          java.util.HashMap
                                            values
                                              loadFactor
                                                (float)1.06115891E9 - 0x3f 40 00 00
                                              threshold
                                                (int)0 - 0x00 00 00 00
                                            objectAnnotation
                                              TC_BLOCKDATA - 0x77
                                                Length - 8 - 0x08
                                                Contents - 0x0000001000000000
                                              TC_ENDBLOCKDATA - 0x78
                                  type
                                    (object)
                                      TC_CLASS - 0x76
                                        TC_CLASSDESC - 0x72
                                          className
                                            Length - 15 - 0x00 0f
                                            Value - java.lang.Class - 0x6a6176612e6c616e672e436c617373
                                          serialVersionUID - 0x2c 7e 55 03 d9 bf 95 53
                                          newHandle 0x00 7e 00 0d
                                          classDescFlags - 0x02 - SC_SERIALIZABLE
                                          fieldCount - 0 - 0x00 00
                                          classAnnotations
                                            TC_ENDBLOCKDATA - 0x78
                                          superClassDesc
                                            TC_NULL - 0x70
                                        newHandle 0x00 7e 00 0e

        *
        * */


    }
}
