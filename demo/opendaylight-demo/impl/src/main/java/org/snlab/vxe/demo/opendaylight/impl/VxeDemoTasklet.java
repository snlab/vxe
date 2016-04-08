/*
 * Copyright © 2016 SNLAB and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.snlab.vxe.demo.opendaylight.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.host.tracker.rev140624.HostNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.LinkId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TpId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;

import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import org.snlab.vxe.api.Datastore;
import org.snlab.vxe.api.VxeDatastore;
import org.snlab.vxe.api.VxeEntryPoint;
import org.snlab.vxe.api.VxeTasklet;

@VxeTasklet
public class VxeDemoTasklet {

    private Datastore datastore = null;

    private final TopologyKey topologyKey = new TopologyKey(new TopologyId("flow:1"));

    @VxeEntryPoint
    public void findPath(MacAddress source, MacAddress destination,
                            @VxeDatastore Datastore datastore) {

        this.datastore = datastore;

        Node src = getAttachmentPoint(source);
        Node dst = getAttachmentPoint(destination);

        Topology topology = getTopology();

        List<Link> path = bfs(topology, src, dst);

        setupPath(source, destination, path);
    }

    private void setupPath(MacAddress source, MacAddress destination,
                            List<Link> path) {
        for (Link link: path) {
            System.out.println("Setting up path for "
                                + "<" + source.getValue() + ","
                                + destination.getValue() + ">"
                                + "on switch "
                                + link.getSource().getSourceNode().getValue());
        }
    }

    private Topology getTopology() {
        InstanceIdentifier<Topology> iid;
        iid = InstanceIdentifier.builder(NetworkTopology.class)
                                .child(Topology.class, topologyKey).build();
        VxeOpenDaylightIdentifier<Topology> vid = new VxeOpenDaylightIdentifier<>(
            LogicalDatastoreType.OPERATIONAL, iid
        );

        return datastore.read(vid);
    }

    private Node getAttachmentPoint(MacAddress mac) {
        NodeKey nodeKey = new NodeKey(new NodeId("host:" + mac.getValue()));

        InstanceIdentifier<Node> iid;
        iid = InstanceIdentifier.builder(NetworkTopology.class)
                                .child(Topology.class, topologyKey)
                                .child(Node.class, nodeKey).build();

        VxeOpenDaylightIdentifier<Node> vid = new VxeOpenDaylightIdentifier<>(
                LogicalDatastoreType.OPERATIONAL, iid
        );
        Node node = datastore.read(vid);

        return node;
    }

    private static class Tuple {
        private NodeId nodeId = null;
        private int dist = Integer.MAX_VALUE;
        private LinkId linkId = null;
        private Tuple previous = null;

        Tuple(NodeId nodeId, int dist, LinkId linkId, Tuple previous) {
            this.nodeId = nodeId;
            this.dist = dist;
            this.linkId = linkId;
            this.previous = previous;
        }

        public NodeId getNodeId() {
            return nodeId;
        }

        public int getDistance() {
            return dist;
        }

        public LinkId getLinkId() {
            return linkId;
        }

        public Tuple getPrevious() {
            return previous;
        }
    }

    private static final List<LinkId> EMPTY_LIST = new LinkedList<LinkId>();

    private List<Link> bfs(Topology topology, Node src, Node dst) {
        List<Node> nodes = topology.getNode();
        List<Link> links = topology.getLink();

        Map<NodeId, List<LinkId>> graph = new HashMap<NodeId, List<LinkId>>();
        Map<NodeId, Node> nodeMap = new HashMap<NodeId, Node>();
        Map<LinkId, Link> linkMap = new HashMap<LinkId, Link>();

        for (Node node: nodes) {
            nodeMap.put(node.getNodeId(), node);
        }

        for (Link link: links) {
            linkMap.put(link.getLinkId(), link);
        }

        for (Link link: links) {
            NodeId source = link.getSource().getSourceNode();

            List<LinkId> edges = graph.getOrDefault(source, new LinkedList<LinkId>());
            edges.add(link.getLinkId());

            graph.put(source, edges);
        }

        LinkedList<Tuple> queue = new LinkedList<>();
        queue.addLast(new Tuple(src.getNodeId(), 0, null, null));

        while (!queue.isEmpty()) {
            Tuple tuple = queue.pollFirst();

            Node node = nodeMap.get(tuple.getNodeId());
            if (node.getNodeId().equals(dst.getNodeId())) {
                LinkedList<Link> path = new LinkedList<>();
                while (tuple.getPrevious() != null) {
                    Tuple previous = tuple.getPrevious();
                    if (previous.getPrevious() != null) {
                        path.addFirst(linkMap.get(tuple.getLinkId()));
                    }

                    tuple = previous;
                }
                return path;
            }
            nodeMap.remove(node);
            /* remove visited nodes */

            int dist = tuple.getDistance();

            List<LinkId> edges = graph.getOrDefault(node.getNodeId(), EMPTY_LIST);
            for (LinkId linkId: edges) {
                Link link = linkMap.get(linkId);

                NodeId otherId = link.getDestination().getDestNode();
                Node other = nodeMap.get(otherId);
                if (other == null) {
                    /* Already visited */
                    continue;
                }

                queue.addLast(new Tuple(otherId, dist + 1, linkId, tuple));
            }
        }
        return null;
    }
}
