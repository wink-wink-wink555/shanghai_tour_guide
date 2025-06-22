package dao;

import config.DatabaseConfig;
import model.Attraction;
import model.AttractionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttractionDAO {
    public List<Attraction> getAllAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        String sql = "SELECT a.*, t.type_name, t.description as type_description " +
                    "FROM attractions a " +
                    "LEFT JOIN attraction_types t ON a.type_id = t.id " +
                    "ORDER BY a.id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Attraction attraction = mapResultSetToAttraction(rs);
                attractions.add(attraction);
            }
            
        } catch (SQLException e) {
            System.err.println("获取所有景点失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attractions;
    }

    public Attraction getAttractionById(int id) {
        String sql = "SELECT a.*, t.type_name, t.description as type_description " +
                    "FROM attractions a " +
                    "LEFT JOIN attraction_types t ON a.type_id = t.id " +
                    "WHERE a.id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAttraction(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("根据ID获取景点失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Attraction> searchAttractionsByName(String name) {
        List<Attraction> attractions = new ArrayList<>();
        String sql = "SELECT a.*, t.type_name, t.description as type_description " +
                    "FROM attractions a " +
                    "LEFT JOIN attraction_types t ON a.type_id = t.id " +
                    "WHERE a.name LIKE ? OR a.english_name LIKE ? " +
                    "ORDER BY a.id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + name + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Attraction attraction = mapResultSetToAttraction(rs);
                attractions.add(attraction);
            }
            
        } catch (SQLException e) {
            System.err.println("根据名称搜索景点失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attractions;
    }

    public List<String> getAllDistricts() {
        List<String> districts = new ArrayList<>();
        String sql = "SELECT DISTINCT district FROM attractions WHERE district IS NOT NULL ORDER BY district";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                districts.add(rs.getString("district"));
            }
            
        } catch (SQLException e) {
            System.err.println("获取所有区域失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return districts;
    }

    private Attraction mapResultSetToAttraction(ResultSet rs) throws SQLException {
        Attraction attraction = new Attraction();
        
        attraction.setId(rs.getInt("id"));
        attraction.setName(rs.getString("name"));
        attraction.setEnglishName(rs.getString("english_name"));
        attraction.setDescription(rs.getString("description"));
        attraction.setDetailedInfo(rs.getString("detailed_info"));
        attraction.setPrice(rs.getBigDecimal("price"));
        attraction.setOpeningHours(rs.getString("opening_hours"));
        attraction.setMaxCapacity(rs.getInt("max_capacity"));
        attraction.setLatitude(rs.getDouble("latitude"));
        attraction.setLongitude(rs.getDouble("longitude"));
        attraction.setTypeId(rs.getInt("type_id"));
        attraction.setDistrict(rs.getString("district"));
        attraction.setAddress(rs.getString("address"));
        attraction.setPhone(rs.getString("phone"));
        attraction.setWebsite(rs.getString("website"));
        attraction.setRecommendDuration(rs.getInt("recommend_duration"));
        attraction.setRating(rs.getBigDecimal("rating"));

        String typeName = rs.getString("type_name");
        if (typeName != null) {
            AttractionType type = new AttractionType();
            type.setId(rs.getInt("type_id"));
            type.setTypeName(typeName);
            type.setDescription(rs.getString("type_description"));
            attraction.setAttractionType(type);
        }
        
        return attraction;
    }
} 