package com.rule606.model;

public enum ReportType {
    A1("heldOrderRoutingPublicReport"),
    B1("heldExemptNotHeldOrderRoutingCustomerReport"),
    B3("notHeldOrderHandlingCustomerReport");

    private final String rootElement;

    ReportType(String rootElement) {
        this.rootElement = rootElement;
    }

    public String getRootElement() {
        return rootElement;
    }

    public static ReportType fromRootElement(String rootElement) {
        for (ReportType type : values()) {
            if (type.rootElement.equals(rootElement)) {
                return type;
            }
        }
        return null;
    }
}
