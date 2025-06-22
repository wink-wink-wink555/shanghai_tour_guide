package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/shanghai_tourism?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Connection connection = null;
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动加载失败: " + e.getMessage());
        }
    }
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("数据库连接成功！");
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
} 