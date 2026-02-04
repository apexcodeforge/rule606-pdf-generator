package com.rule606.model.b3;

import java.util.ArrayList;
import java.util.List;

public class HandlingMonthly {
    private String year;
    private int month;
    private String sentShr;
    private String executedAsPrincipalShr;
    private String ioiExposedOrd;
    private List<String> ioiExposedVenues = new ArrayList<>();
    private List<RoutingVenue> routingVenues = new ArrayList<>();

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public String getSentShr() { return sentShr; }
    public void setSentShr(String sentShr) { this.sentShr = sentShr; }
    public String getExecutedAsPrincipalShr() { return executedAsPrincipalShr; }
    public void setExecutedAsPrincipalShr(String v) { this.executedAsPrincipalShr = v; }
    public String getIoiExposedOrd() { return ioiExposedOrd; }
    public void setIoiExposedOrd(String v) { this.ioiExposedOrd = v; }
    public List<String> getIoiExposedVenues() { return ioiExposedVenues; }
    public List<RoutingVenue> getRoutingVenues() { return routingVenues; }

    public String getSummaryValueByTag(String tag) {
        return switch (tag) {
            case "sentShr" -> sentShr;
            case "executedAsPrincipalShr" -> executedAsPrincipalShr;
            case "ioiExposedOrd" -> ioiExposedOrd;
            default -> "";
        };
    }
}
