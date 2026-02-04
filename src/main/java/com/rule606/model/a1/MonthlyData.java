package com.rule606.model.a1;

public class MonthlyData {
    private String year;
    private int month;
    private OrderRoutingSection sp500;
    private OrderRoutingSection otherStocks;
    private OrderRoutingSection options;

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public OrderRoutingSection getSp500() { return sp500; }
    public void setSp500(OrderRoutingSection sp500) { this.sp500 = sp500; }
    public OrderRoutingSection getOtherStocks() { return otherStocks; }
    public void setOtherStocks(OrderRoutingSection otherStocks) { this.otherStocks = otherStocks; }
    public OrderRoutingSection getOptions() { return options; }
    public void setOptions(OrderRoutingSection options) { this.options = options; }

    public OrderRoutingSection getSectionByIndex(int index) {
        return switch (index) {
            case 0 -> sp500;
            case 1 -> otherStocks;
            case 2 -> options;
            default -> null;
        };
    }
}
