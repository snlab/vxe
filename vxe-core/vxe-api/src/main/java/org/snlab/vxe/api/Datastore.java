
package org.snlab.vxe.api;

public interface Datastore {

    public <T> T read(Identifier<T> key);

    public <T> void write(Identifier<T> key, T value);

}
