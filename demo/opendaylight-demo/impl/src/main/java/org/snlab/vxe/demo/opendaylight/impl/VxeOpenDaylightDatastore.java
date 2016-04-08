/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.snlab.vxe.api.Datastore;
import org.snlab.vxe.api.Identifier;

import com.google.common.base.Optional;

public class VxeOpenDaylightDatastore implements Datastore {

    private ReadWriteTransaction rwt = null;

    public VxeOpenDaylightDatastore(ReadWriteTransaction rwt) {
        this.rwt = rwt;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(Identifier<T> id) {
        VxeOpenDaylightIdentifier<? extends DataObject> vid;

        vid = (VxeOpenDaylightIdentifier<? extends DataObject>) id;

        try {
            Optional<? extends DataObject> result;
            result = rwt.read(vid.getType(), vid.getInstanceIdentifier()).get();
            return (T) result.get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void write(Identifier<T> id, T value) {
        VxeOpenDaylightIdentifier<? extends DataObject> vid;
        vid = (VxeOpenDaylightIdentifier<? extends DataObject>) id;

        forceWrite(vid.getType(), vid.getInstanceIdentifier(), (Object) value);
    }

    @SuppressWarnings("unchecked")
    private <T extends DataObject> void forceWrite(LogicalDatastoreType type,
                                                    InstanceIdentifier<T> iid,
                                                    Object value) {
        rwt.put(type, iid, (T) value, true);
    }

}
