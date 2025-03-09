package spigot.plugin;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import spigot.plugin.commands.TimeIsMoneyCommandExecutor;
import spigot.plugin.database.DatabaseManager;
import spigot.plugin.listeners.BlockEventListener;
import spigot.plugin.listeners.PlayerListener;
import spigot.plugin.logger.filter.Log4jFilter;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TimeIsMoney extends JavaPlugin {
    @Getter
    private static TimeIsMoney instance;

    private File customConfigFile;
    @Getter
    private FileConfiguration customConfig;

    @Override
    public void onEnable() {
        instance = this;

        registerLog4jFilter();
        loadCustomConfiguration();
        loadDatabaseFile();

        testDatabase();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockEventListener(), this);

        getCommand("timeismoney").setExecutor(new TimeIsMoneyCommandExecutor());

        getLogger().info("Plugin TimeIsMoney enabled! ");
    }

    @Override
    public void onDisable() {
        DatabaseManager.closeConnection();
        getLogger().info("Plugin TimeIsMoney disabled!");
    }

    private void testDatabase() {
        try (Connection dbConnection = DatabaseManager.getConnection();
             Statement statement = dbConnection.createStatement()) {
            String sqlQueryTest = "CREATE TABLE IF NOT EXISTS users(id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    " username VARCHAR(255) NOT NULL, money INTEGER DEFAULT 0, last_request_date TIMESTAMP)";

            statement.executeUpdate(sqlQueryTest);
            getLogger().info("Test database terminato!");
        } catch (SQLException ex) {
            getLogger().severe("Error on test database! " + ex.getMessage());
        }
    }

    private void loadCustomConfiguration() {
        getLogger().info("Loading configuration file!");
        String configurationFileName = "configuration.yml";
        customConfigFile = new File(getDataFolder(), configurationFileName);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource(configurationFileName, false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Error during loading of configuration file! " + e.getCause());
        }
    }

    private void loadDatabaseFile() {
        getLogger().info("Loading database file!");
        String databaseFileName = "database.mv.db";
        File dbFile = new File(getDataFolder(), databaseFileName);
        if(!dbFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource(databaseFileName, false);
        }
    }

    private void registerLog4jFilter() {
        Log4jFilter.registerFilter();
    }
}
