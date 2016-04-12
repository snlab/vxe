/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.vxe.demo.datastore;

import org.snlab.vxe.api.Identifier;

public class LinkKey implements Identifier<Link> {

    private String linkId = null;

    public LinkKey(String linkId) {
        this.linkId = linkId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof LinkKey) {
            if (obj == this) {
                return true;
            }
            LinkKey rhs = (LinkKey) obj;
            return this.linkId.equals(rhs.linkId);
        } else if (obj instanceof String) {
            return this.linkId.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return linkId.hashCode();
    }

}
