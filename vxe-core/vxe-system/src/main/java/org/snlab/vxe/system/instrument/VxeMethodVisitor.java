/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.vxe.system.instrument;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import org.snlab.vxe.api.annotation.VxeEntryPoint;

public class VxeMethodVisitor extends MethodVisitor {

    private boolean main = false;

    protected static final String VXE_ENTRYPOINT = Type.getDescriptor(
            VxeEntryPoint.class
    );

    public VxeMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }


    public boolean isMain() {
        return main;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (desc.equals(VXE_ENTRYPOINT)) {
            main = true;
        }
        return super.visitAnnotation(desc, visible);
    }
}
