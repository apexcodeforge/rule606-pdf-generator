package com.rule606.model.b1;

public class Transaction {
    private String date;
    private String time;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getFormattedDateTime() {
        String result = "";
        if (date != null) result += date;
        if (time != null) result += " " + time;
        return result.trim();
    }
}
