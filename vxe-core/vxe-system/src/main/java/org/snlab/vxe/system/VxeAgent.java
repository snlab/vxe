
package org.snlab.vxe.system;

import java.lang.instrument.Instrumentation;

public class VxeAgent {

    private static boolean loaded = false;
    private static Instrumentation instrumentation = null;
    private static VxeTransformer transformer = null;

    public static void premain(String agentArgs, Instrumentation inst) {
        setup(agentArgs, inst, "Premain-Class");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        setup(agentArgs, inst, "Agent-Class");
    }

    private static void setup(String agentArgs, Instrumentation inst, String how) {
        if (loaded) {
            System.out.println("[INFO] Vxe: VxeAgent already loaded, skipping");
            return;
        }
        loaded = true;

        instrumentation = inst;

        //TODO initialize the system
        //TODO add classfile transformers
        transformer = new VxeTransformer();
        instrumentation.addTransformer(transformer);

        System.out.println("[INFO] Vxe: VxeAgent loaded as " + how);
    }

    public static byte[] getOriginalBytecode(Class<?> clazz) {
        if (!loaded) {
            return null;
        }
        return (transformer.getOriginalBytecode(clazz));
    }
}
