package com.minkuh.prticl.common;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;

public record PrticlDataSource(String serverName, int port, String database, String user, String password,
                               String schema) {

    public static Optional<PrticlDataSource> getFromConfig(FileConfiguration config) {
        ConfigurationSection dataSourceConfigSection;
        var databaseSection = config.getConfigurationSection("database");

        if (databaseSection == null) {
            return Optional.empty();
        }

        dataSourceConfigSection = databaseSection.getConfigurationSection("data-source");

        String serverName = dataSourceConfigSection.getString("server-name");
        int port = dataSourceConfigSection.getInt("port");
        String database = dataSourceConfigSection.getString("database");
        String user = dataSourceConfigSection.getString("user");
        String password = dataSourceConfigSection.getString("password");
        String schema = dataSourceConfigSection.getString("schema");

        return Optional.of(new PrticlDataSource(serverName, port, database, user, password, schema));
    }

    public String url(boolean useMySQL) {
        return "jdbc:" +
                (useMySQL ? "mysql" : "postgresql")
                + "://" + serverName + ':' + port + '/' + database;
    }
}