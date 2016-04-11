/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.vxe.api.Datastore;
import org.snlab.vxe.api.Identifier;
import org.snlab.vxe.demo.opendaylight.impl.VxeOpenDaylight.OpenDaylightRpc;
import org.snlab.vxe.demo.opendaylight.impl.proxy.ObjectProxyAgent;

import com.google.common.base.Optional;

public class VxeOpenDaylightDatastore implements Datastore {

    private static final Logger LOG = LoggerFactory.getLogger(VxeOpenDaylightDatastore.class);

    private DataBroker broker = null;
    private ReadWriteTransaction rwt = null;
    private VxeOpenDaylight context = null;
    private ObjectProxyAgent agent = null;

    private Set<Identifier<?>> readData = new HashSet<>();
    private Set<Identifier<?>> writeData = new HashSet<>();
    private List<Identifier<?>> rpcData = new LinkedList<>();

    public VxeOpenDaylightDatastore(DataBroker broker, VxeOpenDaylight context) {
        this.broker = broker;
        this.context = context;
        this.agent = new ObjectProxyAgent(context.getCodec(), readData, writeData);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(Identifier<T> id) {
        if (id instanceof VxeOpenDaylightIdentifier) {
            if (rwt == null) {
                rwt = broker.newReadWriteTransaction();
            }
            VxeOpenDaylightIdentifier<? extends DataObject> vid;
            vid = (VxeOpenDaylightIdentifier<? extends DataObject>) id;

            readData.add(id);

            try {
                Optional<? extends DataObject> result;
                result = rwt.read(vid.getType(), vid.getInstanceIdentifier()).get();
                if(result.isPresent()) {
                    return (T) agent.wrap(result.get().getClass(), result.get(), vid);
                } else {
                    return null;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        } else if (id instanceof VxeOpenDaylightRpc) {
            VxeOpenDaylightRpc<? extends DataObject, ? extends DataObject> rpcId;
            rpcId = (VxeOpenDaylightRpc<? extends DataObject, ? extends DataObject>) id;

            return (T) readRpc(rpcId);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <I extends DataObject, O extends DataObject> O readRpc(VxeOpenDaylightRpc<I, O> id) {
        DataObject input = id.getInput();
        OpenDaylightRpc<?, ?> rpc = context.getRpc(input);

        if (rpc == null) {
            LOG.warn("Failed to find RPC instance!");
            return null;
        }
        try {
            if (rwt != null) {
                rwt.submit().get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rwt = null;

        return (O) forceExecute(rpc, input, id);
    }

    @SuppressWarnings("unchecked")
    private <I extends DataObject> Object forceExecute(OpenDaylightRpc<I, ?> rpc,
                                                        DataObject input,
                                                        Identifier<?> id) {
        try {
            RpcResult<?> result = rpc.process((I) input).get();
            if (result.isSuccessful()) {
                rpcData.add(id);
                return result.getResult();
            } else {
                for (Object error: result.getErrors()) {
                    LOG.warn("Rpc error: {}", error);
                }
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn("Failed to execute RPC: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void write(Identifier<T> id, T value) {
        VxeOpenDaylightIdentifier<? extends DataObject> vid;
        vid = (VxeOpenDaylightIdentifier<? extends DataObject>) id;

        forceWrite(vid.getType(), vid.getInstanceIdentifier(), (Object) value);
    }

    @SuppressWarnings("unchecked")
    private <T extends DataObject> void forceWrite(LogicalDatastoreType type,
                                                    InstanceIdentifier<T> iid,
                                                    Object value) {
        if (rwt == null) {
            rwt = broker.newReadWriteTransaction();
        }
        rwt.put(type, iid, (T) value, true);
    }

    public ReadWriteTransaction getTx() {
        return rwt;
    }

    public Set<Identifier<?>> getReadData() {
        return readData;
    }

    public Set<Identifier<?>> getWriteData() {
        return writeData;
    }

    public List<Identifier<?>> getRpcData() {
        return rpcData;
    }
}
