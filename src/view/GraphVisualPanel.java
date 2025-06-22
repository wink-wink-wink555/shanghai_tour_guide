package view;

import algorithm.Graph;
import model.Attraction;
import model.AttractionPath;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GraphVisualPanel extends JPanel {

    private final Graph graph;
    private final Map<Integer, Point> nodePositions;
    private static final int PADDING = 80; // 内边距
    private static final int NODE_RADIUS = 10; // 节点半径

    public GraphVisualPanel(Graph graph) {
        this.graph = graph;
        this.nodePositions = new HashMap<>();
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (graph == null || graph.isEmpty()) {
            g2d.drawString("图中没有数据", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }
        calculateNodePositions();
        resolveOverlaps();
        g2d.setColor(Color.GRAY);
        for (Map.Entry<Integer, java.util.List<AttractionPath>> entry : graph.getAdjacencyList().entrySet()) {
            int fromId = entry.getKey();
            Point fromPoint = nodePositions.get(fromId);
            for (AttractionPath path : entry.getValue()) {
                int toId = path.getToAttractionId();
                Point toPoint = nodePositions.get(toId);
                if (fromPoint != null && toPoint != null) {
                    g2d.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
                }
            }
        }
        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int nodeId = entry.getKey();
            Point p = entry.getValue();
            Attraction attraction = graph.getVertex(nodeId);
            g2d.setColor(new Color(135, 206, 250)); // 天蓝色
            g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            if (attraction != null) {
                String name = attraction.getName();
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(name);
                // 增加文字与节点的垂直间距
                g2d.drawString(name, p.x - textWidth / 2, p.y + NODE_RADIUS + fm.getAscent() + 2);
            }
        }
    }

    private void resolveOverlaps() {
        int iterations = 100;
        double minDistance = NODE_RADIUS * 3; // 节点中心之间的最小距离

        java.util.List<Integer> nodeIds = new java.util.ArrayList<>(nodePositions.keySet());
        int n = nodeIds.size();
        if (n <= 1) return;

        for (int iter = 0; iter < iterations; iter++) {
            boolean hasOverlap = false;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    Point p1 = nodePositions.get(nodeIds.get(i));
                    Point p2 = nodePositions.get(nodeIds.get(j));

                    double dx = p1.x - p2.x;
                    double dy = p1.y - p2.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < minDistance) {
                        hasOverlap = true;
                        double overlap = minDistance - distance;
                        double adjustX = 0;
                        double adjustY = 0;
                        
                        // 如果两点重合，随机推开
                        if (distance == 0.0) {
                            adjustX = (Math.random() - 0.5);
                            adjustY = (Math.random() - 0.5);
                        } else {
                            adjustX = (dx / distance) * overlap * 0.5;
                            adjustY = (dy / distance) * overlap * 0.5;
                        }

                        p1.x += (int)adjustX;
                        p1.y += (int)adjustY;
                        p2.x -= (int)adjustX;
                        p2.y -= (int)adjustY;
                    }
                }
            }
            if (!hasOverlap) break; // 优化：如果一轮没有重叠，则提前结束
        }
    }

    private void calculateNodePositions() {
        nodePositions.clear();
        if (graph.isEmpty()) return;
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;

        for (int nodeId : graph.getAllVertexIds()) {
            Attraction attr = graph.getVertex(nodeId);
            if (attr != null) {
                minLat = Math.min(minLat, attr.getLatitude());
                maxLat = Math.max(maxLat, attr.getLatitude());
                minLon = Math.min(minLon, attr.getLongitude());
                maxLon = Math.max(maxLon, attr.getLongitude());
            }
        }
        if (minLat == Double.MAX_VALUE) {
             nodePositions.put(graph.getAllVertexIds().iterator().next(), new Point(getWidth() / 2, getHeight() / 2));
             return;
        }
        double geoWidth = maxLon - minLon;
        double geoHeight = maxLat - minLat;
        
        int availableScreenWidth = getWidth() - 2 * PADDING;
        int availableScreenHeight = getHeight() - 2 * PADDING;

        double scaleX = (geoWidth == 0) ? 1 : availableScreenWidth / geoWidth;
        double scaleY = (geoHeight == 0) ? 1 : availableScreenHeight / geoHeight;
        double scale = Math.min(scaleX, scaleY);
        for (int nodeId : graph.getAllVertexIds()) {
            Attraction attr = graph.getVertex(nodeId);
            if (attr != null) {
                int screenX = PADDING + (int) ((attr.getLongitude() - minLon) * scale);
                int screenY = PADDING + (int) ((maxLat - attr.getLatitude()) * scale); // Y轴反向
                nodePositions.put(nodeId, new Point(screenX, screenY));
            }
        }
    }
} 