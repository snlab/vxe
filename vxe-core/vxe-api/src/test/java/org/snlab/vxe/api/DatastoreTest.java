
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
