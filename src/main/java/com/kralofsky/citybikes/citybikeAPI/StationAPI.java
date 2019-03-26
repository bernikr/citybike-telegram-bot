package com.kralofsky.citybikes.citybikeAPI;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StationAPI {
    private static String API_PATH = "http://dynamisch.citybikewien.at/citybike_xml.php";

    public static Collection<Station> getAllStations() throws ApiException {
        Element root;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(new URL(API_PATH).openStream());
            root = doc.getDocumentElement();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ApiException(e.getMessage(), e);
        }
        List<Station> stationList = new ArrayList<>();
        for (Node n: XmlUtil.asList(root.getElementsByTagName("station"))) {
            stationList.add(nodeToStation(n));
        }
        return stationList;
    }

    private static Station nodeToStation(Node n) throws ApiException {
        Collection<List<Node>> nodes = XmlUtil.asMap(n.getChildNodes()).values();
        if(nodes.stream().anyMatch(l -> l.size() > 1))
            throw new ApiException("API format error: Station contains more than one of a child node");
        Map<String, String> values = nodes.stream()
                .map(l -> l.get(0))
                .collect(Collectors.toMap(Node::getNodeName, Node::getTextContent));
        try {
            return new Station(
                    Integer.valueOf(values.get("id")),
                    Integer.valueOf(values.get("internal_id")),
                    values.get("name"),
                    Integer.valueOf(values.get("boxes")),
                    Integer.valueOf(values.get("free_boxes")),
                    Integer.valueOf(values.get("free_bikes")),
                    statusFromString(values.get("status")),
                    values.get("description"),
                    Double.valueOf(values.get("latitude")),
                    Double.valueOf(values.get("longitude"))
            );
        } catch (NullPointerException | NumberFormatException e) {
            throw new ApiException("API format error: missing fields", e);
        }
    }

    private static Station.Status statusFromString(String s) throws ApiException {
        switch (s) {
            case "aktiv":
                return Station.Status.ACTIVE;
            case "nicht in Betrieb":
                return Station.Status.INOPERATIVE;
            case "in Bau":
                return Station.Status.UNDER_CONSTRUCTION;
            default:
                throw new ApiException("API Error: Unknown Status '" + s + "'");
        }
    }
}
