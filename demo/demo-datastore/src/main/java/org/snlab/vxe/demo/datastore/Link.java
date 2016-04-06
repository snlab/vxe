
package org.snlab.vxe.demo.datastore;

import org.snlab.vxe.api.VxeDataUnit;

@VxeDataUnit
public interface Link {

    public String getLinkId();

    public String getEndpoint1();

    public String getEndpoint2();

    public int getCapacity();

    public int getAvailableBandwidth();

}
