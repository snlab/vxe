package org.snlab.vxe.demo;

import org.snlab.vxe.api.Datastore;
import org.snlab.vxe.api.VxeDatastore;
import org.snlab.vxe.api.VxeEntryPoint;
import org.snlab.vxe.api.VxeTasklet;

@VxeTasklet
public class DemoTasklet {

    @VxeEntryPoint
    public void myMain(Integer parameter, @VxeDatastore Datastore datastore) {
        datastore.read(null);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };

        runnable.run();
    }

}
