package com.rule606.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class XmlUtils {

    public static String getElementValue(Element root, String tagName) {
        NodeList nodes = root.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return "";
        Node first = nodes.item(0);
        if (first.getFirstChild() == null) return "";
        return first.getFirstChild().getNodeValue();
    }

    public static String getNodeValue(Node node, boolean skipCommas) {
        if (node.getFirstChild() == null) return "";
        String val = node.getFirstChild().getNodeValue();
        if (val == null) return "";
        if (!skipCommas) {
            val = addCommas(val);
        }
        return val;
    }

    public static String getNodeValue(Node node) {
        return getNodeValue(node, false);
    }

    public static List<Element> getPreviousSiblings(Element node) {
        List<Element> siblings = new ArrayList<>();
        Node current = node;
        while (current != null) {
            if (current instanceof Element e) {
                siblings.add(e);
            }
            current = current.getPreviousSibling();
        }
        return siblings;
    }

    public static List<Element> getNextSiblings(Element node) {
        List<Element> siblings = new ArrayList<>();
        Node current = node;
        while (current != null) {
            if (current instanceof Element e) {
                siblings.add(e);
            }
            current = current.getNextSibling();
        }
        return siblings;
    }

    public static List<Element> getParents(Node node) {
        List<Element> parents = new ArrayList<>();
        Node current = node;
        while (current != null) {
            if (current instanceof Element e) {
                parents.add(e);
            }
            current = current.getParentNode();
        }
        return parents;
    }

    public static String getVenueName(Element venueElt) {
        String venueNameVal = "";
        String[] tags = {"venueName", "name", "services", "mic", "mpid", "otherNames"};
        for (String tag : tags) {
            String nodeVal = "";
            NodeList children = venueElt.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child instanceof Element e && e.getTagName().equals(tag)) {
                    nodeVal = getNodeValue(e, true);
                    nodeVal = nodeVal.replaceAll("\\s+", " ").trim();
                    break;
                }
            }
            switch (tag) {
                case "venueName", "name" -> venueNameVal += nodeVal;
                case "services", "mic", "mpid" -> {
                    if (!nodeVal.isEmpty()) {
                        venueNameVal += " (" + nodeVal + ")";
                        venueNameVal = venueNameVal.trim();
                    }
                }
            }
        }
        return venueNameVal.isEmpty() ? "\u200B" : venueNameVal;
    }

    public static String addCommas(String number) {
        if (number == null || number.isEmpty()) return number;
        String[] parts = number.split("\\.");
        String intPart = parts[0].replaceAll("(\\d)(?=(\\d{3})+(?!\\d))", "$1,");
        if (parts.length > 1) {
            return intPart + "." + parts[1];
        }
        return intPart;
    }

    public static List<Element> getChildElements(Element parent) {
        List<Element> elements = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element e) {
                elements.add(e);
            }
        }
        return elements;
    }

    public static Element getFirstChildByTag(Element parent, String tagName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element e && e.getTagName().equals(tagName)) {
                return e;
            }
        }
        return null;
    }

    public static List<Element> getChildrenByTag(Element parent, String tagName) {
        List<Element> result = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element e && e.getTagName().equals(tagName)) {
                result.add(e);
            }
        }
        return result;
    }
}
