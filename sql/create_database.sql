-- 上海市旅游景点导游系统数据库设计
-- 创建数据库
CREATE DATABASE IF NOT EXISTS shanghai_tourism CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shanghai_tourism;

-- 1. 景点类型表
CREATE TABLE attraction_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL COMMENT '景点类型名称',
    description VARCHAR(200) COMMENT '类型描述'
);

-- 2. 景点信息表
CREATE TABLE attractions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '景点名称',
    english_name VARCHAR(100) COMMENT '英文名称',
    description TEXT COMMENT '景点简介',
    detailed_info TEXT COMMENT '详细介绍',
    price DECIMAL(10,2) DEFAULT 0 COMMENT '门票价格(元)',
    opening_hours VARCHAR(100) COMMENT '开放时间',
    max_capacity INT DEFAULT 10000 COMMENT '最大人流量',
    latitude DECIMAL(10,8) NOT NULL COMMENT '纬度',
    longitude DECIMAL(11,8) NOT NULL COMMENT '经度',
    type_id INT COMMENT '景点类型ID',
    district VARCHAR(50) COMMENT '所在区域',
    address VARCHAR(200) COMMENT '详细地址',
    phone VARCHAR(20) COMMENT '联系电话',
    website VARCHAR(200) COMMENT '官方网站',
    recommend_duration INT DEFAULT 120 COMMENT '推荐游览时长(分钟)',
    rating DECIMAL(3,1) DEFAULT 4.5 COMMENT '评分(1-5)',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES attraction_types(id),
    INDEX idx_location (latitude, longitude),
    INDEX idx_type (type_id),
    INDEX idx_district (district)
);

-- 3. 交通工具表
CREATE TABLE transport_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '交通工具名称',
    speed_kmh DECIMAL(5,2) COMMENT '平均速度(公里/小时)',
    cost_per_km DECIMAL(8,2) COMMENT '每公里费用(元)'
);

-- 4. 景点间路径表
CREATE TABLE attraction_paths (
    id INT PRIMARY KEY AUTO_INCREMENT,
    from_attraction_id INT NOT NULL COMMENT '起点景点ID',
    to_attraction_id INT NOT NULL COMMENT '终点景点ID',
    distance_km DECIMAL(8,2) NOT NULL COMMENT '距离(公里)',
    transport_type_id INT NOT NULL COMMENT '交通工具ID',
    travel_time_minutes INT COMMENT '预估时间(分钟)',
    cost DECIMAL(10,2) COMMENT '费用(元)',
    description VARCHAR(200) COMMENT '路径描述',
    FOREIGN KEY (from_attraction_id) REFERENCES attractions(id),
    FOREIGN KEY (to_attraction_id) REFERENCES attractions(id),
    FOREIGN KEY (transport_type_id) REFERENCES transport_types(id),
    INDEX idx_from_to (from_attraction_id, to_attraction_id),
    INDEX idx_transport (transport_type_id)
);

-- 5. 游客类型表
CREATE TABLE visitor_types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL COMMENT '游客类型',
    description VARCHAR(200) COMMENT '类型描述'
);

-- 6. 景点推荐表(根据游客类型)
CREATE TABLE attraction_recommendations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    attraction_id INT NOT NULL,
    visitor_type_id INT NOT NULL,
    recommendation_score INT DEFAULT 5 COMMENT '推荐指数(1-10)',
    reason VARCHAR(200) COMMENT '推荐理由',
    FOREIGN KEY (attraction_id) REFERENCES attractions(id),
    FOREIGN KEY (visitor_type_id) REFERENCES visitor_types(id)
); 