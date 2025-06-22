package view;

import algorithm.Graph;
import javax.swing.*;
import java.awt.*;

public class GraphDisplayDialog extends JDialog {

    public GraphDisplayDialog(Frame owner, Graph graph) {
        super(owner, "图结构详情", true);
        setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 文字描述面板
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setText(graph.toString());
        JScrollPane textScrollPane = new JScrollPane(textArea);
        tabbedPane.addTab("文字描述", textScrollPane);

        // 图形视图面板
        GraphVisualPanel visualPanel = new GraphVisualPanel(graph);
        tabbedPane.addTab("图形视图", visualPanel);
        
        add(tabbedPane);
        
        setLocationRelativeTo(owner);
    }
} 