
package org.snlab.vxe.system.instrument;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.vxe.api.annotation.VxeTasklet;
import org.snlab.vxe.system.ClassIdentifier;
import org.snlab.vxe.system.VxeClassMap;

public class VxeClassVisitor extends ClassVisitor {

    protected static Logger LOG = LoggerFactory.getLogger(VxeClassVisitor.class);

    private ClassIdentifier ci = null;
    private boolean annotated = false;
    private VxeClassMap map = null;
    private String mainMethod = null;

    protected static final String VXE_TASKLET = Type.getDescriptor(
            VxeTasklet.class
    );

    public VxeClassVisitor(ClassVisitor cv, ClassIdentifier ci, VxeClassMap map) {
        super(Opcodes.ASM5, cv);

        this.ci = ci;
        this.map = map;
    }

    public String getMainMethod() {
        return mainMethod;
    }

    public boolean isAnnotated() {
        return annotated;
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        LOG.info("Class {} is owned by {}: ", ci.getClassName(), owner);
        ClassIdentifier ci = new ClassIdentifier(this.ci.getClassLoader(), owner);
        if (map.contains(ci)) {
            annotated = true;
        }
        super.visitOuterClass(owner, name, desc);
    }

    /**
     * Check if the class is annotated with VxeTasklet.
     *
     * @param desc The descriptor of the annotation.
     * @param visible Whether the annotation is visible.
     * @return
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        LOG.debug("Found annotation on class {}: {}", ci.getClassName(), desc);
        if (desc.equals(VXE_TASKLET)) {
            LOG.info("Loading VxeTasklet class: {}", ci.getClassName());

            annotated = true;
        }
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                        String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (annotated) {
            return new VxeMethodVisitor(mv);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (annotated) {
            LOG.info("The class {} will be processed by VXE", ci.getClassName());
        }
        super.visitEnd();
    }

}
