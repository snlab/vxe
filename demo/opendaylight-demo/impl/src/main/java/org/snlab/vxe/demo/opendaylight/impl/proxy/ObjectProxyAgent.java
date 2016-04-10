/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.vxe.api.Identifier;
import org.snlab.vxe.demo.opendaylight.impl.VxeOpenDaylightIdentifier;

public final class ObjectProxyAgent {

    private static Logger LOG = LoggerFactory.getLogger(ObjectProxyAgent.class);

    private Set<Identifier<?>> readData;
    private Set<Identifier<?>> writeData;

    public ObjectProxyAgent(Set<Identifier<?>> readData, Set<Identifier<?>> writeData) {
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
        InvocationHandler handler = new ProxyHandler<T>(instance, id);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, handler);
    }

    private class ProxyHandler<T> implements InvocationHandler {

        private Object instance = null;
        private Identifier<? extends DataObject> id;

        public ProxyHandler(Object instance, Identifier<? extends DataObject> id) {
            this.instance = instance;
            this.id = id;
        }

        @Override
        public Object invoke(Object proxy, Method method,
                                Object[] args) throws Throwable {
            Object obj = method.invoke(instance, args);
            Class<?> returnType = method.getReturnType();

            if (obj.getClass().isPrimitive()) {
                return obj;
            } else if (!returnType.isInterface()) {
                return obj;
            } else if (DataObject.class.isAssignableFrom(returnType)) {
                return wrap(returnType, obj, id);
            }
            return wrap(returnType, obj, id);
        }

    }
}
