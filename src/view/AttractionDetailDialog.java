package view;

import model.Attraction;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AttractionDetailDialog extends JDialog {
    
    private Attraction attraction;
    
    public AttractionDetailDialog(JFrame parent, Attraction attraction) {
        super(parent, "景点详情", true);
        this.attraction = attraction;
        initializeComponents();
        
        setSize(600, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = createTitlePanel();

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tabbedPane.setPreferredSize(new Dimension(570, 480));

        JPanel basicInfoPanel = createBasicInfoPanel();
        tabbedPane.addTab("基本信息", basicInfoPanel);

        JPanel detailInfoPanel = createDetailInfoPanel();
        tabbedPane.addTab("详细信息", detailInfoPanel);

        JPanel locationPanel = createLocationPanel();
        tabbedPane.addTab("地理位置", locationPanel);

        JPanel descriptionPanel = createDescriptionPanel();
        tabbedPane.addTab("简介描述", descriptionPanel);
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel titleLabel = new JLabel(attraction.getName());
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 102, 153));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 3));
        if (attraction.getEnglishName() != null && !attraction.getEnglishName().trim().isEmpty()) {
            JLabel englishLabel = new JLabel(attraction.getEnglishName());
            englishLabel.setFont(new Font("Arial", Font.ITALIC, 13));
            englishLabel.setForeground(new Color(102, 102, 102));
            subtitlePanel.add(englishLabel);
        }
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        if (subtitlePanel.getComponentCount() > 0) {
            titlePanel.add(subtitlePanel, BorderLayout.SOUTH);
        }
        
        return titlePanel;
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addInfoRow(panel, gbc, 0, "景点ID:", String.valueOf(attraction.getId()));
        addInfoRow(panel, gbc, 1, "景点名称:", attraction.getName());

        String englishName = (attraction.getEnglishName() != null && !attraction.getEnglishName().trim().isEmpty()) 
            ? attraction.getEnglishName() : "暂无";
        addInfoRow(panel, gbc, 2, "英文名称:", englishName);
        
        addInfoRow(panel, gbc, 3, "所在区域:", attraction.getDistrict());

        String typeName = (attraction.getAttractionType() != null) 
            ? attraction.getAttractionType().getTypeName() : "未知";
        addInfoRow(panel, gbc, 4, "景点类型:", typeName);

        String rating = (attraction.getRating() != null) 
            ? String.format("%.1f 分", attraction.getRating().doubleValue()) : "暂无评分";
        addInfoRow(panel, gbc, 5, "评分:", rating);

        String price = "免费";
        if (attraction.getPrice() != null && attraction.getPrice().doubleValue() > 0) {
            price = String.format("%.2f 元", attraction.getPrice().doubleValue());
        }
        addInfoRow(panel, gbc, 6, "门票价格:", price);

        String duration = attraction.getRecommendDuration() + " 分钟";
        addInfoRow(panel, gbc, 7, "推荐游览时长:", duration);
        
        return panel;
    }
    
    private JPanel createDetailInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String address = (attraction.getAddress() != null && !attraction.getAddress().trim().isEmpty()) 
            ? attraction.getAddress() : "暂无";
        addInfoRow(panel, gbc, 0, "详细地址:", address);

        String openingHours = (attraction.getOpeningHours() != null && !attraction.getOpeningHours().trim().isEmpty()) 
            ? attraction.getOpeningHours() : "暂无";
        addInfoRow(panel, gbc, 1, "开放时间:", openingHours);

        String phone = (attraction.getPhone() != null && !attraction.getPhone().trim().isEmpty()) 
            ? attraction.getPhone() : "暂无";
        addInfoRow(panel, gbc, 2, "联系电话:", phone);

        String website = (attraction.getWebsite() != null && !attraction.getWebsite().trim().isEmpty()) 
            ? attraction.getWebsite() : "暂无";
        addInfoRow(panel, gbc, 3, "官方网站:", website);

        String maxCapacity = String.format("%,d 人", attraction.getMaxCapacity());
        addInfoRow(panel, gbc, 4, "最大人流量:", maxCapacity);

        addInfoRow(panel, gbc, 5, "类型ID:", String.valueOf(attraction.getTypeId()));
        
        return panel;
    }
    
    private JPanel createLocationPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel coordPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addInfoRow(coordPanel, gbc, 0, "纬度:", String.format("%.8f°", attraction.getLatitude()));
        addInfoRow(coordPanel, gbc, 1, "经度:", String.format("%.8f°", attraction.getLongitude()));
        
        panel.add(coordPanel, BorderLayout.NORTH);

        JPanel coordinatePanel = new JPanel(new BorderLayout());
        coordinatePanel.setBorder(new TitledBorder("坐标信息"));
        
        JTextArea coordinateInfo = new JTextArea();
        coordinateInfo.setText("地理坐标系统：WGS84\n" +
            "精确坐标：" + String.format("%.8f, %.8f", attraction.getLatitude(), attraction.getLongitude()) + "\n" +
            "所在区域：" + attraction.getDistrict() + "\n" +
            "详细地址：" + (attraction.getAddress() != null ? attraction.getAddress() : "暂无"));
        coordinateInfo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        coordinateInfo.setEditable(false);
        coordinateInfo.setBackground(new Color(248, 248, 248));
        coordinateInfo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        coordinateInfo.setRows(4);
        
        coordinatePanel.add(coordinateInfo, BorderLayout.CENTER);
        panel.add(coordinatePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel briefPanel = new JPanel(new BorderLayout());
        briefPanel.setBorder(new TitledBorder("景点简介"));
        
        String description = (attraction.getDescription() != null && !attraction.getDescription().trim().isEmpty()) 
            ? attraction.getDescription() : "暂无简介信息";
        
        JTextArea briefArea = new JTextArea(description);
        briefArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        briefArea.setEditable(false);
        briefArea.setLineWrap(true);
        briefArea.setWrapStyleWord(true);
        briefArea.setBackground(new Color(248, 248, 248));
        briefArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        briefArea.setRows(4);
        
        JScrollPane briefScrollPane = new JScrollPane(briefArea);
        briefScrollPane.setPreferredSize(new Dimension(520, 100));
        briefPanel.add(briefScrollPane, BorderLayout.CENTER);

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(new TitledBorder("详细介绍"));
        
        String detailedInfo = (attraction.getDetailedInfo() != null && !attraction.getDetailedInfo().trim().isEmpty()) 
            ? attraction.getDetailedInfo() : "暂无详细介绍信息";
        
        JTextArea detailArea = new JTextArea(detailedInfo);
        detailArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setBackground(new Color(248, 248, 248));
        detailArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        detailArea.setRows(8);
        
        JScrollPane detailScrollPane = new JScrollPane(detailArea);
        detailScrollPane.setPreferredSize(new Dimension(520, 180));
        detailPanel.add(detailScrollPane, BorderLayout.CENTER);
        
        panel.add(briefPanel, BorderLayout.NORTH);
        panel.add(detailPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 5, 0));
        
        JButton closeButton = new JButton("关闭");
        closeButton.setPreferredSize(new Dimension(80, 30));
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(closeButton);
        return buttonPanel;
    }
    
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("微软雅黑", Font.BOLD, 12));
        labelComponent.setForeground(new Color(68, 68, 68));
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        valueComponent.setForeground(new Color(51, 51, 51));
        panel.add(valueComponent, gbc);
    }
} 