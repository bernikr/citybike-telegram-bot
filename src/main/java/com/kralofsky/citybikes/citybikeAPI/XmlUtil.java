package com.kralofsky.citybikes.citybikeAPI;

import java.util.*;
import java.util.stream.Collectors;

import org.w3c.dom.*;

public class XmlUtil {
    private XmlUtil() {}

    public static Map<String, List<Node>> asMap(NodeList nodeList) {
        return asList(nodeList).stream().filter(n -> n.getNodeType() == Node.ELEMENT_NODE).collect(Collectors.groupingBy(Node::getNodeName));
    }

    public static List<Node> asList(NodeList nodeList) {
        return nodeList.getLength() == 0 ? Collections.emptyList() : new NodeListWrapper(nodeList);
    }

    private static class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
        private final NodeList list;

        NodeListWrapper(NodeList l) {
            list = l;
        }

        public Node get(int index) {
            return list.item(index);
        }

        public int size() {
            return list.getLength();
        }
    }
}