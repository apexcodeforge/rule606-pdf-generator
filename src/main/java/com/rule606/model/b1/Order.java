package com.rule606.model.b1;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private boolean directed;
    private List<Route> routes = new ArrayList<>();

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public boolean isDirected() { return directed; }
    public void setDirected(boolean directed) { this.directed = directed; }
    public List<Route> getRoutes() { return routes; }

    public String getTypeLabel() {
        return directed ? "Directed" : "Non-Directed";
    }

    public int getRowSpan() {
        int span = 0;
        for (Route route : routes) {
            span += route.getRowSpan();
        }
        return Math.max(1, span);
    }
}
