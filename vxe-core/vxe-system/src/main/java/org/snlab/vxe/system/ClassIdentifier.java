/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.vxe.system;

import java.util.Objects;

public class ClassIdentifier {

    private ClassLoader classLoader = null;
    private String className = null;

    public ClassIdentifier(ClassLoader classLoader, String className) {
        this.classLoader = classLoader;
        this.className = className;
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs instanceof ClassIdentifier) {
            ClassIdentifier ci = (ClassIdentifier) rhs;
            return (this.classLoader.equals(ci.classLoader))
                && (this.className.equals(ci.className));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.classLoader, this.className);
    }

    /**
     * @return the classLoader
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }
}

