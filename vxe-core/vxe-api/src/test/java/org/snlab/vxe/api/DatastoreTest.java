
package org.snlab.vxe.api;

import org.junit.Test;
import org.junit.Assert;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DatastoreTest {

    @Test
    public void testDatastore() {
        Identifier<Integer> key = (Identifier<Integer>) mock(Identifier.class);
        Datastore datastore = mock(Datastore.class);
        when(datastore.read(eq(key))).thenReturn(new Integer(1));
        Assert.assertEquals(datastore.read(key), new Integer(1));
    }

}
