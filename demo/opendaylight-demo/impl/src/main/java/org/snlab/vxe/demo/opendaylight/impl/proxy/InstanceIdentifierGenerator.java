/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl.proxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.opendaylight.controller.md.sal.binding.impl.BindingToNormalizedNodeCodec;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Identifiable;
import org.opendaylight.yangtools.yang.binding.Identifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier;
import org.opendaylight.yangtools.yang.data.api.YangInstanceIdentifier.NodeIdentifierWithPredicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

public final class InstanceIdentifierGenerator {

    private BindingToNormalizedNodeCodec codec = null;

    private static Logger LOG = LoggerFactory.getLogger(InstanceIdentifierGenerator.class);

    public InstanceIdentifierGenerator(BindingToNormalizedNodeCodec codec) {
        this.codec = codec;
    }

    @SuppressWarnings("unchecked")
    public <T extends DataObject> InstanceIdentifier<?> get(InstanceIdentifier<T> root,
                                        Class<?> clazz, Object obj) {
        if (Augmentation.class.isAssignableFrom(clazz)) {
            return augmentation(root, (Class<AugmentationDataObject<? super T>>)clazz);
        } else if (ChildOf.class.isAssignableFrom(clazz)) {
            if (Identifiable.class.isAssignableFrom(clazz)) {
                Identifier<?> key = ((Identifiable<?>) obj).getKey();
                return childWithKey(root, clazz, key);
            } else {
                return child(root, (Class<ChildOfDataObject<T>>)clazz);
            }
        } else if (List.class.isAssignableFrom(clazz)) {
            return child(root, (Class<ChildOfDataObject<T>>)clazz);
        }
        return root;
    }

    public InstanceIdentifier<?> childWithKey(InstanceIdentifier<?> root,
                                                Class<?> clazz, Identifier<?> key) {
        QName qname = extractNodeQName(clazz);
        Map<QName, Object> ykey = extractKey(qname, key);

        YangInstanceIdentifier yiid;
        yiid = codec.toYangInstanceIdentifier(root).node(qname)
                    .node(new NodeIdentifierWithPredicates(qname, ykey));

        LOG.debug("Before: {}", codec.toYangInstanceIdentifier(root));
        LOG.debug("After: {}", yiid);

        InstanceIdentifier<?> iid = codec.fromYangInstanceIdentifier(yiid);
        LOG.debug("Result: {}", iid);
        return iid;
    }

    public static interface ChildOfDataObject<T> extends DataObject, ChildOf<T> {
    }

    public <N extends DataObject & ChildOf<T>, T extends DataObject>
        InstanceIdentifier<?> child(InstanceIdentifier<T> root, Class<N> clazz) {
        return root.child(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends DataObject> InstanceIdentifier<?> list(InstanceIdentifier<T> root,
                                                                Class<?> elementType) {
        return child(root, (Class<ChildOfDataObject<T>>)elementType);
    }

    private QName extractNodeQName(Class<?> clazz) {
        try {
            return (QName)clazz.getField("QNAME").get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private String render(String method) {
        StringBuffer sb = new StringBuffer();
        for (int i = 3; i < method.length(); ++i) {
            char c = method.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 3) {
                    sb.append('-');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return new String(sb);
    }

    private Map<QName, Object> extractKey(QName nodeQName, Object identifier) {
        Class<?> clazz = identifier.getClass();
        if (Identifier.class.isAssignableFrom(clazz)) {
            //ok this is cheating
            for (Method method: clazz.getMethods()) {
                if (method.getName().startsWith("get")) {
                    String localName = render(method.getName());
                    try {
                        QName qname = QName.create(nodeQName, localName);
                        Object key = method.invoke(identifier);
                        if (!String.class.isAssignableFrom(key.getClass())) {
                            Method gv = key.getClass().getMethod("getValue");
                            key = gv.invoke(key);
                        }
                        return ImmutableMap.of(qname, key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static interface AugmentationDataObject<T>
            extends DataObject, Augmentation<T> {
    }

    public <N extends DataObject & Augmentation<? super T>, T extends DataObject>
        InstanceIdentifier<?> augmentation(InstanceIdentifier<T> root,
                                                Class<N> clazz) {
        InstanceIdentifier<N> niid = root.augmentation(clazz);
        return niid;
    }
}
