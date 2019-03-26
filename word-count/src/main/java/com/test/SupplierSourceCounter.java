package com.test;

import java.time.LocalDateTime;

public class SupplierSourceCounter {
    private String supplier;
    private String source;
    private LocalDateTime enterTime;
    private LocalDateTime publishTime;
    private boolean isDup;

    public SupplierSourceCounter() {
    }

    public SupplierSourceCounter(String supplier, String source, LocalDateTime enterTime, LocalDateTime publishTime, boolean isDup) {
        this.supplier = supplier;
        this.source = source;
        this.enterTime = enterTime;
        this.publishTime = publishTime;
        this.isDup = isDup;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(LocalDateTime enterTime) {
        this.enterTime = enterTime;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public boolean isDup() {
        return isDup;
    }

    public void setDup(boolean dup) {
        isDup = dup;
    }

    @Override
    public String toString() {
        return "SupplierSourceCounter{" +
                "supplier='" + supplier + '\'' +
                ", source='" + source + '\'' +
                ", enterTime='" + enterTime + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", isDup=" + isDup +
                '}';
    }
}
