package view;

import service.TourismService;
import model.Attraction;
import algorithm.DijkstraAlgorithm;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PathPlanningPanel extends JFrame {
    
    private TourismService tourismService;

    private JComboBox<Attraction> startAttractionCombo;
    private JComboBox<Attraction> endAttractionCombo;
    private JComboBox<String> optimizeTypeCombo;

    private JTable pathRouteTable;
    private DefaultTableModel pathRouteTableModel;
    private JTable pathStatsTable;
    private DefaultTableModel pathStatsTableModel;
    private JTable pathDetailsTable;
    private DefaultTableModel pathDetailsTableModel;
    
    public PathPlanningPanel(TourismService tourismService) {
        this.tourismService = tourismService;
        initializeComponents();
        loadInitialData();
        
        setTitle("路径规划");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("路径规划设置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        startAttractionCombo = new JComboBox<>();
        endAttractionCombo = new JComboBox<>();
        optimizeTypeCombo = new JComboBox<>(new String[]{"距离最短", "时间最短", "费用最低"});
        JButton planButton = new JButton("规划路径");
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("起点景点:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(startAttractionCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("终点景点:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(endAttractionCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("优化方式:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(optimizeTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(planButton, gbc);

        JPanel resultPanel = createPathResultPanel();

        planButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performPathPlanning();
            }
        });
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);
        
        add(panel);
    }
    
    private JPanel createPathResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new TitledBorder("路径景点"));
        
        String[] routeColumns = {"序号", "景点名称", "区域", "门票费用(元)", "到达方式"};
        pathRouteTableModel = new DefaultTableModel(routeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pathRouteTable = new JTable(pathRouteTableModel);
        pathRouteTable.setRowHeight(25);
        pathRouteTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JScrollPane routeScrollPane = new JScrollPane(pathRouteTable);
        leftPanel.add(routeScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBorder(new TitledBorder("路径统计"));
        
        String[] statsColumns = {"统计项目", "数值"};
        pathStatsTableModel = new DefaultTableModel(statsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pathStatsTable = new JTable(pathStatsTableModel);
        pathStatsTable.setRowHeight(25);
        pathStatsTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JScrollPane statsScrollPane = new JScrollPane(pathStatsTable);
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(new TitledBorder("详细路径"));
        
        String[] detailColumns = {"起点", "终点", "距离(km)", "时间(分钟)", "费用(元)", "交通方式"};
        pathDetailsTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pathDetailsTable = new JTable(pathDetailsTableModel);
        pathDetailsTable.setRowHeight(25);
        pathDetailsTable.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        JScrollPane detailsScrollPane = new JScrollPane(pathDetailsTable);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setTopComponent(statsPanel);
        rightSplitPane.setBottomComponent(detailsPanel);
        rightSplitPane.setDividerLocation(150);
        rightPanel.add(rightSplitPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadInitialData() {
        try {
            TourismService.AttractionQueryService queryService = tourismService.getAttractionQueryService();
            List<Attraction> attractions = queryService.searchByName("");
            loadAttractionsToComboBoxes(attractions);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAttractionsToComboBoxes(List<Attraction> attractions) {
        startAttractionCombo.removeAllItems();
        endAttractionCombo.removeAllItems();
        
        for (Attraction attraction : attractions) {
            startAttractionCombo.addItem(attraction);
            endAttractionCombo.addItem(attraction);
        }
    }
    
    private void performPathPlanning() {
        try {
            Attraction startAttraction = (Attraction) startAttractionCombo.getSelectedItem();
            Attraction endAttraction = (Attraction) endAttractionCombo.getSelectedItem();
            String optimizeType = getOptimizeType();
            
            if (startAttraction == null || endAttraction == null) {
                JOptionPane.showMessageDialog(this, "请选择起点和终点", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (startAttraction.getId() == endAttraction.getId()) {
                JOptionPane.showMessageDialog(this, "起点和终点不能相同", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            TourismService.PathPlanningService pathService = tourismService.getPathPlanningService();
            DijkstraAlgorithm.PathResult result = pathService.findShortestPath(
                startAttraction.getId(), endAttraction.getId(), optimizeType);
            
            displayPathResult(result);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "路径规划失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getOptimizeType() {
        String selected = (String) optimizeTypeCombo.getSelectedItem();
        switch (selected) {
            case "时间最短": return "time";
            case "费用最低": return "cost";
            default: return "distance";
        }
    }
    
    private void displayPathResult(DijkstraAlgorithm.PathResult result) {
        pathRouteTableModel.setRowCount(0);
        pathStatsTableModel.setRowCount(0);
        pathDetailsTableModel.setRowCount(0);
        
        if (result == null || result.getPath().isEmpty()) {
            Object[] errorRow = {"错误信息", "无法找到从起点到终点的路径，请检查起点和终点是否连通"};
            pathStatsTableModel.addRow(errorRow);
            return;
        }
        List<Attraction> path = result.getPath();
        for (int i = 0; i < path.size(); i++) {
            Attraction attraction = path.get(i);
            String arrivalMethod;
            if (i == 0) {
                arrivalMethod = "起点";
            } else {
                // 从 PathResult 中获取前一段路径的描述作为交通方式
                arrivalMethod = result.getPathDetails().get(i - 1).getDescription();
            }

            String ticketPrice = "免费";
            if (attraction.getPrice() != null && attraction.getPrice().doubleValue() > 0) {
                ticketPrice = String.format("%.2f", attraction.getPrice().doubleValue());
            }
            
            Object[] row = {
                i + 1,
                attraction.getName(),
                attraction.getDistrict(),
                ticketPrice,
                arrivalMethod
            };
            pathRouteTableModel.addRow(row);
        }

        double attractionCost = 0.0;
        for (Attraction attraction : path) {
            if (attraction.getPrice() != null) {
                attractionCost += attraction.getPrice().doubleValue();
            }
        }

        double transportCost = result.getTotalCost();
        double totalCost = transportCost + attractionCost;

        Object[][] statsData = {
            {"途经景点", String.format("%d 个", path.size())},
            {"通勤距离", String.format("%.2f 公里", result.getTotalDistance())},
            {"通勤时间", String.format("%d 分钟 (%.1f小时)", result.getTotalTime(), result.getTotalTime() / 60.0)},
            {"通勤费用", String.format("%.2f 元", transportCost)},
            {"景点费用", String.format("%.2f 元", attractionCost)},
            {"总费用", String.format("%.2f 元", totalCost)},
        };
        
        for (Object[] row : statsData) {
            pathStatsTableModel.addRow(row);
        }

        List<model.AttractionPath> pathDetails = result.getPathDetails();
        if (!pathDetails.isEmpty() && pathDetails.size() == path.size() - 1) {
            try {
                for (int i = 0; i < pathDetails.size(); i++) {
                    model.AttractionPath pathDetail = pathDetails.get(i);
                    Attraction from = path.get(i);
                    Attraction to = path.get(i + 1);

                    String transportMode = pathDetail.getTransportType() != null ? 
                                         pathDetail.getTransportType().getName() : "未知交通方式";
                    
                    Object[] row = {
                        from.getName(),
                        to.getName(),
                        String.format("%.2f", pathDetail.getDistanceKm().doubleValue()),
                        String.valueOf(pathDetail.getTravelTimeMinutes()),
                        String.format("%.1f", pathDetail.getCost().doubleValue()),
                        transportMode + " - " + (pathDetail.getDescription() != null ? pathDetail.getDescription() : "")
                    };
                    pathDetailsTableModel.addRow(row);
                }
            } catch (Exception e) {
                Object[] errorRow = {
                    "数据错误",
                    "获取路径详情失败",
                    "请检查数据",
                    "—",
                    "—",
                    "错误: " + e.getMessage()
                };
                pathDetailsTableModel.addRow(errorRow);
            }
        } else {
            for (int i = 0; i < path.size() - 1; i++) {
                Attraction from = path.get(i);
                Attraction to = path.get(i + 1);
                
                Object[] row = {
                    from.getName(),
                    to.getName(),
                    "数据缺失",
                    "数据缺失",
                    "数据缺失",
                    "未找到具体路径信息"
                };
                pathDetailsTableModel.addRow(row);
            }
        }
    }
} 