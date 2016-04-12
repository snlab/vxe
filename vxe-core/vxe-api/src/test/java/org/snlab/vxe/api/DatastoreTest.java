/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.vxe.api;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

public class DatastoreTest {

    @Test
    public void testDatastore() {
        Identifier<Integer> key = (Identifier<Integer>) mock(Identifier.class);
        Datastore datastore = mock(Datastore.class);
        when(datastore.read(eq(key))).thenReturn(new Integer(1));
        Assert.assertEquals(datastore.read(key), new Integer(1));
    }

}
