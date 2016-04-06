
package org.snlab.vxe.demo.datastore;

import java.util.List;

import org.snlab.vxe.api.VxeDataUnit;

@VxeDataUnit
public interface Node {

    public String getNodeId();

    public List<String> getLinkIds();

}
