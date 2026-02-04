package com.rule606.model.a1;

public class Venue {
    private String name;
    private String orderPct;
    private String marketPct;
    private String marketableLimitPct;
    private String nonMarketableLimitPct;
    private String otherPct;
    private String netPmtPaidRecvMarketOrdersUsd;
    private String netPmtPaidRecvMarketOrdersCph;
    private String netPmtPaidRecvMarketableLimitOrdersUsd;
    private String netPmtPaidRecvMarketableLimitOrdersCph;
    private String netPmtPaidRecvNonMarketableLimitOrdersUsd;
    private String netPmtPaidRecvNonMarketableLimitOrdersCph;
    private String netPmtPaidRecvOtherOrdersUsd;
    private String netPmtPaidRecvOtherOrdersCph;
    private String materialAspects;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOrderPct() { return orderPct; }
    public void setOrderPct(String orderPct) { this.orderPct = orderPct; }
    public String getMarketPct() { return marketPct; }
    public void setMarketPct(String marketPct) { this.marketPct = marketPct; }
    public String getMarketableLimitPct() { return marketableLimitPct; }
    public void setMarketableLimitPct(String marketableLimitPct) { this.marketableLimitPct = marketableLimitPct; }
    public String getNonMarketableLimitPct() { return nonMarketableLimitPct; }
    public void setNonMarketableLimitPct(String nonMarketableLimitPct) { this.nonMarketableLimitPct = nonMarketableLimitPct; }
    public String getOtherPct() { return otherPct; }
    public void setOtherPct(String otherPct) { this.otherPct = otherPct; }
    public String getNetPmtPaidRecvMarketOrdersUsd() { return netPmtPaidRecvMarketOrdersUsd; }
    public void setNetPmtPaidRecvMarketOrdersUsd(String v) { this.netPmtPaidRecvMarketOrdersUsd = v; }
    public String getNetPmtPaidRecvMarketOrdersCph() { return netPmtPaidRecvMarketOrdersCph; }
    public void setNetPmtPaidRecvMarketOrdersCph(String v) { this.netPmtPaidRecvMarketOrdersCph = v; }
    public String getNetPmtPaidRecvMarketableLimitOrdersUsd() { return netPmtPaidRecvMarketableLimitOrdersUsd; }
    public void setNetPmtPaidRecvMarketableLimitOrdersUsd(String v) { this.netPmtPaidRecvMarketableLimitOrdersUsd = v; }
    public String getNetPmtPaidRecvMarketableLimitOrdersCph() { return netPmtPaidRecvMarketableLimitOrdersCph; }
    public void setNetPmtPaidRecvMarketableLimitOrdersCph(String v) { this.netPmtPaidRecvMarketableLimitOrdersCph = v; }
    public String getNetPmtPaidRecvNonMarketableLimitOrdersUsd() { return netPmtPaidRecvNonMarketableLimitOrdersUsd; }
    public void setNetPmtPaidRecvNonMarketableLimitOrdersUsd(String v) { this.netPmtPaidRecvNonMarketableLimitOrdersUsd = v; }
    public String getNetPmtPaidRecvNonMarketableLimitOrdersCph() { return netPmtPaidRecvNonMarketableLimitOrdersCph; }
    public void setNetPmtPaidRecvNonMarketableLimitOrdersCph(String v) { this.netPmtPaidRecvNonMarketableLimitOrdersCph = v; }
    public String getNetPmtPaidRecvOtherOrdersUsd() { return netPmtPaidRecvOtherOrdersUsd; }
    public void setNetPmtPaidRecvOtherOrdersUsd(String v) { this.netPmtPaidRecvOtherOrdersUsd = v; }
    public String getNetPmtPaidRecvOtherOrdersCph() { return netPmtPaidRecvOtherOrdersCph; }
    public void setNetPmtPaidRecvOtherOrdersCph(String v) { this.netPmtPaidRecvOtherOrdersCph = v; }
    public String getMaterialAspects() { return materialAspects; }
    public void setMaterialAspects(String materialAspects) { this.materialAspects = materialAspects; }

    public String getValueByTag(String tag) {
        return switch (tag) {
            case "orderPct" -> orderPct;
            case "marketPct" -> marketPct;
            case "marketableLimitPct" -> marketableLimitPct;
            case "nonMarketableLimitPct" -> nonMarketableLimitPct;
            case "otherPct" -> otherPct;
            case "netPmtPaidRecvMarketOrdersUsd" -> netPmtPaidRecvMarketOrdersUsd;
            case "netPmtPaidRecvMarketOrdersCph" -> netPmtPaidRecvMarketOrdersCph;
            case "netPmtPaidRecvMarketableLimitOrdersUsd" -> netPmtPaidRecvMarketableLimitOrdersUsd;
            case "netPmtPaidRecvMarketableLimitOrdersCph" -> netPmtPaidRecvMarketableLimitOrdersCph;
            case "netPmtPaidRecvNonMarketableLimitOrdersUsd" -> netPmtPaidRecvNonMarketableLimitOrdersUsd;
            case "netPmtPaidRecvNonMarketableLimitOrdersCph" -> netPmtPaidRecvNonMarketableLimitOrdersCph;
            case "netPmtPaidRecvOtherOrdersUsd" -> netPmtPaidRecvOtherOrdersUsd;
            case "netPmtPaidRecvOtherOrdersCph" -> netPmtPaidRecvOtherOrdersCph;
            default -> "";
        };
    }
}
