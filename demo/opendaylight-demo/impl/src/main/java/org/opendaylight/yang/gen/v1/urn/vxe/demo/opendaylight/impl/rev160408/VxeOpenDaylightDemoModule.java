package org.opendaylight.yang.gen.v1.urn.vxe.demo.opendaylight.impl.rev160408;

import org.snlab.vxe.demo.opendaylight.impl.VxeOpenDaylightDemoProvider;

public class VxeOpenDaylightDemoModule extends org.opendaylight.yang.gen.v1.urn.vxe.demo.opendaylight.impl.rev160408.AbstractVxeOpenDaylightDemoModule {
    public VxeOpenDaylightDemoModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public VxeOpenDaylightDemoModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.vxe.demo.opendaylight.impl.rev160408.VxeOpenDaylightDemoModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        VxeOpenDaylightDemoProvider provider;
        provider = new VxeOpenDaylightDemoProvider(getBindingMappingServiceDependency());
        getBrokerDependency().registerProvider(provider);

        return provider;
    }

}
