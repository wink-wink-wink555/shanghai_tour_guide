package dao;

import config.DatabaseConfig;
import model.AttractionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttractionTypeDAO {
    public List<AttractionType> getAllAttractionTypes() {
        List<AttractionType> types = new ArrayList<>();
        String sql = "SELECT * FROM attraction_types ORDER BY id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                AttractionType type = new AttractionType();
                type.setId(rs.getInt("id"));
                type.setTypeName(rs.getString("type_name"));
                type.setDescription(rs.getString("description"));
                types.add(type);
            }
            
        } catch (SQLException e) {
            System.err.println("获取所有景点类型失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return types;
    }
} 