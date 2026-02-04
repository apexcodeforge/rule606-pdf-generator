package com.rule606.model.b3;

import java.util.ArrayList;
import java.util.List;

public class DirectedSection {
    private boolean directed;
    private List<HandlingMonthly> monthlyData = new ArrayList<>();

    public DirectedSection(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() { return directed; }
    public List<HandlingMonthly> getMonthlyData() { return monthlyData; }
}
