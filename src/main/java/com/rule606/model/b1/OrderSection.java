package com.rule606.model.b1;

import java.util.ArrayList;
import java.util.List;

public class OrderSection {
    private String sectionTag;
    private String title;
    private List<Order> orders = new ArrayList<>();

    public OrderSection(String sectionTag, String title) {
        this.sectionTag = sectionTag;
        this.title = title;
    }

    public String getSectionTag() { return sectionTag; }
    public String getTitle() { return title; }
    public List<Order> getOrders() { return orders; }
}
