package model;

import java.math.BigDecimal;

public class AttractionPath {
    private int id;                    // 路径ID
    private int fromAttractionId;      // 起点景点ID
    private int toAttractionId;        // 终点景点ID
    private BigDecimal distanceKm;     // 距离(公里)
    private int travelTimeMinutes;     // 预估时间(分钟)
    private BigDecimal cost;           // 费用(元)
    private String description;        // 路径描述

    private Attraction fromAttraction; // 起点景点
    private Attraction toAttraction;   // 终点景点
    private TransportType transportType; // 交通工具

    public AttractionPath() {}
    public AttractionPath(int fromAttractionId, int toAttractionId, 
                         BigDecimal distanceKm, int transportTypeId,
                         int travelTimeMinutes, BigDecimal cost) {
        this.fromAttractionId = fromAttractionId;
        this.toAttractionId = toAttractionId;
        this.distanceKm = distanceKm;
        this.travelTimeMinutes = travelTimeMinutes;
        this.cost = cost;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getFromAttractionId() { return fromAttractionId; }
    public void setFromAttractionId(int fromAttractionId) { this.fromAttractionId = fromAttractionId; }
    
    public int getToAttractionId() { return toAttractionId; }
    public void setToAttractionId(int toAttractionId) { this.toAttractionId = toAttractionId; }
    
    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

    public int getTravelTimeMinutes() { return travelTimeMinutes; }
    public void setTravelTimeMinutes(int travelTimeMinutes) { this.travelTimeMinutes = travelTimeMinutes; }
    
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TransportType getTransportType() { return transportType; }
    public void setTransportType(TransportType transportType) { this.transportType = transportType; }

    public double getWeight(String optimizeType) {
        switch (optimizeType.toLowerCase()) {
            case "distance":
                return distanceKm.doubleValue();
            case "time":
                return travelTimeMinutes;
            case "cost":
                return cost.doubleValue();
            default:
                return distanceKm.doubleValue(); // 默认使用距离
        }
    }
    
    @Override
    public String toString() {
        return String.format("从%s到%s，距离%.2f公里，耗时%d分钟，费用%.2f元",
                fromAttraction != null ? fromAttraction.getName() : "景点" + fromAttractionId,
                toAttraction != null ? toAttraction.getName() : "景点" + toAttractionId,
                distanceKm.doubleValue(), travelTimeMinutes, cost.doubleValue());
    }
} 