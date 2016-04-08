/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl;

import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.snlab.vxe.api.Identifier;

import com.google.common.base.Objects;

public class VxeOpenDaylightIdentifier<T extends DataObject> implements Identifier<T> {

    private LogicalDatastoreType type = null;
    private InstanceIdentifier<T> iid = null;

    public VxeOpenDaylightIdentifier(LogicalDatastoreType type,
                                            InstanceIdentifier<T> iid) {
        this.type = type;
        this.iid = iid;
    }

    public LogicalDatastoreType getType() {
        return type;
    }

    public InstanceIdentifier<T> getInstanceIdentifier() {
        return iid;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, iid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (obj instanceof VxeOpenDaylightIdentifier) {
            VxeOpenDaylightIdentifier<?> vid = (VxeOpenDaylightIdentifier<?>) obj;
            return (this.getInstanceIdentifier().equals(vid.getInstanceIdentifier())
                            && this.getType().equals(vid.getType()));
        }
        return false;
    }
}
