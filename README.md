# 上海市旅游景点导游程序

<div align="center">

![Java](https://img.shields.io/badge/Java-8+-orange.svg)
![MySQL](https://img.shields.io/badge/MySQL-5.7+-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey.svg)

*一个基于Java Swing的智能上海旅游导引系统*

[功能特性](#功能特性) • [快速开始](#快速开始) • [技术架构](#技术架构) • [贡献指南](CONTRIBUTING.md) • [许可证](#许可证)

</div>

## 项目简介

这是一个基于Java Swing开发的上海市旅游景点智能导游系统，采用图论算法和数据库技术，为用户提供景点查询、路径规划、附近推荐和游览规划等服务。系统集成了30个上海著名景点的详细信息和300多条真实路径数据，支持多种交通方式的智能路径规划。

### ✨ 项目亮点
- 🎯 **智能路径规划**: 基于Dijkstra算法的最优路径计算
- 🗺️ **真实数据**: 300+条基于上海实际交通网络的路径数据
- 🔍 **多维查询**: 支持按名称、类型、区域、价格等多维度搜索
- 📍 **精准定位**: 基于Haversine公式的地理距离计算
- 💡 **智能推荐**: 贪心算法优化的游览规划
- 🎨 **友好界面**: 现代化的Java Swing用户界面

**开发者：[wink-wink-wink555](https://github.com/wink-wink-wink555)**

## 功能特性

### 🔍 景点查询
- **多维度搜索**：支持按景点名称、类型、区域、价格范围进行查询
- **详细信息展示**：提供景点的完整信息，包括门票价格、开放时间、地理坐标、推荐游览时长等
- **景点类型分类**：历史文化、现代建筑、自然风光、购物娱乐、博物馆、主题乐园等10个类型

### 🗺️ 智能路径规划
- **Dijkstra最短路径算法**：基于图论的最优路径计算
- **多目标优化**：支持按距离、时间、费用三种方式进行路径优化
- **详细路径信息**：显示逐段路径的交通方式、时间、距离、费用详情
- **真实交通数据**：基于上海实际交通网络的准确数据

### 📍 附近景点推荐
- **Haversine距离计算**：基于地理坐标的精确距离计算
- **自定义搜索半径**：可设定1-50公里的搜索范围
- **方位信息显示**：显示景点相对于当前位置的方向（东北、西南等）
- **距离排序**：按距离远近智能排序推荐

### 📋 智能游览规划
- **贪心算法优化**：基于景点评分和路径成本的智能选择
- **约束条件设置**：支持最大时间和最大距离的约束
- **动态路径计算**：实时调用Dijkstra算法计算相邻景点间最优路径
- **费用统计**：自动计算景点门票费用、交通费用和总花费

### 📖 景点信息浏览
- **完整数据展示**：表格形式展示所有景点的核心信息
- **实时数据刷新**：支持数据更新和重新加载

## 技术架构

### 系统架构设计
```
┌─────────────────┐
│   表现层 (GUI)   │  ← Java Swing界面
├─────────────────┤
│  业务逻辑层      │  ← Service层服务
├─────────────────┤
│   算法计算层     │  ← Dijkstra + 贪心算法
├─────────────────┤
│  数据访问层      │  ← DAO数据访问对象
├─────────────────┤
│   数据库层       │  ← MySQL数据存储
└─────────────────┘
```

### 核心算法实现

#### 1. Dijkstra最短路径算法
- **时间复杂度**: O((V+E)logV)，其中V为景点数，E为路径数
- **优先队列优化**: 使用Java PriorityQueue实现高效的最小值提取
- **多权重支持**: 支持距离、时间、费用三种权重的路径优化
- **路径重构**: 完整记录最优路径的详细信息

```java
// 核心算法逻辑示例
public PathResult findShortestPath(int startId, int endId, String optimizeType) {
    PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
    // 使用优先队列实现Dijkstra算法
    // 根据optimizeType选择不同的权重函数
}
```

#### 2. 贪心游览规划算法
- **评分策略**: 基于 `景点评分 / (路径成本 + 1)` 的贪心选择
- **约束检查**: 实时检查时间和距离约束，避免超出限制
- **动态规划**: 从当前景点动态选择下一个最优景点
- **成本计算**: 结合Dijkstra算法结果进行准确的成本估算

```java
// 贪心算法核心逻辑
double score = (candidateAttraction.getRating().doubleValue()) / (cost + 1);
if (score > maxScore && 满足约束条件) {
    // 选择该景点作为下一个访问点
}
```

#### 3. 地理计算算法
- **Haversine公式**: 计算地球表面两点间的精确距离
- **方位角计算**: 基于经纬度计算相对方向
- **方向描述**: 将角度转换为易懂的方向描述（东北、西南等）

### 数据模型设计

#### 景点信息模型 (Attraction)
```java
public class Attraction {
    private int id;                    // 景点ID
    private String name;               // 景点名称
    private String englishName;        // 英文名称
    private String description;        // 景点简介
    private BigDecimal price;          // 门票价格
    private double latitude;           // 纬度
    private double longitude;          // 经度
    private int recommendDuration;     // 推荐游览时长(分钟)
    private BigDecimal rating;         // 评分(1-5)
    // ... 其他属性
}
```

#### 路径信息模型 (AttractionPath)
```java
public class AttractionPath {
    private int fromAttractionId;      // 起点景点ID
    private int toAttractionId;        // 终点景点ID
    private BigDecimal distanceKm;     // 距离(公里)
    private int travelTimeMinutes;     // 预估时间(分钟)
    private BigDecimal cost;           // 费用(元)
    private TransportType transportType; // 交通工具类型
    // 支持多种权重计算
}
```

### 数据库设计

#### 核心数据表结构
- **attractions**: 30个上海著名景点的详细信息
- **attraction_paths**: 300+条真实路径数据，支持双向查询
- **attraction_types**: 10种景点类型分类
- **transport_types**: 6种交通方式（步行、地铁、公交、出租车、网约车、共享单车）
- **visitor_types**: 8种游客类型的个性化推荐
- **attraction_recommendations**: 基于游客类型的推荐数据

#### 交通方式配置
| ID | 交通方式 | 速度(km/h) | 费用(元/km) |
|----|----------|------------|-------------|
| 1  | 步行     | 5.0        | 0           |
| 2  | 地铁     | 35.0       | 0.5         |
| 3  | 公交车   | 20.0       | 0.3         |
| 4  | 出租车   | 25.0       | 2.5         |
| 5  | 网约车   | 28.0       | 2.0         |
| 6  | 共享单车 | 15.0       | 0.1         |

## 系统环境要求

### 软件环境
- **Java**: JDK 8 或更高版本
- **数据库**: MySQL 5.7 或更高版本
- **操作系统**: Windows/Linux/macOS

### 硬件要求
- **内存**: 最小512MB，推荐1GB
- **存储**: 至少100MB可用空间
- **网络**: 无需网络连接（本地数据库）

## 快速开始

### 📋 环境要求
- **Java**: JDK 8 或更高版本
- **数据库**: MySQL 5.7 或更高版本
- **内存**: 最小512MB，推荐1GB
- **存储**: 至少100MB可用空间

### 🚀 一键运行
```bash
# 克隆项目
git clone https://github.com/wink-wink-wink555/shanghai_tour_guide.git
cd shanghai_tour_guide

# 配置数据库（参见下方详细步骤）
# 下载MySQL驱动到lib目录
# 运行项目
```

## 安装与配置

### 1. 数据库环境配置

#### 安装MySQL并创建数据库
```sql
-- 1. 登录MySQL
mysql -u root -p

-- 2. 创建数据库和表结构
source sql/create_database.sql

-- 3. 导入景点基础数据
source sql/shanghai_tourism_database.sql

-- 4. 导入路径数据
source sql/shanghai_tourism_paths.sql
```

#### 配置数据库连接
修改 `src/config/DatabaseConfig.java`：
```java
private static final String URL = "jdbc:mysql://localhost:3306/shanghai_tourism?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";  // 修改为您的MySQL密码
```

### 2. 依赖库配置

确保 `lib/` 目录下包含以下JAR文件：
- `mysql-connector-java-8.0.x.jar` (MySQL JDBC驱动)

### 3. 编译与运行

#### Windows环境
```batch
# 编译项目
javac -encoding UTF-8 -cp "src;lib/*" src/*.java src/*/*.java

# 运行程序
java -cp "src;lib/*" Main
```

#### Linux/macOS环境
```bash
# 编译项目
javac -encoding UTF-8 -cp "src:lib/*" src/*.java src/*/*.java

# 运行程序
java -cp "src:lib/*" Main
```

#### 使用IDE运行（推荐）
- **IntelliJ IDEA**: 直接打开项目文件夹，配置MySQL驱动后运行Main.java
- **Eclipse**: 导入项目，添加lib目录下的jar文件到构建路径
- **VS Code**: 使用Java扩展包，配置classpath后运行

## 使用指南

### 主界面功能

启动程序后，主界面提供5个核心功能模块：

1. **景点查询** - 查询和搜索上海市的旅游景点
2. **路径规划** - 规划2个景点之间的最优路径  
3. **附近景点** - 查找指定位置附近的景点推荐
4. **游览规划** - 制定多景点游览计划
5. **所有景点** - 浏览所有景点信息

### 详细操作说明

#### 景点查询功能
1. 在搜索框输入景点名称（支持模糊搜索）
2. 选择景点类型或所在区域进行筛选
3. 设置价格范围（0-500元）
4. 点击"搜索"按钮查看结果
5. 双击表格中的景点可查看详细信息

#### 路径规划功能
1. 从下拉菜单选择起点和终点景点
2. 选择优化方式：
   - **距离优先**：最短物理距离
   - **时间优先**：最少travel时间
   - **费用优先**：最低交通费用
3. 点击"规划路径"
4. 查看详细路径表格，包含每段的交通方式、时间、费用

#### 附近推荐功能
1. 输入当前位置的经纬度坐标
2. 或点击"使用当前位置"按钮（需要定位权限）
3. 设置搜索半径（1-50公里）
4. 点击"推荐景点"
5. 查看按距离排序的推荐结果，包含方位信息

#### 游览规划功能
1. 选择起始景点
2. 设置约束条件：
   - **可用时间**：总游览时间（小时）
   - **最大距离**：愿意travel的最大距离（公里）
3. 选择优化目标（距离/时间/费用）
4. 点击"制定游览计划"
5. 查看优化后的游览路线，包含：
   - 景点访问顺序
   - 相邻景点间的交通安排
   - 费用统计（门票+交通费用）

## 项目结构

```
shanghai_tour_guide/
├── src/                          # 源代码目录
│   ├── Main.java                 # 程序主入口
│   ├── algorithm/                # 算法实现
│   │   ├── DijkstraAlgorithm.java     # Dijkstra最短路径算法
│   │   ├── Graph.java                 # 图数据结构
│   │   └── TourPlanningAlgorithm.java # 贪心游览规划算法
│   ├── config/                   # 配置文件
│   │   └── DatabaseConfig.java       # 数据库连接配置
│   ├── dao/                      # 数据访问层
│   │   ├── AttractionDAO.java         # 景点数据访问
│   │   ├── AttractionPathDAO.java     # 路径数据访问
│   │   └── AttractionTypeDAO.java     # 类型数据访问
│   ├── model/                    # 数据模型
│   │   ├── Attraction.java           # 景点实体类
│   │   ├── AttractionPath.java       # 路径实体类
│   │   ├── AttractionType.java       # 景点类型实体类
│   │   └── TransportType.java        # 交通工具实体类
│   ├── service/                  # 业务逻辑层
│   │   └── TourismService.java       # 旅游服务主类
│   ├── util/                     # 工具类
│   │   └── GeoUtil.java              # 地理计算工具
│   └── view/                     # 用户界面
│       ├── MainFrame.java            # 主界面框架
│       ├── AllAttractionsPanel.java  # 景点浏览面板
│       ├── AttractionQueryPanel.java # 景点查询面板
│       ├── AttractionDetailDialog.java # 景点详情对话框
│       ├── PathPlanningPanel.java    # 路径规划面板
│       ├── NearbyRecommendationPanel.java # 附近推荐面板
│       ├── TourPlanningPanel.java    # 游览规划面板
│       ├── GraphDisplayDialog.java   # 图形显示对话框
│       └── GraphVisualPanel.java     # 图形可视化面板
├── sql/                          # 数据库脚本
│   ├── create_database.sql           # 数据库创建脚本
│   ├── shanghai_tourism_database.sql # 基础数据插入
│   └── shanghai_tourism_paths.sql    # 路径数据插入
├── lib/                          # 依赖库目录
│   ├── README.md                     # 依赖库说明
│   └── mysql-connector-java-*.jar   # MySQL JDBC驱动
├── .gitignore                    # Git忽略文件
├── LICENSE                       # MIT许可证
├── README.md                     # 项目说明文档
├── CONTRIBUTING.md               # 贡献指南
├── SECURITY.md                   # 安全政策
├── CHANGELOG.md                  # 变更日志
├── OPEN_SOURCE_CHECKLIST.md     # 开源清单
└── test_data_consistency.md     # 测试文档
```

## 数据说明

### 景点数据
系统包含30个上海著名景点，覆盖：
- **黄浦区**：外滩、南京路步行街、豫园、城隍庙、上海博物馆等
- **浦东新区**：东方明珠、上海中心大厦、金茂大厦、上海科技馆、迪士尼乐园等
- **其他区域**：静安寺、朱家角古镇、七宝古镇、佘山国家森林公园等

### 路径数据
- **数据量**：300+条真实路径记录
- **交通方式**：支持6种交通工具的路径数据
- **数据准确性**：基于上海实际交通网络和真实travel时间
- **双向支持**：大部分景点间支持双向路径查询

## 技术特色

### 算法优化
- **图论算法应用**：使用邻接表存储图结构，优化空间复杂度
- **优先队列优化**：Dijkstra算法采用二叉堆实现，提升时间效率
- **动态权重计算**：支持距离、时间、费用多维度的权重切换
- **贪心策略优化**：游览规划中采用评分/成本比的智能选择策略

### 用户体验
- **直观的GUI设计**：采用Java Swing创建友好的图形界面
- **实时数据反馈**：所有操作都有即时的结果显示
- **详细信息展示**：提供完整的路径规划和费用计算详情
- **错误处理机制**：完善的异常处理和用户提示

### 数据完整性
- **真实地理数据**：基于上海实际景点的精确经纬度坐标
- **准确交通信息**：包含真实的travel时间、距离和费用数据
- **多维度属性**：景点包含价格、评分、开放时间等完整信息

## 故障排除

### 常见问题及解决方案

#### 1. 数据库连接失败
**错误信息**：`数据库连接失败: Communications link failure`

**解决方案**：
- 检查MySQL服务是否启动：`net start mysql` (Windows) 或 `sudo service mysql start` (Linux)
- 验证数据库连接信息是否正确
- 确认防火墙设置允许MySQL连接

#### 2. 找不到MySQL驱动
**错误信息**：`ClassNotFoundException: com.mysql.cj.jdbc.Driver`

**解决方案**：
- 确认 `lib/` 目录下存在 `mysql-connector-java-*.jar` 文件
- 检查classpath设置是否正确

#### 3. 编译错误
**错误信息**：`找不到符号` 或 `程序包不存在`

**解决方案**：
- 确保所有源文件存在且完整
- 检查Java版本是否为JDK 8或更高
- 使用UTF-8编码编译：`javac -encoding UTF-8`

#### 4. 界面显示异常
**解决方案**：
- 确保系统支持Java Swing
- 检查显示器分辨率和DPI设置
- 尝试更新Java版本

## 扩展开发

### 添加新景点
1. 在 `attractions` 表中插入新景点数据
2. 在 `attraction_paths` 表中添加与其他景点的路径信息
3. 系统会自动加载新数据，无需修改代码

### 支持新的交通方式
1. 在 `transport_types` 表中添加新的交通工具记录
2. 在路径数据中使用新的交通工具ID
3. 系统会自动识别和处理新的交通方式

### 算法扩展
- **A*算法**：可替换Dijkstra算法以支持启发式搜索
- **遗传算法**：用于多目标优化的游览规划
- **机器学习推荐**：基于用户行为的个性化推荐算法

## 版本历史

### v1.0.0 (当前版本)
- ✅ 完整的Java Swing GUI界面
- ✅ Dijkstra最短路径算法实现
- ✅ 贪心游览规划算法
- ✅ 30个上海景点数据
- ✅ 300+条真实路径数据
- ✅ 5个核心功能模块
- ✅ 完善的数据库设计
- ✅ 跨平台运行支持

## 🤝 参与贡献

我们欢迎各种形式的贡献！请查看 [贡献指南](CONTRIBUTING.md) 了解如何参与：

- 🐛 [报告Bug](https://github.com/wink-wink-wink555/shanghai_tour_guide/issues)
- 💡 [提出功能建议](https://github.com/wink-wink-wink555/shanghai_tour_guide/issues)
- 🔧 [提交代码](https://github.com/wink-wink-wink555/shanghai_tour_guide/pulls)
- 📚 [改进文档](https://github.com/wink-wink-wink555/shanghai_tour_guide/wiki)

### 🌟 贡献者

感谢所有为项目做出贡献的开发者！

<a href="https://github.com/wink-wink-wink555/shanghai_tour_guide/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=wink-wink-wink555/shanghai_tour_guide" />
</a>

## 📜 许可证

本项目采用 [MIT 许可证](LICENSE)。

## 🆘 支持与反馈

- **🐛 Bug报告**: [GitHub Issues](https://github.com/wink-wink-wink555/shanghai_tour_guide/issues)
- **💬 讨论交流**: [GitHub Discussions](https://github.com/wink-wink-wink555/shanghai_tour_guide/discussions)
- **📧 联系开发者**: 详见下方联系方式
- **⭐ 如果这个项目对您有帮助**: 请给个Star⭐

## 📞 联系方式

- **Email**: [yfsun.jeff@gmail.com](mailto:yfsun.jeff@gmail.com)
- **GitHub**: [wink-wink-wink555](https://github.com/wink-wink-wink555)
- **LinkedIn**: [Yifei Sun](https://www.linkedin.com/in/yifei-sun-0bab66341/)
- **Bilibili**: [NO_Desire](https://space.bilibili.com/623490717)

## 🚀 路线图

### 即将推出的功能
- [ ] 🗺️ 集成在线地图显示
- [ ] 🌐 多语言支持 (英文)
- [ ] 📱 移动端适配
- [ ] 🔄 实时交通数据更新
- [ ] 👥 用户评价系统
- [ ] 🎨 主题切换功能

### 技术改进计划
- [ ] 🚀 性能优化和内存管理
- [ ] 🧪 单元测试覆盖
- [ ] 📊 添加更多算法选项 (A*, Floyd)
- [ ] 🔗 API接口支持

## 🏆 致谢

- 感谢所有对本人数据结构与算法学习有帮助的老师
- 感谢所有测试用户的反馈和建议

---

<div align="center">

**如果这个项目对您有帮助，请给个 ⭐ Star！**

*让更多人发现这个项目*

📌 **重要提示**：运行程序前请确保MySQL服务已启动，并完成数据库的创建和数据导入。

[🔝 回到顶部](#上海市旅游景点导游程序)

</div> 
