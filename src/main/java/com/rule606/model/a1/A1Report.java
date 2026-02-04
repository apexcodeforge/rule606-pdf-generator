package com.rule606.model.a1;

import java.util.ArrayList;
import java.util.List;

public class A1Report {
    private String brokerDealer;
    private String year;
    private String quarter;
    private String timestamp;
    private List<MonthlyData> monthlyData = new ArrayList<>();

    public String getBrokerDealer() { return brokerDealer; }
    public void setBrokerDealer(String brokerDealer) { this.brokerDealer = brokerDealer; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getQuarter() { return quarter; }
    public void setQuarter(String quarter) { this.quarter = quarter; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public List<MonthlyData> getMonthlyData() { return monthlyData; }
}
