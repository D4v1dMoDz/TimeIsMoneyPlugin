package spigot.plugin.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import spigot.plugin.TimeIsMoney;
import java.sql.*;

public class DatabaseManager {
    private static HikariConfig hikariConfig;
    private static HikariDataSource hikariDataSource;

    public static Connection getConnection() throws SQLException {
        if(hikariConfig == null) {
            FileConfiguration customConfig = TimeIsMoney.getInstance().getCustomConfig();
            String url = customConfig.getString("database.url");
            String driverClassName = customConfig.getString("database.driver");
            String user = customConfig.getString("database.user");
            String password = customConfig.getString("database.password");

            hikariConfig = loadHikariConfig(url, driverClassName, user, password);
        }

        if(hikariDataSource == null) {
            hikariDataSource = new HikariDataSource(hikariConfig);
        }

        return hikariDataSource.getConnection();
    }

    public static void closeConnection() {
        if(hikariDataSource != null && hikariDataSource.isRunning()) {
            hikariDataSource.close();
            hikariDataSource = null;
            hikariConfig = null;
            TimeIsMoney.getInstance().getLogger().info("Database connection closed!");
        }
    }

    private static HikariConfig loadHikariConfig(String connectionUrl, String driverClassName, String username, String password) {
        var config = new HikariConfig();
        config.setJdbcUrl(connectionUrl);
        config.setDriverClassName(driverClassName);
        config.setUsername(username);
        config.setPassword(password);

        return config;
    }
}
