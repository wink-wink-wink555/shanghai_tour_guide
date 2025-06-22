# MySQL驱动配置说明

## 说明
这个目录用于存放MySQL JDBC驱动程序。运行程序前需要下载并放置MySQL Connector/J JAR文件。

## 下载步骤

1. 访问MySQL官方下载页面：
   https://dev.mysql.com/downloads/connector/j/

2. 下载 `mysql-connector-java-8.x.x.jar` 文件
   - 选择 Platform Independent 版本
   - 下载ZIP或TAR.GZ格式的文件

3. 解压下载的文件，找到其中的 `mysql-connector-java-x.x.x.jar` 文件

4. 将JAR文件复制到此lib目录下

## 文件结构示例
```
lib/
└── mysql-connector-java-8.0.33.jar
```

## 注意事项
- 建议使用MySQL 8.0.x版本的驱动
- JAR文件名必须以 `mysql-connector-java-` 开头
- 确保下载的是完整的JAR文件，不是源码包

## 验证安装
如果正确安装了MySQL驱动，运行程序时应该能够成功连接到数据库。

如果出现 `ClassNotFoundException` 错误，请检查：
1. JAR文件是否放在正确的lib目录下
2. JAR文件是否完整（大小应该在2-3MB左右）
3. 文件名是否正确 