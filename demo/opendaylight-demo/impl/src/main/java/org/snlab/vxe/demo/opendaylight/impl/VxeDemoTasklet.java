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
import java.util.Objects;

import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.FlowTableRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.transaction.rev150304.TransactionId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetDestination;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetDestinationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetSource;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetSourceBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.LinkId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.snlab.vxe.api.Datastore;
import org.snlab.vxe.api.Identifier;
import org.snlab.vxe.api.annotation.VxeDatastore;
import org.snlab.vxe.api.annotation.VxeEntryPoint;
import org.snlab.vxe.api.annotation.VxeTasklet;

import com.google.common.collect.ImmutableList;

@VxeTasklet
public class VxeDemoTasklet {

    private static final short DEFAULT_TABLE_ID = 0;
    private static final int DEFAULT_PRIORITY = 20;

    private final Integer DEFAULT_HARD_TIMEOUT = 3600;
    private final Integer DEFAULT_IDLE_TIMEOUT = 1800;
    private final Long OFP_NO_BUFFER = Long.valueOf(4294967295L);

    private Datastore datastore = null;
    private TransactionId transactionId = null;

    private static final TopologyKey topologyKey
        = new TopologyKey(new TopologyId("flow:1"));

    @SuppressWarnings("deprecation")
    private static final Class<org.opendaylight.yang.gen.v1
        .urn.opendaylight.inventory.rev130819.nodes.Node> INV_NODE_CLASS
            = org.opendaylight.yang.gen.v1
                .urn.opendaylight.inventory.rev130819.nodes.Node.class;

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
        int counter = 0;
        for (Link link: path) {
            System.out.println("Setting up path for "
                                + "<" + source.getValue() + ","
                                + destination.getValue() + ">"
                                + "on switch "
                                + link.getSource().getSourceNode().getValue());


            if (counter > 0) {
                // Not the first link, set up src -> dst
                String swid = link.getSource().getSourceNode().getValue();
                String tpid = link.getSource().getSourceTp().getValue();

                NodeConnectorRef ref = getNodeConnectorRef(swid, tpid);
                setupSwitch(source, destination, ref, tpid);
            }
            ++counter;
            if (counter < path.size()) {
                // Not the last link, set up dst -> src
                String swid = link.getDestination().getDestNode().getValue();
                String tpid = link.getDestination().getDestTp().getValue();

                NodeConnectorRef ref = getNodeConnectorRef(swid, tpid);
                setupSwitch(destination, source, ref, tpid);
            }
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private void setupSwitch(MacAddress source, MacAddress destination,
                                NodeConnectorRef connector, String tpid) {
        TableKey tableKey = new TableKey((short) DEFAULT_TABLE_ID);
        FlowId flowId = new FlowId(Long.toString(Objects.hash(source, destination)));
        FlowKey flowKey = new FlowKey(flowId);

        InstanceIdentifier<NodeConnector> iid;
        iid = (InstanceIdentifier<NodeConnector>) connector.getValue();

        InstanceIdentifier<Table> tiid;
        tiid = iid.firstIdentifierOf(INV_NODE_CLASS)
                    .augmentation(FlowCapableNode.class)
                    .child(Table.class, tableKey);
        FlowTableRef tableRef = new FlowTableRef(tiid);

        InstanceIdentifier<Flow> fiid;
        fiid = tiid.child(Flow.class, flowKey);
        FlowRef flowRef = new FlowRef(fiid);

        EthernetSource src = new EthernetSourceBuilder()
                                    .setAddress(source).build();
        EthernetDestination dst = new EthernetDestinationBuilder()
                                        .setAddress(destination).build();
        EthernetMatch ethMatch = new EthernetMatchBuilder()
                                    .setEthernetSource(src)
                                    .setEthernetDestination(dst).build();
        Match match = new MatchBuilder().setEthernetMatch(ethMatch).build();

        Uri port = new NodeConnectorId(tpid);
        OutputAction outAction = new OutputActionBuilder()
                                        .setMaxLength(0xffff)
                                        .setOutputNodeConnector(port).build();
        OutputActionCase outputCase = new OutputActionCaseBuilder()
                                            .setOutputAction(outAction).build();
        Action action = new ActionBuilder()
                                .setOrder(0)
                                .setAction(outputCase).build();

        ApplyActions operation = new ApplyActionsBuilder()
                                        .setAction(ImmutableList.of(action)).build();
        ApplyActionsCase operationCase = new ApplyActionsCaseBuilder()
                                        .setApplyActions(operation).build();
        Instruction ins = new InstructionBuilder()
                                .setOrder(0)
                                .setInstruction(operationCase).build();
        Instructions instructions = new InstructionsBuilder()
                                        .setInstruction(ImmutableList.of(ins)).build();

        Flow flow = new FlowBuilder()
                        .setTableId(DEFAULT_TABLE_ID)
                        .setFlowName("mac2mac")
                        .setId(flowId)
                        .setFlags(new FlowModFlags(false, false, false, false, false))
                        .setMatch(match)
                        .setInstructions(instructions)
                        .setPriority(DEFAULT_PRIORITY)
                        .setBufferId(OFP_NO_BUFFER)
                        .setHardTimeout(DEFAULT_HARD_TIMEOUT)
                        .setIdleTimeout(DEFAULT_IDLE_TIMEOUT).build();

        NodeRef nodeRef = new NodeRef(iid.firstIdentifierOf(INV_NODE_CLASS));
        Uri flowTransaction = new Uri(flow.getId().getValue());
        if (transactionId != null) {
            flowTransaction = new Uri(transactionId.getValue().toString());
        }
        AddFlowInput input = new AddFlowInputBuilder(flow)
                                    .setNode(nodeRef)
                                    .setFlowRef(flowRef)
                                    .setFlowTable(tableRef)
                                    .setTransactionUri(flowTransaction).build();
        Identifier<AddFlowOutput> id = new VxeOpenDaylightRpc<AddFlowInput, AddFlowOutput>(input);

        AddFlowOutput output = datastore.read(id);
        transactionId = output.getTransactionId();
    }

    @SuppressWarnings("deprecation")
    private NodeConnectorRef getNodeConnectorRef(String swid, String tpid) {
        InstanceIdentifier<NodeConnector> iid;

        org.opendaylight.yang.gen.v1
            .urn.opendaylight.inventory.rev130819.NodeId nodeId;
        nodeId = new org.opendaylight.yang.gen.v1
            .urn.opendaylight.inventory.rev130819.NodeId(swid);
        org.opendaylight.yang.gen.v1
            .urn.opendaylight.inventory.rev130819.nodes.NodeKey nodeKey;
        nodeKey = new org.opendaylight.yang.gen.v1
            .urn.opendaylight.inventory.rev130819.nodes.NodeKey(nodeId);

        NodeConnectorKey nconnKey = new NodeConnectorKey(new NodeConnectorId(tpid));
        iid = InstanceIdentifier.builder(Nodes.class)
            .child(INV_NODE_CLASS, nodeKey)
            .child(NodeConnector.class, nconnKey).build();

        NodeConnectorRef ref = new NodeConnectorRef(iid);

        return ref;
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
                    path.addFirst(linkMap.get(tuple.getLinkId()));

                    tuple = tuple.getPrevious();
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
