/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.vxe.system;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.vxe.system.instrument.ControlFlowTraceGenerator;

import org.snlab.vxe.system.VxeClassMetadata;

public class VxeTransformer implements ClassFileTransformer {

    protected static Logger LOG = LoggerFactory.getLogger(VxeTransformer.class);

    private VxeClassMap map = new VxeClassMap();

    @Override
    public byte[] transform(ClassLoader classLoader, String className,
                            Class<?> redefinedClass, ProtectionDomain protectionDomain,
                            byte[] classBuffer) throws IllegalClassFormatException {
        LOG.debug("ClassLoader: {}", classLoader);
        LOG.debug("Loading class: {}", className);
        LOG.debug("Class buffer is null? {}", (classBuffer == null));

        try {
            ClassIdentifier ci = new ClassIdentifier(classLoader, className);

            ControlFlowTraceGenerator cft = new ControlFlowTraceGenerator(ci, classBuffer, map);

            if (cft.isAnnotated()) {
                VxeClassMetadata meta = new VxeClassMetadata();

                meta.original = classBuffer.clone();
                meta.extended = cft.getClassBuffer();

                meta.main = cft.getMainMethod();

                map.put(ci, meta);
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage());
        }
        return null;
    }

    public byte[] getOriginalBytecode(Class<?> clazz) {
        ClassLoader cl = clazz.getClassLoader();
        String className = clazz.getName().replace(".", "/");

        ClassIdentifier ci = new ClassIdentifier(cl, className);
        VxeClassMetadata group = map.get(ci);
        return (group != null? group.original : null);
    }
}
