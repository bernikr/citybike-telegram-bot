package com.kralofsky.citybikes.citybikeAPI;


import com.kralofsky.citybikes.entity.Location;
import com.kralofsky.citybikes.entity.Station;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BasicStationAPI implements StationAPI {
    private final ApiUrls apiUrls;

    @Autowired
    public BasicStationAPI(ApiUrls apiUrls) {
        this.apiUrls = apiUrls;
    }

    @Override
    public Collection<Station> getAllStations() throws ApiException {
        log.info("getAllStations() from API");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(apiUrls.getStationApiUrl().openStream());
            Element root = doc.getDocumentElement();

            List<Station> stationList = new ArrayList<>();
            for (Node n: XmlUtil.asList(root.getElementsByTagName("station"))) {
                stationList.add(nodeToStation(n));
            }
            return stationList;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ApiException(e.getMessage(), e);
        }
    }

    private Station nodeToStation(Node node) throws ApiException {
        Collection<List<Node>> nodes = XmlUtil.asMap(node.getChildNodes()).values();
        if(nodes.stream().anyMatch(l -> l.size() > 1))
            throw new ApiException("API format error: Station contains more than one of a child node");
        Map<String, String> values = nodes.stream()
                .map(l -> l.get(0))
                .collect(Collectors.toMap(Node::getNodeName,
                        n -> n.getTextContent().trim().replaceAll("\n\\s+", "\n")));
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
                    new Location(
                        Double.valueOf(values.get("latitude")),
                        Double.valueOf(values.get("longitude"))
                    )
            );
        } catch (NullPointerException | NumberFormatException e) {
            throw new ApiException("API format error: missing fields", e);
        }
    }

    private Station.Status statusFromString(String s) throws ApiException {
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
