package com.rule606.model.b3;

public class B3Report {
    private String brokerDealer;
    private String customer;
    private String timestamp;
    private String startDate;
    private String endDate;
    private DirectedSection directed;
    private DirectedSection nonDirected;

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
    public DirectedSection getDirected() { return directed; }
    public void setDirected(DirectedSection directed) { this.directed = directed; }
    public DirectedSection getNonDirected() { return nonDirected; }
    public void setNonDirected(DirectedSection nonDirected) { this.nonDirected = nonDirected; }
}
