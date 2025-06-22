package algorithm;

import model.Attraction;
import model.AttractionPath;
import java.util.*;

// Dijkstra算法（“路径规划”使用）
public class DijkstraAlgorithm {
    public static class PathResult {
        private List<Attraction> path;          // 路径中的景点列表
        private List<AttractionPath> pathDetails; // 路径详细信息
        private double totalDistance;           // 总距离
        private int totalTime;                 // 总时间
        private double totalCost;              // 总费用
        public PathResult() {
            this.path = new ArrayList<>();
            this.pathDetails = new ArrayList<>();
        }
        public List<Attraction> getPath() { return path; }
        public void setPath(List<Attraction> path) { this.path = path; }
        
        public List<AttractionPath> getPathDetails() { return pathDetails; }
        public void setPathDetails(List<AttractionPath> pathDetails) { this.pathDetails = pathDetails; }
        
        public double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }
        
        public int getTotalTime() { return totalTime; }
        public void setTotalTime(int totalTime) { this.totalTime = totalTime; }
        
        public double getTotalCost() { return totalCost; }
        public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("路径: ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i).getName());
                if (i < path.size() - 1) {
                    sb.append(" -> ");
                }
            }
            sb.append(String.format("\n总距离: %.2f公里", totalDistance));
            sb.append(String.format("\n总时间: %d分钟", totalTime));
            sb.append(String.format("\n总费用: %.2f元", totalCost));
            return sb.toString();
        }
    }
    private Graph graph;
    public DijkstraAlgorithm(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("图对象不能为空");
        }
        this.graph = graph;
    }
    public DijkstraAlgorithm() {
        this.graph = new Graph();
    }
    // 找到从一个起点到一个特定终点的具体路径
    public PathResult findShortestPath(int startId, int endId, String optimizeType) {
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        Map<Integer, AttractionPath> pathEdges = new HashMap<>();
        // 根据 Node 对象的 distance 属性进行比较，distance 值越小的，优先级越高
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        Set<Integer> visited = new HashSet<>();
        for (Integer attractionId : graph.getAllVertexIds()) {
            distances.put(attractionId, Double.MAX_VALUE);
        }
        distances.put(startId, 0.0);
        queue.offer(new Node(startId, 0.0));
        
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentId = current.attractionId;
            
            if (visited.contains(currentId)) {
                continue;
            }
            visited.add(currentId);
            
            if (currentId == endId) {
                break;
            }
            List<AttractionPath> neighbors = graph.getNeighbors(currentId);
            for (AttractionPath path : neighbors) {
                int neighborId = path.getToAttractionId();
                
                if (visited.contains(neighborId)) {
                    continue;
                }
                
                double newDistance = distances.get(currentId) + path.getWeight(optimizeType);
                
                if (newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);
                    previous.put(neighborId, currentId);
                    pathEdges.put(neighborId, path);
                    queue.offer(new Node(neighborId, newDistance));
                }
            }
        }
        
        return reconstructPath(startId, endId, previous, pathEdges, distances.get(endId));
    }
    private PathResult reconstructPath(int startId, int endId, Map<Integer, Integer> previous, 
                                     Map<Integer, AttractionPath> pathEdges, double totalWeight) {
        PathResult result = new PathResult();
        if (!previous.containsKey(endId) && startId != endId) {
            return result;
        }
        List<Integer> pathIds = new ArrayList<>();
        List<AttractionPath> pathDetailsList = new ArrayList<>();
        int current = endId;
        while (current != startId) {
            pathIds.add(current);
            if (pathEdges.containsKey(current)) {
                pathDetailsList.add(pathEdges.get(current));
            }
            current = previous.get(current);
        }
        pathIds.add(startId);
        Collections.reverse(pathIds);
        Collections.reverse(pathDetailsList);
        List<Attraction> attractionPath = new ArrayList<>();
        for (Integer id : pathIds) {
            attractionPath.add(graph.getVertex(id));
        }
        double totalDistance = 0;
        int totalTime = 0;
        double totalCost = 0;
        for (AttractionPath path : pathDetailsList) {
            totalDistance += path.getDistanceKm().doubleValue();
            totalTime += path.getTravelTimeMinutes();
            totalCost += path.getCost().doubleValue();
        }
        result.setPath(attractionPath);
        result.setPathDetails(pathDetailsList);
        result.setTotalDistance(totalDistance);
        result.setTotalTime(totalTime);
        result.setTotalCost(totalCost);
        
        return result;
    }
    private static class Node {
        int attractionId;
        double distance;
        
        Node(int attractionId, double distance) {
            this.attractionId = attractionId;
            this.distance = distance;
        }
    }
} 