package com.test;

import java.util.List;

public class ArticleDuplicationResponse {
    private Boolean isDup;
    private List<ArticleDuplicationResponseId> dupIds;

    public ArticleDuplicationResponse(Boolean isDup, List<ArticleDuplicationResponseId> dupIds) {
        this.isDup = isDup;
        this.dupIds = dupIds;
    }

    public Boolean getIsDup() {
        return isDup;
    }

    public void setIsDup(Boolean dup) {
        this.isDup = dup;
    }

    public List<ArticleDuplicationResponseId> getDupIds() {
        return dupIds;
    }

    public void setDupIds(List<ArticleDuplicationResponseId> dupIds) {
        this.dupIds = dupIds;
    }

    public ArticleDuplicationResponse() {
    }
}