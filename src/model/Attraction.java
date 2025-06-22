package model;

import java.math.BigDecimal;

public class Attraction {
    private int id;                    // 景点ID
    private String name;               // 景点名称
    private String englishName;        // 英文名称
    private String description;        // 景点简介
    private String detailedInfo;       // 详细介绍
    private BigDecimal price;          // 门票价格
    private String openingHours;       // 开放时间
    private int maxCapacity;           // 最大人流量
    private double latitude;           // 纬度
    private double longitude;          // 经度
    private int typeId;                // 景点类型ID
    private String district;           // 所在区域
    private String address;            // 详细地址
    private String phone;              // 联系电话
    private String website;            // 官方网站
    private int recommendDuration;     // 推荐游览时长(分钟)
    private BigDecimal rating;         // 评分

    private AttractionType attractionType;  // 景点类型对象

    public Attraction() {}
    public Attraction(int id, String name, String description, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEnglishName() { return englishName; }
    public void setEnglishName(String englishName) { this.englishName = englishName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDetailedInfo() { return detailedInfo; }
    public void setDetailedInfo(String detailedInfo) { this.detailedInfo = detailedInfo; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public int getRecommendDuration() { return recommendDuration; }
    public void setRecommendDuration(int recommendDuration) { this.recommendDuration = recommendDuration; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public AttractionType getAttractionType() { return attractionType; }
    public void setAttractionType(AttractionType attractionType) { this.attractionType = attractionType; }
    
    @Override
    public String toString() {
        return name + " (" + district + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Attraction that = (Attraction) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 