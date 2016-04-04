
package org.snlab.vxe.system;

import java.util.Objects;

class ClassIdentifier {

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
}

