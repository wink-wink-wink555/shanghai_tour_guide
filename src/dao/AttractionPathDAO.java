package dao;

import config.DatabaseConfig;
import model.AttractionPath;
import model.TransportType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttractionPathDAO {
    public List<AttractionPath> getAllPaths() {
        List<AttractionPath> paths = new ArrayList<>();
        String sql = "SELECT p.*, t.name as transport_name, t.speed_kmh, t.cost_per_km " +
                    "FROM attraction_paths p " +
                    "LEFT JOIN transport_types t ON p.transport_type_id = t.id " +
                    "ORDER BY p.from_attraction_id, p.to_attraction_id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                AttractionPath path = mapResultSetToPath(rs);
                paths.add(path);
            }
            
        } catch (SQLException e) {
            System.err.println("获取所有路径失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return paths;
    }
    private AttractionPath mapResultSetToPath(ResultSet rs) throws SQLException {
        AttractionPath path = new AttractionPath();
        
        path.setId(rs.getInt("id"));
        path.setFromAttractionId(rs.getInt("from_attraction_id"));
        path.setToAttractionId(rs.getInt("to_attraction_id"));
        path.setDistanceKm(rs.getBigDecimal("distance_km"));
        path.setTravelTimeMinutes(rs.getInt("travel_time_minutes"));
        path.setCost(rs.getBigDecimal("cost"));
        path.setDescription(rs.getString("description"));
        
        // 设置交通工具信息
        String transportName = rs.getString("transport_name");
        if (transportName != null) {
            TransportType transportType = new TransportType();
            transportType.setId(rs.getInt("transport_type_id"));
            transportType.setName(transportName);
            transportType.setSpeedKmh(rs.getBigDecimal("speed_kmh"));
            transportType.setCostPerKm(rs.getBigDecimal("cost_per_km"));
            path.setTransportType(transportType);
        }
        
        return path;
    }
} 