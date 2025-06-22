package view;

import service.TourismService;
import model.Attraction;
import model.AttractionType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AttractionQueryPanel extends JFrame {
    
    private TourismService tourismService;

    private JTextField searchField;
    private JComboBox<String> districtCombo;
    private JPanel typeCheckBoxPanel;
    private List<JCheckBox> typeCheckBoxes;
    private List<AttractionType> allTypes;
    private JButton sortButton;
    private boolean sortByRating = false;
    private JTable attractionTable;
    private DefaultTableModel attractionTableModel;
    private JButton detailButton;
    private List<Attraction> currentSearchResults;
    
    public AttractionQueryPanel(TourismService tourismService) {
        this.tourismService = tourismService;
        initializeComponents();
        loadInitialData();
        
        setTitle("景点查询");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel queryConditionPanel = new JPanel();
        queryConditionPanel.setLayout(new BoxLayout(queryConditionPanel, BoxLayout.Y_AXIS));
        queryConditionPanel.setBorder(new TitledBorder("查询条件"));

        JPanel firstRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        districtCombo = new JComboBox<>();
        
        firstRowPanel.add(new JLabel("关键词:"));
        firstRowPanel.add(searchField);
        firstRowPanel.add(new JLabel("区域:"));
        firstRowPanel.add(districtCombo);

        JPanel secondRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRowPanel.add(new JLabel("类型:"));

        typeCheckBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        typeCheckBoxes = new ArrayList<>();
        allTypes = new ArrayList<>();
        
        secondRowPanel.add(typeCheckBoxPanel);

        JPanel thirdRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton searchButton = new JButton("搜索");
        searchButton.setPreferredSize(new Dimension(120, 35));
        searchButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        thirdRowPanel.add(searchButton);
        
        queryConditionPanel.add(firstRowPanel);
        queryConditionPanel.add(secondRowPanel);
        queryConditionPanel.add(thirdRowPanel);

        String[] columns = {"ID", "景点名称", "区域", "类型", "评分", "票价(元)", "开放时间", "简介"};
        attractionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attractionTable = new JTable(attractionTableModel);
        attractionTable.setRowHeight(30);
        attractionTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        attractionTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        attractionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        attractionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = attractionTable.getSelectedRow();
                    boolean hasSelection = selectedRow >= 0;
                    boolean hasContent = currentSearchResults != null && !currentSearchResults.isEmpty();
                    detailButton.setEnabled(hasSelection && hasContent);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(attractionTable);
        scrollPane.setBorder(new TitledBorder("查询结果"));

        JPanel tableBottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sortButton = new JButton("按评分排序");
        detailButton = new JButton("查看详情");
        sortButton.setEnabled(false);
        detailButton.setEnabled(false);
        
        tableBottomPanel.add(sortButton);
        tableBottomPanel.add(detailButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAttractionSearch();
            }
        });

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentSearchResults != null && !currentSearchResults.isEmpty()) {
                    sortByRating = !sortByRating;
                    sortButton.setText(sortByRating ? "按默认排序" : "按评分排序");
                    performAttractionSearch();
                }
            }
        });

        detailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAttractionDetail();
            }
        });
        
        panel.add(queryConditionPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableBottomPanel, BorderLayout.SOUTH);
        
        add(panel);
    }

    private void loadInitialData() {
        try {
            TourismService.AttractionQueryService queryService = tourismService.getAttractionQueryService();

            List<String> districts = queryService.getAllDistricts();
            districtCombo.removeAllItems();
            districtCombo.addItem("所有区域");
            for (String district : districts) {
                districtCombo.addItem(district);
            }

            List<AttractionType> types = queryService.getAllTypes();
            allTypes.clear();
            allTypes.addAll(types);

            typeCheckBoxPanel.removeAll();
            typeCheckBoxes.clear();

            for (AttractionType type : types) {
                JCheckBox checkBox = new JCheckBox(type.getTypeName());
                checkBox.putClientProperty("attractionType", type);
                typeCheckBoxes.add(checkBox);
                typeCheckBoxPanel.add(checkBox);
            }

            typeCheckBoxPanel.revalidate();
            typeCheckBoxPanel.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performAttractionSearch() {
        try {
            TourismService.AttractionQueryService queryService = tourismService.getAttractionQueryService();
            List<Attraction> results = new ArrayList<>();
            
            String keyword = searchField.getText().trim();
            String selectedDistrict = (String) districtCombo.getSelectedItem();

            List<AttractionType> selectedTypes = new ArrayList<>();
            for (JCheckBox checkBox : typeCheckBoxes) {
                if (checkBox.isSelected()) {
                    AttractionType type = (AttractionType) checkBox.getClientProperty("attractionType");
                    if (type != null) {
                        selectedTypes.add(type);
                    }
                }
            }

            if (keyword.isEmpty()) {
                results = queryService.searchByName(""); // 获取所有
            } else {
                results = queryService.searchByName(keyword);
            }

            if (selectedDistrict != null && !"所有区域".equals(selectedDistrict)) {
                List<Attraction> filteredResults = new ArrayList<>();
                for (Attraction attraction : results) {
                    if (selectedDistrict.equals(attraction.getDistrict())) {
                        filteredResults.add(attraction);
                    }
                }
                results = filteredResults;
            }

            if (!selectedTypes.isEmpty()) {
                List<Attraction> filteredResults = new ArrayList<>();
                for (Attraction attraction : results) {
                    if (attraction.getAttractionType() != null) {
                        for (AttractionType selectedType : selectedTypes) {
                            if (attraction.getAttractionType().getId() == selectedType.getId()) {
                                filteredResults.add(attraction);
                                break;
                            }
                        }
                    }
                }
                results = filteredResults;
            }

            if (sortByRating) {
                results.sort((a, b) -> {
                    Double ratingA = a.getRating() != null ? a.getRating().doubleValue() : 0.0;
                    Double ratingB = b.getRating() != null ? b.getRating().doubleValue() : 0.0;
                    return ratingB.compareTo(ratingA);
                });
            }
            
            displayAttractionResults(results);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayAttractionResults(List<Attraction> attractions) {
        currentSearchResults = attractions;
        attractionTableModel.setRowCount(0);
        
        for (Attraction attraction : attractions) {
            Object[] row = {
                attraction.getId(),
                attraction.getName(),
                attraction.getDistrict(),
                attraction.getAttractionType() != null ? attraction.getAttractionType().getTypeName() : "未知",
                attraction.getRating() != null ? attraction.getRating().doubleValue() : 0.0,
                attraction.getPrice() != null ? attraction.getPrice().doubleValue() : 0.0,
                attraction.getOpeningHours(),
                attraction.getDescription()
            };
            attractionTableModel.addRow(row);
        }

        boolean hasContent = attractions != null && !attractions.isEmpty();
        sortButton.setEnabled(hasContent);
    }
    
    private void showAttractionDetail() {
        int selectedRow = attractionTable.getSelectedRow();
        if (selectedRow >= 0 && currentSearchResults != null && selectedRow < currentSearchResults.size()) {
            Attraction selectedAttraction = currentSearchResults.get(selectedRow);
            AttractionDetailDialog dialog = new AttractionDetailDialog(this, selectedAttraction);
            dialog.setVisible(true);
        }
    }
} 