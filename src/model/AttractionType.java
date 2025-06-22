package model;

public class AttractionType {
    private int id;                // 类型ID
    private String typeName;       // 类型名称
    private String description;    // 类型描述

    public AttractionType() {}
    public AttractionType(int id, String typeName, String description) {
        this.id = id;
        this.typeName = typeName;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return typeName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AttractionType that = (AttractionType) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 