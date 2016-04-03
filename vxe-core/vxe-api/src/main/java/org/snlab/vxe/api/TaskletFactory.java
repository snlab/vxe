
package org.snlab.vxe.api;

public interface TaskletFactory<T> {

    public Tasklet<T> create(Object... parameters);

}
