package algorithm;

import model.Attraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 贪心算法（“游览规划”使用）
public class TourPlanningAlgorithm {
    private final Graph graph;
    private final DijkstraAlgorithm dijkstra;
    private final Map<Integer, Attraction> attractions;
    public static class TourPlan {
        private final List<Attraction> attractions;
        private double totalDistance;
        private int totalTime;
        private double totalCost;
        public TourPlan() {
            this.attractions = new ArrayList<>();
            this.totalDistance = 0.0;
            this.totalTime = 0;
            this.totalCost = 0.0;
        }
        public void addAttraction(Attraction attraction, DijkstraAlgorithm.PathResult pathResult) {
            this.attractions.add(attraction);
            if (pathResult != null) {
                this.totalDistance += pathResult.getTotalDistance();
                this.totalTime += pathResult.getTotalTime();
                this.totalCost += pathResult.getTotalCost();
            }
            // 加上景点的游览时间
            this.totalTime += attraction.getRecommendDuration();
        }

        public List<Attraction> getAttractions() {
            return attractions;
        }

        public double getTotalDistance() {
            return totalDistance;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public double getTotalCost() {
            return totalCost;
        }
    }

    public TourPlanningAlgorithm(Graph graph, DijkstraAlgorithm dijkstra, Map<Integer, Attraction> attractions) {
        this.graph = graph;
        this.dijkstra = dijkstra;
        this.attractions = attractions;
    }

    public TourPlan planTour(int startAttractionId, double maxDistance, int maxTime, String optimizeType) {
        TourPlan tourPlan = new TourPlan();
        List<Integer> visitedAttractionIds = new ArrayList<>();
        Attraction currentAttraction = attractions.get(startAttractionId);
        if (currentAttraction == null) {
            return tourPlan; // 如果起点不存在，返回空计划
        }
        // 首先将起点加入计划
        tourPlan.addAttraction(currentAttraction, null);
        visitedAttractionIds.add(startAttractionId);
        while (true) {
            Attraction bestNextAttraction = null;
            DijkstraAlgorithm.PathResult bestPathResult = null;
            double maxScore = -1.0;
            // 寻找下一个最佳景点
            for (Integer candidateId : attractions.keySet()) {
                if (visitedAttractionIds.contains(candidateId)) {
                    continue; // 跳过已访问的景点
                }
                // 使用Dijkstra计算从当前景点到候选景点的成本
                DijkstraAlgorithm.PathResult pathResult = dijkstra.findShortestPath(currentAttraction.getId(), candidateId, optimizeType);
                if (pathResult == null || pathResult.getPath().isEmpty()) {
                    continue; // 无法到达该候选景点
                }
                Attraction candidateAttraction = attractions.get(candidateId);
                int estimatedTime = tourPlan.getTotalTime() + pathResult.getTotalTime() + candidateAttraction.getRecommendDuration();
                double estimatedDistance = tourPlan.getTotalDistance() + pathResult.getTotalDistance();
                // 检查是否超出资源限制
                if (estimatedTime > maxTime || estimatedDistance > maxDistance) {
                    continue;
                }
                // 贪心策略：选择评分高、成本低的景点 (value/cost)
                double cost = getCost(pathResult, optimizeType);
                double score = (candidateAttraction.getRating() != null ? candidateAttraction.getRating().doubleValue() : 3.0) / (cost + 1); // +1 避免除以0
                if (score > maxScore) {
                    maxScore = score;
                    bestNextAttraction = candidateAttraction;
                    bestPathResult = pathResult;
                }
            }
            if (bestNextAttraction != null) {
                // 找到了最佳的下一个景点，将其加入计划
                tourPlan.addAttraction(bestNextAttraction, bestPathResult);
                visitedAttractionIds.add(bestNextAttraction.getId());
                currentAttraction = bestNextAttraction;
            } else {
                // 没有找到任何满足条件的下一个景点，结束规划
                break;
            }
        }
        return tourPlan;
    }
    private double getCost(DijkstraAlgorithm.PathResult pathResult, String optimizeType) {
        switch (optimizeType) {
            case "time":
                return pathResult.getTotalTime();
            case "cost":
                return pathResult.getTotalCost();
            case "distance":
            default:
                return pathResult.getTotalDistance();
        }
    }
} 