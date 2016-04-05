
package org.snlab.vxe.system.instrument;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.snlab.vxe.system.ClassIdentifier;
import org.snlab.vxe.system.VxeClassMap;

public class ControlFlowTraceGenerator {

    private ClassReader cr = null;
    private ClassWriter cw = null;
    private VxeClassVisitor vcv = null;

    public ControlFlowTraceGenerator(ClassIdentifier ci, byte[] classBuffer, VxeClassMap map) {
        cr = new ClassReader(classBuffer);
        cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        vcv = new VxeClassVisitor(cw, ci, map);

        cr.accept(vcv, 0);
    }

    public boolean isAnnotated() {
        return vcv.isAnnotated();
    }

    public byte[] getClassBuffer() {
        return cw.toByteArray();
    }

    public String getMainMethod() {
        return vcv.getMainMethod();
    }
}
