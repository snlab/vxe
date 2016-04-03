package org.snlab.vxe.demo;

import org.snlab.vxe.api.Tasklet;
import org.snlab.vxe.api.TaskletFactory;
import org.snlab.vxe.api.VirtualExecutionEnvironment;

/**
 * This is a demo application for Vxe.
 *
 */
public class App {

    /**
     * The main class for the demo project.
     **/
    public static void main(String[] args) {
        VirtualExecutionEnvironment vxe = null;

        Tasklet<DemoTasklet> tasklet = null;

        try {
            tasklet.submit();
            tasklet.cancel();
        } catch (Exception e) {
            System.out.println("hello");
        }
    }
}
