package service;

import algorithm.DijkstraAlgorithm;
import algorithm.Graph;
import algorithm.TourPlanningAlgorithm;
import dao.AttractionDAO;
import dao.AttractionPathDAO;
import dao.AttractionTypeDAO;
import model.*;
import util.GeoUtil;

import java.util.*;

public class TourismService {

    private AttractionDAO attractionDAO;
    private AttractionPathDAO pathDAO;
    private Graph graph;
    private DijkstraAlgorithm dijkstraAlgorithm;
    private TourPlanningAlgorithm tourPlanningAlgorithm;

    public TourismService() {
        this.attractionDAO = new AttractionDAO();
        this.pathDAO = new AttractionPathDAO();
        this.graph = new Graph();
        this.dijkstraAlgorithm = new DijkstraAlgorithm(graph);
        initializeGraph();
        this.tourPlanningAlgorithm = new TourPlanningAlgorithm(graph, dijkstraAlgorithm, graph.getVertices());
    }
    public void initializeGraph() {
        try {
            // 第一步：添加所有景点（顶点）
            List<Attraction> attractions = attractionDAO.getAllAttractions();
            for (Attraction attraction : attractions) {
                graph.addVertex(attraction);
            }
            // 第二步：添加所有路径（边）
            List<AttractionPath> paths = pathDAO.getAllPaths();
            for (AttractionPath path : paths) {
                graph.addEdge(path);
            }
            System.out.println("图数据初始化完成：" + attractions.size() + "个景点，" + paths.size() + "条路径");
            System.out.println(graph.getGraphInfo());
        } catch (Exception e) {
            System.err.println("图初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Graph getGraph() {
        return this.graph;
    }

    public static class AttractionQueryService {
        private AttractionDAO attractionDAO;
        private AttractionTypeDAO typeDAO;

        public AttractionQueryService() {
            this.attractionDAO = new AttractionDAO();
            this.typeDAO = new AttractionTypeDAO();
        }
        public List<Attraction> searchByName(String name) {
            return attractionDAO.searchAttractionsByName(name);
        }
        public List<AttractionType> getAllTypes() {
            return typeDAO.getAllAttractionTypes();
        }
        public List<String> getAllDistricts() {
            return attractionDAO.getAllDistricts();
        }
    }
    public class PathPlanningService {
        public DijkstraAlgorithm.PathResult findShortestPath(int startId, int endId, String optimizeType) {
            return dijkstraAlgorithm.findShortestPath(startId, endId, optimizeType);
        }
    }
    public class NearbyRecommendationService {
        public List<Attraction> recommendNearbyAttractions(double latitude, double longitude,
                                                           double radiusKm, int limit) {
            List<Attraction> allAttractions = attractionDAO.getAllAttractions();
            List<AttractionDistance> nearbyAttractions = new ArrayList<>();

            for (Attraction attraction : allAttractions) {
                double distance = GeoUtil.calculateDistance(latitude, longitude,
                        attraction.getLatitude(), attraction.getLongitude());
                if (distance <= radiusKm) {
                    nearbyAttractions.add(new AttractionDistance(attraction, distance));
                }
            }
            // 按照距离排序
            nearbyAttractions.sort(Comparator.comparingDouble(AttractionDistance::getDistance));
            List<Attraction> result = new ArrayList<>();
            int count = 0;
            for (AttractionDistance attractionDistance : nearbyAttractions) {
                if (count >= limit) {
                    break; // 达到 limit 后终止循环
                }
                result.add(attractionDistance.getAttraction());
                count++;
            }
            return result;
        }
    }
    public class TourPlanningService {
        public TourPlan planOneDayTourWithOptimization(int startAttractionId, List<Integer> preferredTypes,
                                                       double maxDistance, int availableTimeHours, String optimizeType) {
            // 核心逻辑委托给 TourPlanningAlgorithm
            algorithm.TourPlanningAlgorithm.TourPlan algorithmPlan = tourPlanningAlgorithm.planTour(
                    startAttractionId, maxDistance, availableTimeHours * 60, optimizeType);
            // 将算法层的 Plan 适配为 Service 层的 Plan
            TourPlan servicePlan = new TourPlan();
            if (algorithmPlan != null) {
                servicePlan.setAttractions(algorithmPlan.getAttractions());
                servicePlan.setTotalDistance(algorithmPlan.getTotalDistance());
                servicePlan.setTotalTime(algorithmPlan.getTotalTime());
                servicePlan.setTotalCost(algorithmPlan.getTotalCost());
            }

            return servicePlan;
        }
    }
    public AttractionQueryService getAttractionQueryService() {
        return new AttractionQueryService();
    }

    public PathPlanningService getPathPlanningService() {
        return new PathPlanningService();
    }

    public NearbyRecommendationService getNearbyRecommendationService() {
        return new NearbyRecommendationService();
    }

    public TourPlanningService getTourPlanningService() {
        return new TourPlanningService();
    }

    private static class AttractionDistance {
        private Attraction attraction;
        private double distance;

        public AttractionDistance(Attraction attraction, double distance) {
            this.attraction = attraction;
            this.distance = distance;
        }

        public Attraction getAttraction() { return attraction; }
        public double getDistance() { return distance; }
    }
    public static class TourPlan {
        private List<Attraction> attractions;
        private double totalDistance;
        private double totalCost;
        private int totalTime; // 分钟

        public TourPlan() {
            this.attractions = new ArrayList<>();
        }

        public List<Attraction> getAttractions() { return attractions; }
        public void setAttractions(List<Attraction> attractions) { this.attractions = attractions; }

        public double getTotalDistance() { return totalDistance; }
        public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

        public double getTotalCost() { return totalCost; }
        public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

        public int getTotalTime() { return totalTime; }
        public void setTotalTime(int totalTime) { this.totalTime = totalTime; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("游览计划：\n");
            for (int i = 0; i < attractions.size(); i++) {
                sb.append((i + 1)).append(". ").append(attractions.get(i).getName()).append("\n");
            }
            sb.append(String.format("总距离: %.2f公里\n", totalDistance));
            sb.append(String.format("总费用: %.2f元\n", totalCost));
            sb.append(String.format("总时间: %d分钟\n", totalTime));
            return sb.toString();
        }
    }
} 