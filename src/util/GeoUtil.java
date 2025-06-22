package util;

public class GeoUtil {
    private static final double EARTH_RADIUS = 6371.0; // 公里
    /**
     * 使用Haversine公式计算两点间的球面距离
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 距离（公里）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                  Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    /**
     * 计算方位角（从点1到点2的方向）
     * @param lat1 起点纬度
     * @param lon1 起点经度
     * @param lat2 终点纬度
     * @param lon2 终点经度
     * @return 方位角（度）
     */
    public static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLonRad = Math.toRadians(lon2 - lon1);
        double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - 
                  Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);
        double bearingRad = Math.atan2(y, x);
        double bearingDeg = Math.toDegrees(bearingRad);
        return (bearingDeg + 360) % 360;
    }
    /**
     * 根据方位角获取方向描述
     * @param bearing 方位角（度）
     * @return 方向描述
     */
    public static String getDirectionDescription(double bearing) {
        if (bearing >= 337.5 || bearing < 22.5) {
            return "正北";
        } else if (bearing >= 22.5 && bearing < 67.5) {
            return "东北";
        } else if (bearing >= 67.5 && bearing < 112.5) {
            return "正东";
        } else if (bearing >= 112.5 && bearing < 157.5) {
            return "东南";
        } else if (bearing >= 157.5 && bearing < 202.5) {
            return "正南";
        } else if (bearing >= 202.5 && bearing < 247.5) {
            return "西南";
        } else if (bearing >= 247.5 && bearing < 292.5) {
            return "正西";
        } else {
            return "西北";
        }
    }
} 