package com.arieschwartzman.cortana.model;

import com.arieschwartzman.cortana.model.SummaryInfo;

import java.util.List;

public class SummaryResponse {

    private List<SummaryInfo> summaries = null;
    private int itemCount;

    public List<SummaryInfo> getSummaries() {
        return summaries;
    }
    public int getItemCount() { return itemCount;}
}
