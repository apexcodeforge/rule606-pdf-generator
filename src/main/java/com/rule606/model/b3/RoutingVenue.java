package com.rule606.model.b3;

public class RoutingVenue {
    private String name;
    private String routedShr;
    private String iocRoutedShr;
    private String furtherRoutableShr;
    private String orderSizeShr;
    private String executedShr;
    private String filledPct;
    private String fillSizeShr;
    private String netFeeOrRebateCph;
    private String midpointShr;
    private String midpointPct;
    private String nearsideShr;
    private String nearsidePct;
    private String farsideShr;
    private String farsidePct;
    private String providedLiqudityShr;
    private String providedLiquidityPct;
    private String orderDurationMsec;
    private String providedLiquidityNetCph;
    private String removedLiquidityShr;
    private String removedLiquidityPct;
    private String removedLiquidityNetCph;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValueByTag(String tag) {
        return switch (tag) {
            case "routedShr" -> routedShr;
            case "iocRoutedShr" -> iocRoutedShr;
            case "furtherRoutableShr" -> furtherRoutableShr;
            case "orderSizeShr" -> orderSizeShr;
            case "executedShr" -> executedShr;
            case "filledPct" -> filledPct;
            case "fillSizeShr" -> fillSizeShr;
            case "netFeeOrRebateCph" -> netFeeOrRebateCph;
            case "midpointShr" -> midpointShr;
            case "midpointPct" -> midpointPct;
            case "nearsideShr" -> nearsideShr;
            case "nearsidePct" -> nearsidePct;
            case "farsideShr" -> farsideShr;
            case "farsidePct" -> farsidePct;
            case "providedLiqudityShr" -> providedLiqudityShr;
            case "providedLiquidityPct" -> providedLiquidityPct;
            case "orderDurationMsec" -> orderDurationMsec;
            case "providedLiquidityNetCph" -> providedLiquidityNetCph;
            case "removedLiquidityShr" -> removedLiquidityShr;
            case "removedLiquidityPct" -> removedLiquidityPct;
            case "removedLiquidityNetCph" -> removedLiquidityNetCph;
            default -> "";
        };
    }

    public void setValueByTag(String tag, String value) {
        switch (tag) {
            case "routedShr" -> routedShr = value;
            case "iocRoutedShr" -> iocRoutedShr = value;
            case "furtherRoutableShr" -> furtherRoutableShr = value;
            case "orderSizeShr" -> orderSizeShr = value;
            case "executedShr" -> executedShr = value;
            case "filledPct" -> filledPct = value;
            case "fillSizeShr" -> fillSizeShr = value;
            case "netFeeOrRebateCph" -> netFeeOrRebateCph = value;
            case "midpointShr" -> midpointShr = value;
            case "midpointPct" -> midpointPct = value;
            case "nearsideShr" -> nearsideShr = value;
            case "nearsidePct" -> nearsidePct = value;
            case "farsideShr" -> farsideShr = value;
            case "farsidePct" -> farsidePct = value;
            case "providedLiqudityShr" -> providedLiqudityShr = value;
            case "providedLiquidityPct" -> providedLiquidityPct = value;
            case "orderDurationMsec" -> orderDurationMsec = value;
            case "providedLiquidityNetCph" -> providedLiquidityNetCph = value;
            case "removedLiquidityShr" -> removedLiquidityShr = value;
            case "removedLiquidityPct" -> removedLiquidityPct = value;
            case "removedLiquidityNetCph" -> removedLiquidityNetCph = value;
        }
    }
}
