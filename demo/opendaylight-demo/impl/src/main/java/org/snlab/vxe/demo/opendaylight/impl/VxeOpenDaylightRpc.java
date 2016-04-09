/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl;

import org.opendaylight.yangtools.yang.binding.DataObject;
import org.snlab.vxe.api.Identifier;

public class VxeOpenDaylightRpc< I extends DataObject, O extends DataObject> implements Identifier<O> {

    private I input;

    public VxeOpenDaylightRpc(I input) {
        this.input = input;
    }

    public I getInput() {
        return this.input;
    }
}
