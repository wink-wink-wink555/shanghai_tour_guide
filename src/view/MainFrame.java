package view;

import service.TourismService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private TourismService tourismService;
    
    public MainFrame() {
        initializeServices();
        initializeComponents();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("上海市旅游景点导游程序");
        setSize(800, 600);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initializeServices() {
        try {
            tourismService = new TourismService();
            JOptionPane.showMessageDialog(this, "系统初始化成功！\n欢迎使用上海市旅游景点导游程序", "欢迎", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "系统初始化失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("上海市旅游景点导游程序");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton queryButton = createMenuButton("景点查询", "查询和搜索上海市的旅游景点");
        JButton pathButton = createMenuButton("路径规划", "规划2个景点之间的最优路径");
        JButton nearbyButton = createMenuButton("附近景点", "查找指定位置附近的景点推荐");
        JButton tourButton = createMenuButton("游览规划", "制定多景点游览计划");
        JButton allButton = createMenuButton("所有景点", "浏览所有景点信息");

        gbc.gridx = 0; gbc.gridy = 0;
        buttonPanel.add(queryButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        buttonPanel.add(pathButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        buttonPanel.add(nearbyButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        buttonPanel.add(tourButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        buttonPanel.add(allButton, gbc);

        queryButton.addActionListener(e -> openAttractionQuery());
        pathButton.addActionListener(e -> openPathPlanning());
        nearbyButton.addActionListener(e -> openNearbyRecommendation());
        tourButton.addActionListener(e -> openTourPlanning());
        allButton.addActionListener(e -> openAllAttractions());

        JPanel copyrightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        copyrightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        copyrightPanel.setBackground(new Color(248, 248, 248));
        
        JLabel copyrightLabel = new JLabel("<html><center>欢迎使用上海市旅游景点导游程序！<br>©wink-wink-wink555 版权所有</center></html>");
        copyrightLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(100, 100, 100));
        copyrightPanel.add(copyrightLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(copyrightPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JButton createMenuButton(String text, String tooltip) {
        JButton button = new JButton("<html><div style='text-align: center;'><span style='font-size: 14px; font-weight: bold;'>" + text + "</span><br><span style='font-size: 11px; font-weight: normal;'>" + tooltip + "</span></div></html>");
        button.setPreferredSize(new Dimension(200, 60));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setBackground(new Color(240, 248, 255));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setToolTipText(tooltip);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(173, 216, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 248, 255));
            }
        });
        
        return button;
    }

    private void openAttractionQuery() {
        new AttractionQueryPanel(tourismService).setVisible(true);
    }
    
    private void openPathPlanning() {
        new PathPlanningPanel(tourismService).setVisible(true);
    }
    
    private void openNearbyRecommendation() {
        new NearbyRecommendationPanel(tourismService).setVisible(true);
    }
    
    private void openTourPlanning() {
        new TourPlanningPanel(tourismService).setVisible(true);
    }
    
    private void openAllAttractions() {
        new AllAttractionsPanel(tourismService).setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}