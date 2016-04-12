/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.vxe.demo.datastore;

import org.snlab.vxe.api.annotation.VxeDataUnit;

@VxeDataUnit
public interface Link {

    public String getLinkId();

    public String getEndpoint1();

    public String getEndpoint2();

    public int getCapacity();

    public int getAvailableBandwidth();

}
