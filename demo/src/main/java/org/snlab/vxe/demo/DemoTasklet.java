package org.snlab.vxe.demo;

import org.snlab.vxe.api.Datastore;
import org.snlab.vxe.api.VxeDatastore;
import org.snlab.vxe.api.VxeEntry;
import org.snlab.vxe.api.VxeTasklet;

@VxeTasklet
public class DemoTasklet {

    @VxeEntry
    public void myMain(Integer parameter, @VxeDatastore Datastore datastore) {
        datastore.read(null);
    }

}
