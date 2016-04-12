/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.vxe.demo.datastore;

import org.snlab.vxe.api.Identifier;

public class TopologyKey implements Identifier<Topology> {

    private String topologyId = null;

    public TopologyKey(String topologyId) {
        this.topologyId = topologyId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof TopologyKey) {
            if (obj == this) {
                return true;
            }
            TopologyKey rhs = (TopologyKey) obj;
            return this.topologyId.equals(rhs.topologyId);
        } else if (obj instanceof String) {
            return this.topologyId.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return topologyId.hashCode();
    }

}
