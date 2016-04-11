/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.impl.BindingToNormalizedNodeCodec;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.vxe.api.Identifier;
import org.snlab.vxe.api.Tasklet;
import org.snlab.vxe.api.TaskletFactory;
import org.snlab.vxe.api.VirtualExecutionEnvironment;
import org.snlab.vxe.api.annotation.VxeDatastore;
import org.snlab.vxe.api.annotation.VxeEntryPoint;
import org.snlab.vxe.api.annotation.VxeTasklet;

public class VxeOpenDaylight implements VirtualExecutionEnvironment {

    private static Logger LOG = LoggerFactory.getLogger(VxeOpenDaylight.class);

    private class VxeOpenDaylightTasklet<T> implements Tasklet<T> {

        private Method main = null;
        private Object instance = null;
        private Object[] parameters = null;
        private Annotation[] vxeParameterTypes = null;

        public VxeOpenDaylightTasklet(Method main, Object instance,
                                        Object[] parameters,
                                        Annotation[] vxeParameterTypes) {
            this.main = main;
            this.instance = instance;
            this.parameters = parameters;
            this.vxeParameterTypes = vxeParameterTypes;
        }

        @Override
        public void submit() {
            VxeOpenDaylightDatastore datastore = null;

            List<Object> parameters = new LinkedList<>(Arrays.asList(this.parameters));
            for (Annotation annotation: vxeParameterTypes) {
                if (annotation.annotationType().equals(VxeDatastore.class)) {
                    datastore = new VxeOpenDaylightDatastore(broker, VxeOpenDaylight.this);
                    parameters.add(datastore);
                } else {
                    parameters.add(null);
                }
            }

            try {
                main.invoke(instance, parameters.toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            for (Identifier<?> id: datastore.getReadData()) {
                VxeOpenDaylightIdentifier<?> vid = (VxeOpenDaylightIdentifier<?>) id;
                LOG.info("Read {}", vid.getInstanceIdentifier());
            }

            try {
                ReadWriteTransaction rwt = datastore.getTx();
                if (rwt != null) {
                    rwt.submit().get();
                }
            } catch (InterruptedException | ExecutionException e) {
            }
        }

        @Override
        public void cancel() {
            // TODO Auto-generated method stub

        }

    }

    private class VxeOpenDaylightTaskletFactory<T> implements TaskletFactory<T> {

        private Class<T> clazz = null;
        private Method main = null;
        private Annotation[] vxeParameterTypes = null;

        public VxeOpenDaylightTaskletFactory(Class<T> clazz, Method main,
                                                Annotation[] vxeParameterTypes) {
            this.clazz = clazz;
            this.main = main;
            this.vxeParameterTypes = vxeParameterTypes;
        }

        @Override
        public Tasklet<T> create(Object... parameters) {
            int actualLength = parameters.length + vxeParameterTypes.length;
            if (actualLength != main.getParameters().length) {
                //TODO: error - parameters mismatch
                LOG.warn("Parameters mismatch: {}", parameters);
                return null;
            }
            try {
                Object instance = clazz.newInstance();
                return new VxeOpenDaylightTasklet<T>(main, instance,
                                                        parameters,
                                                        vxeParameterTypes);
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
    }

    private DataBroker broker;
    private Map<Class<?>, TaskletFactory<?>> factoryMap = null;
    private BindingToNormalizedNodeCodec codec = null;

    public VxeOpenDaylight(DataBroker broker, BindingToNormalizedNodeCodec codec) {
        this.broker = broker;
        this.codec = codec;

        this.factoryMap = new HashMap<Class<?>, TaskletFactory<?>>();
        this.rpcMap = new HashMap<Class<?>, OpenDaylightRpc<?, ?>>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TaskletFactory<T> register(Class<T> taskletType) {
        TaskletFactory<?> factory = factoryMap.get(taskletType);
        if (factory == null) {
            if (!taskletType.isAnnotationPresent(VxeTasklet.class)) {
                // TODO: no annotation found
                LOG.error("Failed to find annotation {} in {}", "VxeTasklet", taskletType);
                return null;
            }

            try {
                if (taskletType.getConstructor() == null) {
                    LOG.error("Failed to find a default constructor in {}", taskletType);
                    return null;
                }
            } catch (NoSuchMethodException e) {
                LOG.error("Failed to find a default constructor in {}", taskletType);
                return null;
            }

            Method main = null;
            for (Method method: taskletType.getMethods()) {
                if (method.isAnnotationPresent(VxeEntryPoint.class)) {
                    if (main != null) {
                        // TODO: error - too many entry points
                        LOG.error("Too many entry points for {}", taskletType);
                        return null;
                    }

                    main = method;
                }
            }

            if (main == null) {
                // TODO: error - no entry point found
                LOG.error("No entry point found in {}", taskletType);
                return null;
            }

            List<Annotation> vxeTypes = new LinkedList<>();
            for (Annotation[] annotations: main.getParameterAnnotations()) {
                Annotation vxeType = null;
                for (Annotation annotation: annotations) {
                    if (annotation.annotationType().equals(VxeDatastore.class)) {
                        if (vxeType != null) {
                            // TODO: error - parameter has multiple annotations
                            LOG.error("Some parameter has too many VXE annotations");
                            return null;
                        }
                        vxeType = annotation;
                    }
                    if ((vxeType == null) && (vxeTypes.size() > 0)) {
                        // TODO: error - annotated before normal parameters
                        LOG.error("VXE annotations must come after non-annotated parameters");
                        return null;
                    }
                    vxeTypes.add(vxeType);
                }
            }
            Annotation[] vxeParameterTypes = vxeTypes.toArray(new Annotation[0]);

            factory = new VxeOpenDaylightTaskletFactory<T>(taskletType, main,
                                                            vxeParameterTypes);

            factoryMap.put(taskletType, factory);
        }
        return (TaskletFactory<T>) factory;
    }

    public static interface OpenDaylightRpc<I extends DataObject, O extends DataObject> {

        public Future<RpcResult<O>> process(I input);

        public Future<RpcResult<Void>> clear(I input);

    }

    private Map<Class<?>, OpenDaylightRpc<?, ?>> rpcMap = null;

    public <I extends DataObject> void registerRpc(Class<I> input, OpenDaylightRpc<I, ?> rpc) {
        rpcMap.put(input, rpc);
    }

    public OpenDaylightRpc<?, ?> getRpc(DataObject input) {
        OpenDaylightRpc<?, ?> rpc = null;

        for (Class<?> clazz: input.getClass().getInterfaces()) {
            rpc = rpcMap.get(clazz);
            if (rpc != null) {
                break;
            }
        }

        return rpc;
    }

    public BindingToNormalizedNodeCodec getCodec() {
        return codec;
    }
}
