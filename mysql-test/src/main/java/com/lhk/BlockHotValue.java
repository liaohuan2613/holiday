package com.lhk;

public class BlockHotValue {
    private String code;
    private String name;
    private String category;
    private double hotValue;
    private double hotWeight;
    private double lastHotWeight;
    private double hotTarget;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getHotValue() {
        return hotValue;
    }

    public void setHotValue(double hotValue) {
        this.hotValue = hotValue;
    }

    public double getHotWeight() {
        return hotWeight;
    }

    public void setHotWeight(double hotWeight) {
        this.hotWeight = hotWeight;
    }

    public double getLastHotWeight() {
        return lastHotWeight;
    }

    public void setLastHotWeight(double lastHotWeight) {
        this.lastHotWeight = lastHotWeight;
    }

    public double getHotTarget() {
        return hotTarget;
    }

    public void setHotTarget(double hotTarget) {
        this.hotTarget = hotTarget;
    }

    @Override
    public String toString() {
        return "BlockHotValue{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", hotValue=" + hotValue +
                ", hotWeight=" + hotWeight +
                ", lastHotWeight=" + lastHotWeight +
                ", hotTarget=" + hotTarget +
                '}';
    }
}
