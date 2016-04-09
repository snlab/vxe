/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.NodeFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.RemoveFlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.RemoveFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.RemoveFlowOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.vxe.demo.opendaylight.rev160408.SetupPathInput;
import org.opendaylight.yang.gen.v1.urn.vxe.demo.opendaylight.rev160408.VxeOpendaylightDemoService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.vxe.demo.opendaylight.impl.VxeOpenDaylightDatastore.OpenDaylightRpc;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;

public class VxeOpenDaylightDemoProvider implements BindingAwareProvider,
                                                    AutoCloseable, VxeOpendaylightDemoService {

    private static final Logger LOG = LoggerFactory.getLogger(VxeOpenDaylightDemoProvider.class);

    private DataBroker broker = null;
    private SalFlowService openflow = null;

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("VxeOpenDaylightDemoProvider Session Initiated");

        broker = session.getSALService(DataBroker.class);
        openflow = session.getRpcService(SalFlowService.class);

        if ((broker == null) || (openflow == null)) {
            LOG.error("Missing dependencies!");
        }
        session.addRpcImplementation(VxeOpendaylightDemoService.class, this);

        initializeVxeOpenDaylight(broker);
    }

    private void initializeVxeOpenDaylight(DataBroker broker) {
    }

    @Override
    public void close() throws Exception {
        LOG.info("VxeOpenDaylightDemoProvider Closed");
    }

    private class SalFlowOpenDaylightRpc implements OpenDaylightRpc<AddFlowInput, AddFlowOutput> {
        @Override
        public Future<RpcResult<AddFlowOutput>> process(AddFlowInput input) {
            return openflow.addFlow(input);
        }

        @Override
        public Future<RpcResult<Void>> clear(AddFlowInput input) {
            RemoveFlowInput removeInput = new RemoveFlowInputBuilder((NodeFlow) input)
                .setTransactionUri(input.getTransactionUri())
                .setFlowRef(input.getFlowRef()).build();
            Future<RpcResult<RemoveFlowOutput>> result = openflow.removeFlow(removeInput);
            return Futures.lazyTransform(result, new Function<RpcResult<RemoveFlowOutput>, RpcResult<Void>>() {
                @Override
                public RpcResult<Void> apply(RpcResult<RemoveFlowOutput> out) {
                    if (out.isSuccessful()) {
                        return RpcResultBuilder.<Void>success().build();
                    } else {
                        return RpcResultBuilder.<Void>failed().build();
                    }
                }
            });
        }
    }

    @Override
    public Future<RpcResult<Void>> setupPath(SetupPathInput input) {
        VxeOpenDaylightDatastore datastore = new VxeOpenDaylightDatastore(broker);
        datastore.registerRpc(AddFlowInput.class, new SalFlowOpenDaylightRpc());
        VxeDemoTasklet tasklet = new VxeDemoTasklet();

        tasklet.findPath(input.getSource(), input.getDestination(), datastore);

        try {
            ReadWriteTransaction rwt = datastore.getTx();
            if (rwt != null) {
                rwt.submit().get();
            }
        } catch (InterruptedException | ExecutionException e) {
        }
        return Futures.immediateFuture(RpcResultBuilder.<Void> success().build());
    }

}
