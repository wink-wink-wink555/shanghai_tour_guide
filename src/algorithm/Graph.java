package algorithm;

import model.Attraction;
import model.AttractionPath;
import java.util.*;

public class Graph {
    private Map<Integer, List<AttractionPath>> adjacencyList;
    private Map<Integer, Attraction> vertices;
    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.vertices = new HashMap<>();
    }
    public void addVertex(Attraction attraction) {
        if (attraction == null) {
            throw new IllegalArgumentException("景点不能为空");
        }
        vertices.put(attraction.getId(), attraction);// 用attraction的id作为Key
        adjacencyList.putIfAbsent(attraction.getId(), new ArrayList<>());
    }
    public void addEdge(AttractionPath path) {
        if (path == null) {
            throw new IllegalArgumentException("路径不能为空");
        }
        int fromId = path.getFromAttractionId();
        int toId = path.getToAttractionId();
        if (!vertices.containsKey(fromId) || !vertices.containsKey(toId)) {
            throw new IllegalArgumentException("路径的起点或终点不存在于图中");
        }
        adjacencyList.putIfAbsent(fromId, new ArrayList<>());
        adjacencyList.get(fromId).add(path);
    }
    public List<AttractionPath> getNeighbors(int vertexId) {
        return adjacencyList.getOrDefault(vertexId, new ArrayList<>());
    }
    public Attraction getVertex(int vertexId) {
        return vertices.get(vertexId);
    }
    public Set<Integer> getAllVertexIds() {
        return vertices.keySet();
    }
    public int getVertexCount() {
        return vertices.size();
    }
    public int getEdgeCount() {
        int count = 0;
        for (List<AttractionPath> neighbors : adjacencyList.values()) {
            count += neighbors.size();
        }
        return count;
    }
    public boolean isEmpty() {
        return vertices.isEmpty();
    }
    public String getGraphInfo() {
        return String.format("图信息：%d个顶点，%d条边", getVertexCount(), getEdgeCount());
    }
    public Map<Integer, List<AttractionPath>> getAdjacencyList() {
        return adjacencyList;
    }
    public Map<Integer, Attraction> getVertices() {
        return vertices;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph{\n");
        sb.append("  顶点数: ").append(getVertexCount()).append("\n");
        sb.append("  边数: ").append(getEdgeCount()).append("\n");
        sb.append("  邻接表:\n");
        
        for (Map.Entry<Integer, List<AttractionPath>> entry : adjacencyList.entrySet()) {
            int vertexId = entry.getKey();
            List<AttractionPath> edges = entry.getValue();
            Attraction vertex = vertices.get(vertexId);
            String vertexName = vertex != null ? vertex.getName() : "未知";
            
            sb.append("    ").append(vertexId).append("(").append(vertexName).append("): [");
            for (int i = 0; i < edges.size(); i++) {
                AttractionPath edge = edges.get(i);
                Attraction toVertex = vertices.get(edge.getToAttractionId());
                String toVertexName = toVertex != null ? toVertex.getName() : "未知";
                sb.append(edge.getToAttractionId()).append("(").append(toVertexName).append(")");
                if (i < edges.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]\n");
        }
        sb.append("}");
        return sb.toString();
    }
} 