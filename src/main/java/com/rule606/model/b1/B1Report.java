package com.rule606.model.b1;

import java.util.ArrayList;
import java.util.List;

public class B1Report {
    private String brokerDealer;
    private String customer;
    private String timestamp;
    private String startDate;
    private String endDate;
    private List<OrderSection> sections = new ArrayList<>();

    public String getBrokerDealer() { return brokerDealer; }
    public void setBrokerDealer(String brokerDealer) { this.brokerDealer = brokerDealer; }
    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public List<OrderSection> getSections() { return sections; }
}
