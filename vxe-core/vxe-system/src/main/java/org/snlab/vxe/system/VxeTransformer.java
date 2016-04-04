
package org.snlab.vxe.system;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class VxeTransformer implements ClassFileTransformer {

    private Map<ClassIdentifier, byte[]> original = new HashMap<ClassIdentifier, byte[]>();

    @Override
    public byte[] transform(ClassLoader classLoader, String className,
                            Class<?> redefinedClass, ProtectionDomain protectionDomain,
                            byte[] classBuffer) throws IllegalClassFormatException {
        //System.out.println("ClassLoader: " + classLoader);
        //System.out.println("Loading class " + className);
        //System.out.println("Class buffer is null? " + (classBuffer == null));

        try {
            ClassIdentifier ci = new ClassIdentifier(classLoader, className);
            original.put(ci, classBuffer.clone());
        } catch (Exception e) {
        }

        return null;
    }

    public byte[] getOriginalBytecode(Class<?> clazz) {
        ClassLoader cl = clazz.getClassLoader();
        String className = clazz.getName().replace(".", "/");

        ClassIdentifier ci = new ClassIdentifier(cl, className);
        return original.getOrDefault(ci, null);
    }
}
