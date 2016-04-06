
package org.snlab.vxe.demo.datastore;

import org.snlab.vxe.api.Identifier;

public class LinkKey implements Identifier<Link> {

    private String linkId = null;

    public LinkKey(String linkId) {
        this.linkId = linkId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof LinkKey) {
            if (obj == this) {
                return true;
            }
            LinkKey rhs = (LinkKey) obj;
            return this.linkId.equals(rhs.linkId);
        } else if (obj instanceof String) {
            return this.linkId.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return linkId.hashCode();
    }

}
