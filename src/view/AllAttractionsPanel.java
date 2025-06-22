package view;

import service.TourismService;
import model.Attraction;
import algorithm.Graph;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AllAttractionsPanel extends JFrame {
    private TourismService tourismService;
    private DefaultTableModel allAttractionsModel;
    private JTable allAttractionsTable;
    private JButton detailButton;
    private List<Attraction> currentAttractions;
    
    public AllAttractionsPanel(TourismService tourismService) {
        this.tourismService = tourismService;
        initializeComponents();
        
        setTitle("所有景点浏览");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton refreshButton = new JButton("刷新列表");
        detailButton = new JButton("查看详情");
        detailButton.setEnabled(false); // 初始状态禁用
        JButton viewGraphButton = new JButton("查看图结构");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(detailButton);
        
        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.add(viewGraphButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(rightButtonPanel, BorderLayout.EAST);

        String[] columns = {"ID", "景点名称", "区域", "类型", "评分", "票价(元)", "地址", "开放时间"};
        allAttractionsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        allAttractionsTable = new JTable(allAttractionsModel);
        allAttractionsTable.setRowHeight(25);
        allAttractionsTable.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        allAttractionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 表格行选择事件监听
        allAttractionsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = allAttractionsTable.getSelectedRow();
                    detailButton.setEnabled(selectedRow >= 0);
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(allAttractionsTable);
        scrollPane.setBorder(new TitledBorder("所有景点列表"));

        loadAllAttractions(allAttractionsModel);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllAttractions(allAttractionsModel);
            }
        });
        
        detailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAttractionDetail();
            }
        });
        
        viewGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGraphStructure();
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        add(panel);
    }

    private void loadAllAttractions(DefaultTableModel model) {
        try {
            TourismService.AttractionQueryService queryService = tourismService.getAttractionQueryService();
            currentAttractions = queryService.searchByName("");
            
            model.setRowCount(0);
            
            for (Attraction attraction : currentAttractions) {
                String ticketPrice = "免费";
                if (attraction.getPrice() != null && attraction.getPrice().doubleValue() > 0) {
                    ticketPrice = String.format("%.2f", attraction.getPrice().doubleValue());
                }

                Object[] row = {
                    attraction.getId(),
                    attraction.getName(),
                    attraction.getDistrict(),
                    attraction.getAttractionType() != null ? attraction.getAttractionType().getTypeName() : "未知",
                    attraction.getRating(),
                    ticketPrice,
                    attraction.getAddress(),
                    attraction.getOpeningHours()
                };
                model.addRow(row);
            }
            JOptionPane.showMessageDialog(this, "成功加载 " + currentAttractions.size() + " 个景点", "信息", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载景点数据失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAttractionDetail() {
        int selectedRow = allAttractionsTable.getSelectedRow();
        if (selectedRow >= 0 && currentAttractions != null && selectedRow < currentAttractions.size()) {
            Attraction selectedAttraction = currentAttractions.get(selectedRow);
            AttractionDetailDialog dialog = new AttractionDetailDialog(this, selectedAttraction);
            dialog.setVisible(true);
        }
    }

    private void showGraphStructure() {
        try {
            Graph graph = tourismService.getGraph();
            GraphDisplayDialog dialog = new GraphDisplayDialog(this, graph);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载图结构失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 