
package org.snlab.vxe.api;

public interface Tasklet<T> {

    public void submit();

    public void cancel();

}
