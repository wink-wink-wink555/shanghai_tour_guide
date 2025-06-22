package model;

import java.math.BigDecimal;

public class TransportType {
    private int id;                    // 交通工具ID
    private String name;               // 交通工具名称
    private BigDecimal speedKmh;       // 平均速度(公里/小时)
    private BigDecimal costPerKm;      // 每公里费用(元)

    public TransportType() {}
    public TransportType(int id, String name, BigDecimal speedKmh, BigDecimal costPerKm) {
        this.id = id;
        this.name = name;
        this.speedKmh = speedKmh;
        this.costPerKm = costPerKm;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getSpeedKmh() { return speedKmh; }
    public void setSpeedKmh(BigDecimal speedKmh) { this.speedKmh = speedKmh; }
    
    public BigDecimal getCostPerKm() { return costPerKm; }
    public void setCostPerKm(BigDecimal costPerKm) { this.costPerKm = costPerKm; }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TransportType that = (TransportType) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 