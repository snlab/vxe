
package org.snlab.vxe.api;

public interface VirtualExecutionEnvironment {

    public <T> TaskletFactory<T> register(Class<T> clazz);

}
