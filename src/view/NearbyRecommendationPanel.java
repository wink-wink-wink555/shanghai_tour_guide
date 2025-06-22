package view;

import service.TourismService;
import model.Attraction;
import util.GeoUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class NearbyRecommendationPanel extends JFrame {
    
    private TourismService tourismService;

    private JTextField latitudeField;
    private JTextField longitudeField;
    private JTextField radiusField;
    private JTable recommendationTable;
    private DefaultTableModel recommendationTableModel;
    
    public NearbyRecommendationPanel(TourismService tourismService) {
        this.tourismService = tourismService;
        initializeComponents();
        
        setTitle("附近景点推荐");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("位置信息"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        latitudeField = new JTextField(10);
        longitudeField = new JTextField(10);
        radiusField = new JTextField(10);
        JButton recommendButton = new JButton("推荐景点");
        JButton useCurrentLocationButton = new JButton("使用人民广场");
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("纬度:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(latitudeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("经度:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(longitudeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("搜索半径(km):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(radiusField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(useCurrentLocationButton, gbc);
        gbc.gridx = 1;
        inputPanel.add(recommendButton, gbc);

        radiusField.setText("5.0");

        String[] columns = {"排序", "景点名称", "区域", "距离", "方位", "简介"};
        recommendationTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recommendationTable = new JTable(recommendationTableModel);
        recommendationTable.setRowHeight(30);
        recommendationTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(recommendationTable);
        scrollPane.setBorder(new TitledBorder("推荐结果"));

        recommendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRecommendation();
            }
        });
        
        useCurrentLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                latitudeField.setText("31.2304");
                longitudeField.setText("121.4737");
            }
        });
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        add(panel);
    }

    private void performRecommendation() {
        try {
            String latText = latitudeField.getText().trim();
            String lonText = longitudeField.getText().trim();
            String radiusText = radiusField.getText().trim();
            
            if (latText.isEmpty() || lonText.isEmpty() || radiusText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写完整的位置信息", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double latitude = Double.parseDouble(latText);
            double longitude = Double.parseDouble(lonText);
            double radius = Double.parseDouble(radiusText);
            
            TourismService.NearbyRecommendationService recommendationService = tourismService.getNearbyRecommendationService();
            List<Attraction> attractions = recommendationService.recommendNearbyAttractions(latitude, longitude, radius, 50);
            
            displayRecommendationResults(attractions, latitude, longitude);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入正确的数字格式", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "推荐失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayRecommendationResults(List<Attraction> attractions, double centerLat, double centerLon) {
        recommendationTableModel.setRowCount(0);
        
        for (int i = 0; i < attractions.size(); i++) {
            Attraction attraction = attractions.get(i);

            String distance = "未知";
            String direction = "未知";
            
            if (attraction.getLatitude() != 0.0 && attraction.getLongitude() != 0.0) {
                double dist = GeoUtil.calculateDistance(
                    centerLat, centerLon,
                    attraction.getLatitude(),
                    attraction.getLongitude()
                );
                distance = String.format("%.2f km", dist);

                double bearing = GeoUtil.calculateBearing(
                    centerLat, centerLon,
                    attraction.getLatitude(), 
                    attraction.getLongitude()
                );
                direction = GeoUtil.getDirectionDescription(bearing);
            }
            
            Object[] row = {
                i + 1,
                attraction.getName(),
                attraction.getDistrict(),
                distance,
                direction,
                attraction.getDescription()
            };
            recommendationTableModel.addRow(row);
        }
        
        if (attractions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "在指定范围内未找到景点", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 