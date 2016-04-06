package org.snlab.vxe.demo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.snlab.vxe.api.Datastore;
import org.snlab.vxe.api.Identifier;

import org.snlab.vxe.api.VxeDatastore;
import org.snlab.vxe.api.VxeEntryPoint;
import org.snlab.vxe.api.VxeTasklet;
import org.snlab.vxe.demo.datastore.Link;
import org.snlab.vxe.demo.datastore.Node;
import org.snlab.vxe.demo.datastore.Path;
import org.snlab.vxe.demo.datastore.PathKey;
import org.snlab.vxe.demo.datastore.Topology;
import org.snlab.vxe.demo.datastore.TopologyKey;

@VxeTasklet
public class DemoTasklet {

    static class Tuple {

        private Node node = null;
        private int bandwidth = 0;
        private Link link = null;

        public Tuple(Node node, int bandwidth, Link link) {
            this.node = node;
            this.bandwidth = bandwidth;
            this.link = link;
        }

        public int getBandwidth() {
            return bandwidth;
        }

        public Node getNode() {
            return node;
        }

        public Link getLink() {
            return link;
        }

        public void setBandwidth(int newBandwidth) {
            bandwidth = newBandwidth;
        }

    }

    private static final Tuple NULL_TUPLE = new Tuple(null, Integer.MAX_VALUE, null);

    private String source = null;
    private String destination = null;

    @VxeEntryPoint
    public void myMain(String source, String destination,
                        @VxeDatastore Datastore datastore) {
        this.source = source;
        this.destination = destination;

        Identifier<Topology> tid = new TopologyKey("demo");
        Topology topology = datastore.read(tid);

        final LinkedList<Link> path = new LinkedList<Link>();
        int maximumBandwidth = findMaximum(topology, path);

        System.out.println("Find a path with the maximum bandwidth of " + maximumBandwidth);

        datastore.write(new PathKey("demo-path"), new Path() {
            @Override
            public List<Link> getLinks() {
                return path;
            }
        });
    }

    private String otherNodeId(Link link, Node node) {
        return (node.getNodeId() == link.getEndpoint1()
                ? link.getEndpoint2() : link.getEndpoint1());
    }

    private int findMaximum(Topology topology, LinkedList<Link> path) {
        List<Link> links = topology.getLinks();
        List<Node> nodes = topology.getNodes();

        Map<String, Link> linkMap = new HashMap<String, Link>();
        Map<String, Node> nodeMap = new HashMap<String, Node>();
        Map<Node, Tuple> tupleMap = new HashMap<Node, Tuple>();

        for (Link link: links) {
            linkMap.put(link.getLinkId(), link);
        }

        for (Node node: nodes) {
            nodeMap.put(node.getNodeId(), node);
        }

        Node src = nodeMap.getOrDefault(source, null);
        Node dst = nodeMap.getOrDefault(destination, null);

        Queue<Tuple> queue = new PriorityQueue<Tuple>(new Comparator<Tuple>() {
            @Override
            public int compare(Tuple x, Tuple y) {
                int bwx = x.getBandwidth();
                int bwy = y.getBandwidth();

                return (bwx > bwy ? -1 : 1);
            }
        });

        Tuple srcTuple = new Tuple(src, Integer.MAX_VALUE, null);
        tupleMap.put(src, srcTuple);

        queue.add(srcTuple);
        while (!queue.isEmpty()) {
            Tuple tuple = queue.poll();

            Node node = tuple.getNode();
            int bandwidth = tuple.getBandwidth();

            if (node == dst) {
                while (tuple.getLink() != null) {
                    Link link = tuple.getLink();
                    path.addFirst(link);

                    node = nodeMap.get(otherNodeId(link, node));
                    tuple = tupleMap.get(node);
                }
                return bandwidth;
            }

            if (bandwidth > tupleMap.get(node).getBandwidth()) {
                continue;
            }

            for (String linkId: node.getLinkIds()) {
                Link link = linkMap.getOrDefault(linkId, null);

                if (link == null) {
                    continue;
                }

                Node other = nodeMap.get(otherNodeId(link, node));
                if (other == null) {
                    continue;
                }

                Tuple otherTuple = tupleMap.getOrDefault(other, NULL_TUPLE);
                if (bandwidth <= otherTuple.getBandwidth()) {
                    continue;
                }

                otherTuple = new Tuple(other, bandwidth, link);
                tupleMap.put(other, otherTuple);

                queue.add(otherTuple);
            }
        }
        return 0;
    }
}
