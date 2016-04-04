package org.snlab.vxe.demo;

import org.snlab.vxe.api.Tasklet;
// import org.snlab.vxe.api.TaskletFactory;
// import org.snlab.vxe.api.VirtualExecutionEnvironment;
import org.snlab.vxe.system.VxeAgent;

/**
 * This is a demo application for Vxe.
 *
 */
public class App {

    /**
     * The main class for the demo project.
     **/
    public static void main(String[] args) {
        // VirtualExecutionEnvironment vxe = null;

        Tasklet<DemoTasklet> tasklet = new Tasklet<DemoTasklet>() {
            @Override
            public void submit() {
                System.out.println("Submit");
            }

            @Override
            public void cancel() {
                System.out.println("Cancel");
            }
        };

        try {
            tasklet.submit();
            tasklet.cancel();
        } catch (Exception e) {
            System.out.println("hello");
        }

        System.out.println(DemoTasklet.class.getTypeName());
        System.out.println(DemoTasklet.class.getName());
        System.out.println(DemoTasklet.class.getSimpleName());

        System.out.println("Class is loaded? " + (VxeAgent.getOriginalBytecode(DemoTasklet.class) != null));
    }
}
