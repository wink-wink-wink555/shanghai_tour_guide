package view;

import service.TourismService;
import model.Attraction;
import algorithm.DijkstraAlgorithm;
import util.GeoUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TourPlanningPanel extends JFrame {
    
    private TourismService tourismService;

    private JComboBox<Attraction> startTourCombo;
    private JTextField timeHoursField;
    private JTextField maxDistanceField;
    private JComboBox<String> tourOptimizeTypeCombo;

    private JTable tourAttractionTable;
    private DefaultTableModel tourAttractionTableModel;
    private JTable tourStatsTable;
    private DefaultTableModel tourStatsTableModel;
    private JTable tourTypeTable;
    private DefaultTableModel tourTypeTableModel;
    private JTextArea tourTipsArea;
    
    public TourPlanningPanel(TourismService tourismService) {
        this.tourismService = tourismService;
        initializeComponents();
        loadInitialData();
        
        setTitle("游览规划");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("游览规划设置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        startTourCombo = new JComboBox<>();
        timeHoursField = new JTextField(10);
        maxDistanceField = new JTextField(10);
        tourOptimizeTypeCombo = new JComboBox<>(new String[]{"距离最短", "时间最短", "费用最低"});
        JButton planTourButton = new JButton("制定游览计划");
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("起始景点:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(startTourCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("可用时间(小时):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(timeHoursField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("最大距离(km):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(maxDistanceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("优化方式:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(tourOptimizeTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        inputPanel.add(planTourButton, gbc);

        timeHoursField.setText("8");
        maxDistanceField.setText("20");

        JPanel resultPanel = createTourResultPanel();

        planTourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performTourPlanning();
            }
        });
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);
        
        add(panel);
    }
    
    private JPanel createTourResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(600);

        JPanel leftPanel = new JPanel(new BorderLayout());

        JPanel routePanel = new JPanel(new BorderLayout());
        routePanel.setBorder(new TitledBorder("游览路线"));
        
        String[] tourColumns = {"时间", "景点名称", "区域", "游览时长(分钟)", "票价(元)", "交通方式", "路程时间(分钟)"};
        tourAttractionTableModel = new DefaultTableModel(tourColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tourAttractionTable = new JTable(tourAttractionTableModel);
        tourAttractionTable.setRowHeight(30);
        tourAttractionTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JScrollPane tourScrollPane = new JScrollPane(tourAttractionTable);
        routePanel.add(tourScrollPane, BorderLayout.CENTER);

        JPanel tipsPanel = new JPanel(new BorderLayout());
        tipsPanel.setBorder(new TitledBorder("温馨提示"));
        
        tourTipsArea = new JTextArea(8, 25);
        tourTipsArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tourTipsArea.setEditable(false);
        tourTipsArea.setBackground(new Color(248, 248, 248));
        tourTipsArea.setText("游览建议：\n• 提前查看各景点开放时间\n• 准备好交通卡或移动支付\n• 根据天气情况调整行程");
        JScrollPane tipsScrollPane = new JScrollPane(tourTipsArea);
        tipsPanel.add(tipsScrollPane, BorderLayout.CENTER);

        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setTopComponent(routePanel);
        leftSplitPane.setBottomComponent(tipsPanel);
        leftSplitPane.setDividerLocation(0.5);
        leftSplitPane.setResizeWeight(0.5);
        
        leftPanel.add(leftSplitPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(new TitledBorder("游览统计"));
        
        String[] statsColumns = {"统计项目", "数值", "详细信息"};
        tourStatsTableModel = new DefaultTableModel(statsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tourStatsTable = new JTable(tourStatsTableModel);
        tourStatsTable.setRowHeight(25);
        tourStatsTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JScrollPane statsScrollPane = new JScrollPane(tourStatsTable);
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);

        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setBorder(new TitledBorder("景点类型统计"));
        
        String[] typeColumns = {"景点类型", "数量", "占比"};
        tourTypeTableModel = new DefaultTableModel(typeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tourTypeTable = new JTable(tourTypeTableModel);
        tourTypeTable.setRowHeight(25);
        tourTypeTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JScrollPane typeScrollPane = new JScrollPane(tourTypeTable);
        typePanel.add(typeScrollPane, BorderLayout.CENTER);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setTopComponent(statsPanel);
        rightSplitPane.setBottomComponent(typePanel);
        rightSplitPane.setDividerLocation(300);
        rightSplitPane.setResizeWeight(0.6);
        
        rightPanel.add(rightSplitPane, BorderLayout.CENTER);
        
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);
        
        panel.add(mainSplitPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadInitialData() {
        try {
            TourismService.AttractionQueryService queryService = tourismService.getAttractionQueryService();
            List<Attraction> attractions = queryService.searchByName("");
            loadAttractionsToComboBox(attractions);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAttractionsToComboBox(List<Attraction> attractions) {
        startTourCombo.removeAllItems();
        for (Attraction attraction : attractions) {
            startTourCombo.addItem(attraction);
        }
    }
    
    private void performTourPlanning() {
        try {
            Attraction startAttraction = (Attraction) startTourCombo.getSelectedItem();
            String timeText = timeHoursField.getText().trim();
            String distanceText = maxDistanceField.getText().trim();
            String optimizeType = getTourOptimizeType();
            
            if (startAttraction == null) {
                JOptionPane.showMessageDialog(this, "请选择起始景点", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (timeText.isEmpty() || distanceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请填写完整的规划参数", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double timeHours = Double.parseDouble(timeText);
            double maxDistance = Double.parseDouble(distanceText);
            
            TourismService.TourPlanningService tourService = tourismService.getTourPlanningService();
            TourismService.TourPlan plan = tourService.planOneDayTourWithOptimization(
                startAttraction.getId(), new ArrayList<>(), maxDistance, (int)timeHours, optimizeType);
            
            displayTourPlan(plan);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入正确的数字格式", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "游览规划失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getTourOptimizeType() {
        String selected = (String) tourOptimizeTypeCombo.getSelectedItem();
        switch (selected) {
            case "时间最短": return "time";
            case "费用最低": return "cost";
            default: return "distance";
        }
    }
    
    private void displayTourPlan(TourismService.TourPlan plan) {
        tourAttractionTableModel.setRowCount(0);
        tourStatsTableModel.setRowCount(0);
        tourTypeTableModel.setRowCount(0);
        
        if (plan == null || plan.getAttractions().isEmpty()) {
            Object[] errorRow = {"错误信息", "无法生成游览计划", "时间限制过短或距离限制过小"};
            tourStatsTableModel.addRow(errorRow);
            Object[] suggestionRow = {"建议", "增加可用时间或扩大距离范围", "选择其他起始景点"};
            tourStatsTableModel.addRow(suggestionRow);
            tourTipsArea.setText("游览建议：\n• 提前查看各景点开放时间\n• 准备好交通卡或移动支付\n• 根据天气情况调整行程\n• 预留用餐和休息时间\n• 建议穿着舒适的鞋子\n• 注意景点间的交通连接");
            return;
        }
        List<Attraction> attractions = plan.getAttractions();
        int currentTime = 9 * 60;
        for (int i = 0; i < attractions.size(); i++) {
            Attraction attraction = attractions.get(i);

            int hours = currentTime / 60;
            int minutes = currentTime % 60;
            String timeStr = String.format("%02d:%02d", hours, minutes);

            String transportMode;
            String travelTime;
            
            if (i == 0) {
                transportMode = "起始景点";
                travelTime = "-";
            } else {
                // 使用Dijkstra算法获取真实的路径信息
                Attraction prevAttraction = attractions.get(i - 1);
                try {
                    TourismService.PathPlanningService pathService = tourismService.getPathPlanningService();
                    String optimizeType = getTourOptimizeType();
                    DijkstraAlgorithm.PathResult pathResult = pathService.findShortestPath(
                        prevAttraction.getId(), attraction.getId(), optimizeType);
                    
                    if (!pathResult.getPathDetails().isEmpty()) {
                        model.AttractionPath pathDetail = pathResult.getPathDetails().get(0);
                        transportMode = pathDetail.getTransportType() != null ? 
                                      pathDetail.getTransportType().getName() : "步行";
                        travelTime = String.valueOf(pathDetail.getTravelTimeMinutes());
                    } else {
                        double distance = GeoUtil.calculateDistance(
                            prevAttraction.getLatitude(), prevAttraction.getLongitude(),
                            attraction.getLatitude(), attraction.getLongitude()
                        );
                        
                        if (distance <= 1.0) {
                            transportMode = "步行";
                            travelTime = "15";
                        } else if (distance <= 5.0) {
                            transportMode = "地铁/公交";
                            travelTime = "25";
                        } else if (distance <= 10.0) {
                            transportMode = "地铁";
                            travelTime = "35";
                        } else {
                            transportMode = "出租车";
                            travelTime = "40";
                        }
                    }
                } catch (Exception e) {
                    transportMode = "交通工具";
                    travelTime = "30";
                }
            }
            Object[] row = {
                timeStr,
                attraction.getName(),
                attraction.getDistrict(),
                attraction.getRecommendDuration(),
                attraction.getPrice() != null ? String.format("%.0f", attraction.getPrice().doubleValue()) : "免费",
                transportMode,
                travelTime
            };
            tourAttractionTableModel.addRow(row);
            currentTime += attraction.getRecommendDuration();
            if (i < attractions.size() - 1) {
                int moveTime = travelTime.equals("-") ? 0 : Integer.parseInt(travelTime.equals("计算中...") ? "30" : travelTime);
                currentTime += moveTime;
            }
        }
        double attractionTicketCost = 0;
        for (Attraction attraction : attractions) {
            if (attraction.getPrice() != null) {
                attractionTicketCost += attraction.getPrice().doubleValue();
            }
        }

        double transportCost = plan.getTotalCost();
        double totalExpense = attractionTicketCost + transportCost;

        String optimizeType = getTourOptimizeType();
        
        Object[][] basicStats = {
            {"游览景点", String.format("%d 个", attractions.size()), ""},
            {"总距离", String.format("%.2f 公里", plan.getTotalDistance()), 
             "distance".equals(optimizeType) ? "距离已优化" : ""},
            {"总时间", String.format("%d 分钟", plan.getTotalTime()), 
             String.format("%.1f 小时", plan.getTotalTime() / 60.0) + 
             ("time".equals(optimizeType) ? "（时间已优化）" : "")},
            {"景点费用", String.format("%.2f 元", attractionTicketCost), "门票总价"},
            {"交通费用", String.format("%.2f 元", transportCost), 
             "cost".equals(optimizeType) ? "费用已优化" : "基于实际路径计算"},
            {"总花费", String.format("%.2f 元", totalExpense), 
             "景点+交通费用" + ("cost".equals(optimizeType) ? "（费用已优化）" : "")}
        };
        
        for (Object[] row : basicStats) {
            tourStatsTableModel.addRow(row);
        }

        int startHour = 9;
        int endMinutes = startHour * 60 + plan.getTotalTime();
        int endHour = endMinutes / 60;
        int endMin = endMinutes % 60;
        
        Object[][] timeStats = {
            {"开始时间", String.format("%02d:00", startHour), ""},
            {"结束时间", String.format("%02d:%02d", endHour, endMin), ""},
            {"游览时长", String.format("%.1f 小时", plan.getTotalTime() / 60.0), ""}
        };
        
        for (Object[] row : timeStats) {
            tourStatsTableModel.addRow(row);
        }

        java.util.Map<String, Integer> typeCount = new java.util.HashMap<>();
        for (Attraction attr : attractions) {
            String type = attr.getAttractionType() != null ? 
                         attr.getAttractionType().getTypeName() : "其他";
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }
        
        for (java.util.Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            Object[] row = {
                entry.getKey(),
                String.format("%d 个", entry.getValue()),
                String.format("%.1f%%", (double) entry.getValue() / attractions.size() * 100)
            };
            tourTypeTableModel.addRow(row);
        }

        StringBuilder tips = new StringBuilder();
        tips.append("游览建议：\n");
        tips.append("• 提前查看各景点开放时间\n");
        tips.append("• 准备好交通卡或移动支付\n");
        tips.append("• 根据天气情况调整行程\n");
        tips.append("• 预留用餐和休息时间\n");
        tips.append("• 建议穿着舒适的鞋子\n\n");

        switch (optimizeType) {
            case "distance":
                tips.append("距离优化提示：\n");
                tips.append("• 本路线距离已优化，减少体力消耗\n");
                tips.append("• 景点间距离较短，适合步行游览\n");
                tips.append("• 可考虑租借共享单车进一步节省时间");
                break;
            case "time":
                tips.append("时间优化提示：\n");
                tips.append("• 本路线时间已优化，提高游览效率\n");
                tips.append("• 建议严格按照时间安排游览各景点\n");
                tips.append("• 选择快速交通工具（地铁/出租车）");
                break;
            case "cost":
                tips.append("费用优化提示：\n");
                tips.append("• 本路线费用已优化，经济实惠\n");
                tips.append("• 优先选择公交、地铁等经济交通方式\n");
                tips.append("• 可关注景点优惠票价信息");
                break;
        }
        tourTipsArea.setText(tips.toString());
    }
} 