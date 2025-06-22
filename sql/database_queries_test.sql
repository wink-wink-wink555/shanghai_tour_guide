-- 上海旅游景点数据库查询测试脚本
USE shanghai_tourism;

-- 1. 查看数据统计
SELECT '景点总数' AS 统计项, COUNT(*) AS 数量 FROM attractions
UNION ALL
SELECT '路径总数', COUNT(*) FROM attraction_paths
UNION ALL
SELECT '景点类型数', COUNT(*) FROM attraction_types
UNION ALL
SELECT '交通工具种类', COUNT(*) FROM transport_types
UNION ALL
SELECT '游客类型数', COUNT(*) FROM visitor_types;

-- 2. 查看所有景点基本信息
SELECT 
    id,
    name AS 景点名称,
    district AS 所在区域,
    price AS 门票价格,
    rating AS 评分,
    CONCAT(latitude, ',', longitude) AS 经纬度坐标
FROM attractions
ORDER BY district, rating DESC;

-- 3. 按区域统计景点分布
SELECT 
    district AS 区域,
    COUNT(*) AS 景点数量,
    AVG(price) AS 平均票价,
    AVG(rating) AS 平均评分
FROM attractions
GROUP BY district
ORDER BY 景点数量 DESC;

-- 4. 查看免费景点
SELECT 
    name AS 景点名称,
    district AS 区域,
    description AS 描述,
    rating AS 评分
FROM attractions
WHERE price = 0
ORDER BY rating DESC;

-- 5. 查看高评分景点（4.5分以上）
SELECT 
    name AS 景点名称,
    district AS 区域,
    price AS 票价,
    rating AS 评分,
    opening_hours AS 开放时间
FROM attractions
WHERE rating >= 4.5
ORDER BY rating DESC;

-- 6. 查看景点类型分布
SELECT 
    at.type_name AS 景点类型,
    COUNT(*) AS 数量,
    AVG(a.price) AS 平均票价,
    AVG(a.rating) AS 平均评分
FROM attractions a
JOIN attraction_types at ON a.type_id = at.id
GROUP BY at.type_name
ORDER BY 数量 DESC;

-- 7. 查找某个景点的所有可达路径（以外滩为例）
SELECT 
    from_attraction_name AS 起点,
    to_attraction_name AS 终点,
    transport_type AS 交通方式,
    distance_km AS 距离_公里,
    travel_time_minutes AS 时间_分钟,
    cost AS 费用_元,
    description AS 路径描述
FROM attraction_graph
WHERE from_attraction_name = '外滩'
ORDER BY distance_km;

-- 8. 查找两点间的最短距离路径（外滩到东方明珠）
SELECT 
    from_attraction_name AS 起点,
    to_attraction_name AS 终点,
    transport_type AS 交通方式,
    distance_km AS 距离_公里,
    travel_time_minutes AS 时间_分钟,
    cost AS 费用_元
FROM attraction_graph
WHERE from_attraction_name = '外滩' 
  AND to_attraction_name = '东方明珠'
ORDER BY distance_km LIMIT 1;

-- 9. 查找两点间的最低费用路径
SELECT 
    from_attraction_name AS 起点,
    to_attraction_name AS 终点,
    transport_type AS 交通方式,
    distance_km AS 距离_公里,
    travel_time_minutes AS 时间_分钟,
    cost AS 费用_元
FROM attraction_graph
WHERE from_attraction_name = '外滩' 
  AND to_attraction_name = '东方明珠'
ORDER BY cost LIMIT 1;

-- 10. 查找两点间的最快路径
SELECT 
    from_attraction_name AS 起点,
    to_attraction_name AS 终点,
    transport_type AS 交通方式,
    distance_km AS 距离_公里,
    travel_time_minutes AS 时间_分钟,
    cost AS 费用_元
FROM attraction_graph
WHERE from_attraction_name = '外滩' 
  AND to_attraction_name = '东方明珠'
ORDER BY travel_time_minutes LIMIT 1;

-- 11. 查看黄浦区内的景点连接图
SELECT 
    ag.from_attraction_name AS 起点,
    ag.to_attraction_name AS 终点,
    ag.distance_km AS 距离,
    ag.transport_type AS 交通方式
FROM attraction_graph ag
JOIN attractions a1 ON ag.from_attraction_name = a1.name
JOIN attractions a2 ON ag.to_attraction_name = a2.name
WHERE a1.district = '黄浦区' AND a2.district = '黄浦区'
ORDER BY ag.distance_km;

-- 12. 按游客类型查看推荐景点
SELECT 
    visitor_type AS 游客类型,
    attraction_name AS 推荐景点,
    recommendation_score AS 推荐指数,
    reason AS 推荐理由,
    price AS 门票价格,
    district AS 所在区域
