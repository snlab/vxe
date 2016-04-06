
package org.snlab.vxe.demo.datastore;

import org.snlab.vxe.api.Identifier;

public class NodeKey implements Identifier<Node> {

    private String nodeId = null;

    public NodeKey(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof NodeKey) {
            if (obj == this) {
                return true;
            }
            NodeKey rhs = (NodeKey) obj;
            return this.nodeId.equals(rhs.nodeId);
        } else if (obj instanceof String) {
            return this.nodeId.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nodeId.hashCode();
    }

}
