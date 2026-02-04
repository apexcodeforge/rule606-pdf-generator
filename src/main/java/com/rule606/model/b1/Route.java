package com.rule606.model.b1;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private String venueName;
    private List<Transaction> transactions = new ArrayList<>();

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    public List<Transaction> getTransactions() { return transactions; }

    public int getRowSpan() {
        return Math.max(1, transactions.size());
    }
}
