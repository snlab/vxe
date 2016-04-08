/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl;

import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.vxe.demo.opendaylight.rev160408.SetupPathInput;
import org.opendaylight.yang.gen.v1.urn.vxe.demo.opendaylight.rev160408.VxeOpendaylightDemoService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Futures;

public class VxeOpenDaylightDemoProvider implements BindingAwareProvider,
                                                    AutoCloseable, VxeOpendaylightDemoService {

    private static final Logger LOG = LoggerFactory.getLogger(VxeOpenDaylightDemoProvider.class);

    private DataBroker broker = null;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("VxeOpenDaylightDemoProvider Session Initiated");

        broker = session.getSALService(DataBroker.class);

        initializeVxeOpenDaylight(broker);
    }

    private void initializeVxeOpenDaylight(DataBroker broker) {
        //TODO
    }

    @Override
    public void close() throws Exception {
        LOG.info("VxeOpenDaylightDemoProvider Closed");
    }

    @Override
    public Future<RpcResult<Void>> setupPath(SetupPathInput input) {
        ReadWriteTransaction rwt = broker.newReadWriteTransaction();
        VxeOpenDaylightDatastore vxeDatastore = new VxeOpenDaylightDatastore(rwt);

        VxeDemoTasklet tasklet = new VxeDemoTasklet();

        tasklet.findPath(input.getSource(), input.getDestination(), vxeDatastore);
        return Futures.immediateFuture(RpcResultBuilder.<Void>success().build());
    }

}