FROM personalized_recommendations
WHERE visitor_type = '年轻情侣'
ORDER BY recommendation_score DESC;

-- 13. 查找距离某个坐标最近的景点（计算直线距离）
-- 示例：查找距离人民广场最近的5个景点
SELECT 
    name AS 景点名称,
    district AS 区域,
    ROUND(
        6371 * acos(
            cos(radians(31.22773)) * cos(radians(latitude)) * 
            cos(radians(longitude) - radians(121.47611)) + 
            sin(radians(31.22773)) * sin(radians(latitude))
        ), 2
    ) AS 直线距离_公里
FROM attractions
WHERE name != '人民广场'
ORDER BY 直线距离_公里
LIMIT 5;

-- 14. 统计各种交通工具的使用情况
SELECT 
    tt.name AS 交通工具,
    COUNT(*) AS 路径数量,
    AVG(ap.distance_km) AS 平均距离,
    AVG(ap.travel_time_minutes) AS 平均时间,
    AVG(ap.cost) AS 平均费用
FROM attraction_paths ap
JOIN transport_types tt ON ap.transport_type_id = tt.id
GROUP BY tt.name
ORDER BY 路径数量 DESC;

-- 15. 查找可以一日游的景点组合（基于地理位置邻近）
SELECT DISTINCT
    a1.name AS 景点1,
    a2.name AS 景点2,
    a1.district AS 区域,
    ROUND(ag.distance_km, 1) AS 距离_公里,
    ag.travel_time_minutes AS 时间_分钟,
    (a1.recommend_duration + a2.recommend_duration + ag.travel_time_minutes) AS 总时间_分钟
FROM attraction_graph ag
JOIN attractions a1 ON ag.from_attraction_name = a1.name
JOIN attractions a2 ON ag.to_attraction_name = a2.name
WHERE ag.distance_km <= 2.0  -- 距离在2公里内
  AND ag.travel_time_minutes <= 30  -- 交通时间30分钟内
  AND a1.district = a2.district  -- 同一区域
  AND (a1.recommend_duration + a2.recommend_duration + ag.travel_time_minutes) <= 480  -- 总时间8小时内
ORDER BY 总时间_分钟;

-- 16. 验证图的连通性（检查是否有孤立节点）
SELECT 
    a.name AS 景点名称,
    COUNT(ap.from_attraction_id) AS 出边数量
FROM attractions a
LEFT JOIN attraction_paths ap ON a.id = ap.from_attraction_id
GROUP BY a.id, a.name
HAVING COUNT(ap.from_attraction_id) = 0;

-- 17. 查找最受欢迎的景点（基于推荐次数）
SELECT 
    a.name AS 景点名称,
    COUNT(ar.attraction_id) AS 推荐次数,
    AVG(ar.recommendation_score) AS 平均推荐分,
    a.rating AS 用户评分,
    a.price AS 门票价格
FROM attractions a
LEFT JOIN attraction_recommendations ar ON a.id = ar.attraction_id
GROUP BY a.id, a.name, a.rating, a.price
ORDER BY 推荐次数 DESC, 平均推荐分 DESC;

-- 18. 预算友好的景点推荐（100元以内）
SELECT 
    name AS 景点名称,
    price AS 门票价格,
    rating AS 评分,
    district AS 区域,
    recommend_duration AS 推荐游览时长_分钟
FROM attractions
WHERE price <= 100
ORDER BY rating DESC, price ASC;

-- 19. 查看包含多种交通方式的路径
SELECT 
    from_attraction_name AS 起点,
    to_attraction_name AS 终点,
    GROUP_CONCAT(
        CONCAT(transport_type, '(', distance_km, 'km, ', cost, '元)')
        ORDER BY cost
        SEPARATOR ' | '
    ) AS 交通选择
FROM attraction_graph
GROUP BY from_attraction_name, to_attraction_name
HAVING COUNT(*) > 1
ORDER BY from_attraction_name, to_attraction_name;

-- 20. 生成景点访问热度报告
SELECT 
    a.name AS 景点名称,
    a.district AS 区域,
    a.max_capacity AS 最大容量,
    a.rating AS 评分,
    CASE 
        WHEN a.price = 0 THEN '免费'
        WHEN a.price <= 50 THEN '经济'
        WHEN a.price <= 150 THEN '中等'
        ELSE '高端'
    END AS 价格档次,
    (
        SELECT COUNT(*) 
        FROM attraction_paths ap 
        WHERE ap.to_attraction_id = a.id
    ) AS 可达路径数
FROM attractions a
ORDER BY 可达路径数 DESC, a.rating DESC; 