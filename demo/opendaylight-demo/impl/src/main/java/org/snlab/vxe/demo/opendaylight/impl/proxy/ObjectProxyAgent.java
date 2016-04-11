/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.opendaylight.controller.md.sal.binding.impl.BindingToNormalizedNodeCodec;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.vxe.api.Identifier;
import org.snlab.vxe.demo.opendaylight.impl.VxeOpenDaylightIdentifier;

public final class ObjectProxyAgent {

    @SuppressWarnings("unused")
    private static Logger LOG = LoggerFactory.getLogger(ObjectProxyAgent.class);

    private Set<Identifier<?>> readData;
    private Set<Identifier<?>> writeData;
    private InstanceIdentifierGenerator generator;

    public ObjectProxyAgent(BindingToNormalizedNodeCodec codec,
                            Set<Identifier<?>> readData, Set<Identifier<?>> writeData) {
        this.generator = new InstanceIdentifierGenerator(codec);
        this.readData = readData;
        this.writeData = writeData;
    }

    @SuppressWarnings("unchecked")
    public <T> T wrap(Class<T> clazz, Object instance,
                        Identifier<? extends DataObject> id) {
        Class<?>[] interfaces = { clazz };
        if (!clazz.isInterface()) {
            interfaces = clazz.getInterfaces();
        }
        VxeOpenDaylightIdentifier<?> vid = (VxeOpenDaylightIdentifier<?>) id;
        InvocationHandler handler = new ProxyHandler<T>(instance, vid);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, handler);
    }

    private class ProxyHandler<T> implements InvocationHandler {

        private Object instance = null;
        private VxeOpenDaylightIdentifier<?> id;

        public ProxyHandler(Object instance, VxeOpenDaylightIdentifier<?> id) {
            this.instance = instance;
            this.id = id;
        }

        @Override
        public Object invoke(Object proxy, Method method,
                                Object[] args) throws Throwable {
            Object obj = method.invoke(instance, args);
            Class<?> returnType = method.getReturnType();

            InstanceIdentifier<?> iid = id.getInstanceIdentifier();
            readData.add(id);

            if (obj.getClass().isPrimitive()) {
                return obj;
            } else if (!returnType.isInterface()) {
                return obj;
            } else if (DataObject.class.isAssignableFrom(returnType)) {
                InstanceIdentifier<?> next = generator.get(iid, returnType, obj);
                Identifier<? extends DataObject> nid = createFromId(id, next);
                return wrap(returnType, obj, nid);
            } else if (List.class.isAssignableFrom(returnType)) {
                Class<?> elementType = (Class<?>)((ParameterizedType)method
                                                    .getGenericReturnType())
                                                    .getActualTypeArguments()[0];
                return wrapList((List<?>) obj, elementType);
            }
            return wrap(returnType, obj, id);
        }

        @SuppressWarnings("unchecked")
        private <E> Object wrapList(List<E> list, Class<?> elementType) {
            InstanceIdentifier<?> iid = id.getInstanceIdentifier();

            InstanceIdentifier<?> listIid = generator.list(iid, elementType);
            Identifier<? extends DataObject> listId = createFromId(id, listIid);

            readData.add(listId);

            List<E> newList = new LinkedList<E>();
            for (E e: list) {
                InstanceIdentifier<?> next = generator.get(iid, elementType, e);
                Identifier<? extends DataObject> nid = createFromId(id, next);

                newList.add((E) wrap(elementType, e, nid));
            }
            return newList;
        }

        private <N extends DataObject> VxeOpenDaylightIdentifier<N> createFromId(
                                                    VxeOpenDaylightIdentifier<?> id,
                                                    InstanceIdentifier<N> next) {
            return new VxeOpenDaylightIdentifier<N>(id.getType(), next);
        }

    }
}
