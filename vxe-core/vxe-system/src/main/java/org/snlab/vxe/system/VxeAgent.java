
package org.snlab.vxe.system;

import java.lang.instrument.Instrumentation;

public class VxeAgent {

    public static boolean loaded = false;

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

        //TODO initialize the system
        //TODO add classfile transformers

        System.out.println("[INFO] Vxe: VxeAgent loaded as " + how);
    }

}
