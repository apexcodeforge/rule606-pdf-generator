package com.rule606.xml;

import com.rule606.model.ReportType;
import com.rule606.model.a1.*;
import com.rule606.model.b1.*;
import com.rule606.model.b1.Order;
import com.rule606.model.b1.Route;
import com.rule606.model.b1.Transaction;
import com.rule606.model.b3.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class XmlParser {

    private Document doc;

    public ReportType parse(File xmlFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        String rootTag = doc.getDocumentElement().getTagName();
        ReportType type = ReportType.fromRootElement(rootTag);
        if (type == null) {
            throw new IllegalArgumentException("Unknown report type: " + rootTag);
        }
        return type;
    }

    public boolean hasTimestamp() {
        return doc.getElementsByTagName("timestamp").getLength() > 0;
    }

    public A1Report parseA1() {
        A1Report report = new A1Report();
        Element root = doc.getDocumentElement();
        report.setBrokerDealer(XmlUtils.getElementValue(root, "bd"));
        report.setYear(XmlUtils.getElementValue(root, "year"));
        report.setQuarter(XmlUtils.getElementValue(root, "qtr"));
        if (hasTimestamp()) {
            report.setTimestamp(XmlUtils.getElementValue(root, "timestamp"));
        }

        NodeList monthlyNodes = root.getElementsByTagName("rMonthly");
        for (int i = 0; i < monthlyNodes.getLength(); i++) {
            Element monthlyElt = (Element) monthlyNodes.item(i);
            MonthlyData monthly = new MonthlyData();
            monthly.setYear(XmlUtils.getElementValue(monthlyElt, "year"));
            String monStr = XmlUtils.getElementValue(monthlyElt, "mon");
            monthly.setMonth(Integer.parseInt(monStr));

            NodeList sp500Nodes = monthlyElt.getElementsByTagName("rSP500");
            if (sp500Nodes.getLength() > 0) {
                monthly.setSp500(parseOrderRoutingSection((Element) sp500Nodes.item(0)));
            }
            NodeList otherNodes = monthlyElt.getElementsByTagName("rOtherStocks");
            if (otherNodes.getLength() > 0) {
                monthly.setOtherStocks(parseOrderRoutingSection((Element) otherNodes.item(0)));
            }
            NodeList optionsNodes = monthlyElt.getElementsByTagName("rOptions");
            if (optionsNodes.getLength() > 0) {
                monthly.setOptions(parseOrderRoutingSection((Element) optionsNodes.item(0)));
            }

            report.getMonthlyData().add(monthly);
        }
        return report;
    }

    private OrderRoutingSection parseOrderRoutingSection(Element sectionElt) {
        OrderRoutingSection section = new OrderRoutingSection();
        section.setNdoPct(getChildText(sectionElt, "ndoPct"));
        section.setNdoMarketPct(getChildText(sectionElt, "ndoMarketPct"));
        section.setNdoMarketableLimitPct(getChildText(sectionElt, "ndoMarketableLimitPct"));
        section.setNdoNonmarketableLimitPct(getChildText(sectionElt, "ndoNonmarketableLimitPct"));
        section.setNdoOtherPct(getChildText(sectionElt, "ndoOtherPct"));

        NodeList venuesNodes = sectionElt.getElementsByTagName("rVenues");
        if (venuesNodes.getLength() > 0) {
            Element venuesElt = (Element) venuesNodes.item(0);
            NodeList venueNodes = venuesElt.getElementsByTagName("rVenue");
            for (int i = 0; i < venueNodes.getLength(); i++) {
                Element venueElt = (Element) venueNodes.item(i);
                Venue venue = new Venue();
                venue.setName(XmlUtils.getVenueName(venueElt));
                venue.setOrderPct(getChildText(venueElt, "orderPct"));
                venue.setMarketPct(getChildText(venueElt, "marketPct"));
                venue.setMarketableLimitPct(getChildText(venueElt, "marketableLimitPct"));
                venue.setNonMarketableLimitPct(getChildText(venueElt, "nonMarketableLimitPct"));
                venue.setOtherPct(getChildText(venueElt, "otherPct"));
                venue.setNetPmtPaidRecvMarketOrdersUsd(getChildText(venueElt, "netPmtPaidRecvMarketOrdersUsd"));
                venue.setNetPmtPaidRecvMarketOrdersCph(getChildText(venueElt, "netPmtPaidRecvMarketOrdersCph"));
                venue.setNetPmtPaidRecvMarketableLimitOrdersUsd(getChildText(venueElt, "netPmtPaidRecvMarketableLimitOrdersUsd"));
                venue.setNetPmtPaidRecvMarketableLimitOrdersCph(getChildText(venueElt, "netPmtPaidRecvMarketableLimitOrdersCph"));
                venue.setNetPmtPaidRecvNonMarketableLimitOrdersUsd(getChildText(venueElt, "netPmtPaidRecvNonMarketableLimitOrdersUsd"));
                venue.setNetPmtPaidRecvNonMarketableLimitOrdersCph(getChildText(venueElt, "netPmtPaidRecvNonMarketableLimitOrdersCph"));
                venue.setNetPmtPaidRecvOtherOrdersUsd(getChildText(venueElt, "netPmtPaidRecvOtherOrdersUsd"));
                venue.setNetPmtPaidRecvOtherOrdersCph(getChildText(venueElt, "netPmtPaidRecvOtherOrdersCph"));
                venue.setMaterialAspects(getChildText(venueElt, "materialAspects"));
                section.getVenues().add(venue);
            }
        }
        return section;
    }

    public B1Report parseB1() {
        B1Report report = new B1Report();
        Element root = doc.getDocumentElement();
        report.setBrokerDealer(XmlUtils.getElementValue(root, "bd"));
        report.setCustomer(XmlUtils.getElementValue(root, "customer"));
        if (hasTimestamp()) {
            report.setTimestamp(XmlUtils.getElementValue(root, "timestamp"));
        }
        report.setStartDate(XmlUtils.getElementValue(root, "startDate"));
        report.setEndDate(XmlUtils.getElementValue(root, "endDate"));

        String[][] sectionDefs = {
                {"held", "Held NMS Stocks"},
                {"notHeldExempt", "Exempt Not-Held NMS stocks"},
                {"options", "Options Customer Routing Report"}
        };

        for (String[] def : sectionDefs) {
            NodeList sectionNodes = root.getElementsByTagName(def[0]);
            if (sectionNodes.getLength() > 0) {
                Element sectionElt = (Element) sectionNodes.item(0);
                OrderSection section = new OrderSection(def[0], def[1]);
                NodeList orderNodes = sectionElt.getElementsByTagName("order");
                for (int i = 0; i < orderNodes.getLength(); i++) {
                    Element orderElt = (Element) orderNodes.item(i);
                    Order order = new Order();
                    order.setOrderId(getChildText(orderElt, "orderId"));
                    String directed = getChildText(orderElt, "directed");
                    order.setDirected("Y".equals(directed));

                    List<Element> routeElts = XmlUtils.getChildrenByTag(orderElt, "route");
                    for (Element routeElt : routeElts) {
                        Route route = new Route();
                        route.setVenueName(buildB1VenueName(routeElt));

                        List<Element> txElts = XmlUtils.getChildrenByTag(routeElt, "transaction");
                        for (Element txElt : txElts) {
                            Transaction tx = new Transaction();
                            tx.setDate(getChildText(txElt, "date"));
                            tx.setTime(getChildText(txElt, "time"));
                            route.getTransactions().add(tx);
                        }
                        order.getRoutes().add(route);
                    }
                    section.getOrders().add(order);
                }
                report.getSections().add(section);
            }
        }
        return report;
    }

    private String buildB1VenueName(Element routeElt) {
        String venueName = "";
        NodeList children = routeElt.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!(child instanceof Element e)) continue;
            String tag = e.getTagName();
            String val = XmlUtils.getNodeValue(e, true);
            switch (tag) {
                case "venueName" -> venueName = val;
                case "mic", "mpid" -> {
                    if (!val.isEmpty()) {
                        if (!venueName.isEmpty()) {
                            venueName = venueName + " (" + val + ")";
                        } else {
                            venueName = val;
                        }
                    }
                }
            }
        }
        return venueName;
    }

    public B3Report parseB3() {
        B3Report report = new B3Report();
        Element root = doc.getDocumentElement();
        report.setBrokerDealer(XmlUtils.getElementValue(root, "bd"));
        report.setCustomer(XmlUtils.getElementValue(root, "customer"));
        if (hasTimestamp()) {
            report.setTimestamp(XmlUtils.getElementValue(root, "timestamp"));
        }
        report.setStartDate(XmlUtils.getElementValue(root, "startDate"));
        report.setEndDate(XmlUtils.getElementValue(root, "endDate"));

        NodeList hDirectedNodes = root.getElementsByTagName("hDirected");
        if (hDirectedNodes.getLength() > 0) {
            report.setDirected(parseDirectedSection((Element) hDirectedNodes.item(0), true));
        }
        NodeList hNondirectedNodes = root.getElementsByTagName("hNondirected");
        if (hNondirectedNodes.getLength() > 0) {
            report.setNonDirected(parseDirectedSection((Element) hNondirectedNodes.item(0), false));
        }
        return report;
    }

    private DirectedSection parseDirectedSection(Element sectionElt, boolean isDirected) {
        DirectedSection section = new DirectedSection(isDirected);
        NodeList monthlyNodes = sectionElt.getElementsByTagName("hMonthly");
        for (int i = 0; i < monthlyNodes.getLength(); i++) {
            Element monthlyElt = (Element) monthlyNodes.item(i);
            HandlingMonthly monthly = new HandlingMonthly();
            monthly.setYear(getChildText(monthlyElt, "year"));
            monthly.setMonth(Integer.parseInt(getChildText(monthlyElt, "mon")));
            monthly.setSentShr(getChildText(monthlyElt, "sentShr"));
            monthly.setExecutedAsPrincipalShr(getChildText(monthlyElt, "executedAsPrincipalShr"));
            monthly.setIoiExposedOrd(getChildText(monthlyElt, "ioiExposedOrd"));

            // IOI exposed venues
            NodeList ioiListNodes = monthlyElt.getElementsByTagName("ioiExposedVenueList");
            for (int j = 0; j < ioiListNodes.getLength(); j++) {
                Element ioiListElt = (Element) ioiListNodes.item(j);
                NodeList ioiVenueNodes = ioiListElt.getElementsByTagName("ioiExposedVenue");
                for (int k = 0; k < ioiVenueNodes.getLength(); k++) {
                    Element ioiVenueElt = (Element) ioiVenueNodes.item(k);
                    List<Element> children = XmlUtils.getChildElements(ioiVenueElt);
                    if (!children.isEmpty()) {
                        String venueName = children.get(0).getTextContent();
                        venueName = venueName.replaceAll("\\s+", " ").trim();
                        monthly.getIoiExposedVenues().add(venueName);
                    }
                }
            }

            // Routing venues
            String[] detailTags = {"routedShr", "iocRoutedShr", "furtherRoutableShr",
                    "orderSizeShr", "executedShr", "filledPct", "fillSizeShr",
                    "netFeeOrRebateCph", "midpointShr", "midpointPct", "nearsideShr",
                    "nearsidePct", "farsideShr", "farsidePct", "providedLiqudityShr",
                    "providedLiquidityPct", "orderDurationMsec", "providedLiquidityNetCph",
                    "removedLiquidityShr", "removedLiquidityPct", "removedLiquidityNetCph"};

            NodeList routingListNodes = monthlyElt.getElementsByTagName("routingVenueList");
            for (int j = 0; j < routingListNodes.getLength(); j++) {
                Element routingListElt = (Element) routingListNodes.item(j);
                NodeList iVenueNodes = routingListElt.getElementsByTagName("iVenue");
                for (int k = 0; k < iVenueNodes.getLength(); k++) {
                    Element iVenueElt = (Element) iVenueNodes.item(k);
                    RoutingVenue rv = new RoutingVenue();
                    rv.setName(XmlUtils.getVenueName(iVenueElt));
                    List<Element> venueChildren = XmlUtils.getChildElements(iVenueElt);
                    for (String tag : detailTags) {
                        for (Element child : venueChildren) {
                            if (child.getTagName().equals(tag)) {
                                rv.setValueByTag(tag, XmlUtils.getNodeValue(child));
                                break;
                            }
                        }
                    }
                    monthly.getRoutingVenues().add(rv);
                }
            }

            section.getMonthlyData().add(monthly);
        }
        return section;
    }

    private String getChildText(Element parent, String tagName) {
        Element child = XmlUtils.getFirstChildByTag(parent, tagName);
        if (child == null) {
            // Fall back to getElementsByTagName for nested elements
            NodeList nodes = parent.getElementsByTagName(tagName);
            if (nodes.getLength() == 0) return "";
            child = (Element) nodes.item(0);
        }
        if (child.getFirstChild() == null) return "";
        String val = child.getFirstChild().getNodeValue();
        return val != null ? val : "";
    }
}
