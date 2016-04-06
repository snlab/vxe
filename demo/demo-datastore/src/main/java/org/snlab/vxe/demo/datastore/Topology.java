
package org.snlab.vxe.demo.datastore;

import java.util.List;

import org.snlab.vxe.api.VxeDataUnit;

@VxeDataUnit
public interface Topology {

    public List<Link> getLinks();

    public List<Node> getNodes();

}
