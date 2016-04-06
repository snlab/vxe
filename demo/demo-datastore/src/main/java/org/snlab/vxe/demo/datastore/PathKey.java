
package org.snlab.vxe.demo.datastore;

import org.snlab.vxe.api.Identifier;

public class PathKey implements Identifier<Path> {

    private String pathId = null;

    public PathKey(String pathId) {
        this.pathId = pathId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof PathKey) {
            if (obj == this) {
                return true;
            }
            PathKey rhs = (PathKey) obj;
            return this.pathId.equals(rhs.pathId);
        } else if (obj instanceof String) {
            return this.pathId.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pathId.hashCode();
    }

}
