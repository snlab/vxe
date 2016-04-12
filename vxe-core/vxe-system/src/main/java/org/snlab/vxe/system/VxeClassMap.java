/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.vxe.system;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class VxeClassMap {

    private Map<ClassIdentifier, VxeClassMetadata> map = null;

    public VxeClassMap() {
        map = new HashMap<ClassIdentifier, VxeClassMetadata>();
    }

    public boolean contains(ClassIdentifier ci) {
        return (map.get(ci) != null);
    }

    public VxeClassMetadata get(ClassIdentifier ci) {
        return map.getOrDefault(ci, null);
    }

    public void put(ClassIdentifier ci, VxeClassMetadata group) {
        map.put(ci, group);
    }
}
