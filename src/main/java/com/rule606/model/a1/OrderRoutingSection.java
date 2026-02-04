package com.rule606.model.a1;

import java.util.ArrayList;
import java.util.List;

public class OrderRoutingSection {
    private String ndoPct;
    private String ndoMarketPct;
    private String ndoMarketableLimitPct;
    private String ndoNonmarketableLimitPct;
    private String ndoOtherPct;
    private List<Venue> venues = new ArrayList<>();

    public String getNdoPct() { return ndoPct; }
    public void setNdoPct(String ndoPct) { this.ndoPct = ndoPct; }
    public String getNdoMarketPct() { return ndoMarketPct; }
    public void setNdoMarketPct(String v) { this.ndoMarketPct = v; }
    public String getNdoMarketableLimitPct() { return ndoMarketableLimitPct; }
    public void setNdoMarketableLimitPct(String v) { this.ndoMarketableLimitPct = v; }
    public String getNdoNonmarketableLimitPct() { return ndoNonmarketableLimitPct; }
    public void setNdoNonmarketableLimitPct(String v) { this.ndoNonmarketableLimitPct = v; }
    public String getNdoOtherPct() { return ndoOtherPct; }
    public void setNdoOtherPct(String v) { this.ndoOtherPct = v; }
    public List<Venue> getVenues() { return venues; }

    public String getSummaryValueByTag(String tag) {
        return switch (tag) {
            case "ndoPct" -> ndoPct;
            case "ndoMarketPct" -> ndoMarketPct;
            case "ndoMarketableLimitPct" -> ndoMarketableLimitPct;
            case "ndoNonmarketableLimitPct" -> ndoNonmarketableLimitPct;
            case "ndoOtherPct" -> ndoOtherPct;
            default -> "";
        };
    }

    public boolean isEmpty() {
        return (ndoPct == null || ndoPct.isEmpty())
                && (ndoMarketPct == null || ndoMarketPct.isEmpty())
                && (ndoMarketableLimitPct == null || ndoMarketableLimitPct.isEmpty())
                && (ndoNonmarketableLimitPct == null || ndoNonmarketableLimitPct.isEmpty())
                && (ndoOtherPct == null || ndoOtherPct.isEmpty());
    }
}
