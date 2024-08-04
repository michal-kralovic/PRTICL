package com.minkuh.prticl.common.wrappers;

import org.bukkit.configuration.file.FileConfiguration;

public record PrticlDataSource(String serverName, int port, String database, String user, String password,
                               String schema) {

    public static PrticlDataSource getFromConfig(FileConfiguration config) {
        var dataSourceConfigSection = config.getConfigurationSection("database").getConfigurationSection("data-source");

        String serverName = dataSourceConfigSection.getString("server-name");
        int port = dataSourceConfigSection.getInt("port");
        String database = dataSourceConfigSection.getString("database");
        String user = dataSourceConfigSection.getString("user");
        String password = dataSourceConfigSection.getString("password");
        String schema = dataSourceConfigSection.getString("schema");

        return new PrticlDataSource(serverName, port, database, user, password, schema);
    }

    public String url(boolean useMySQL) {
        return "jdbc:" +
                (useMySQL ? "mysql" : "postgresql")
                + "://" + serverName + ':' + port + '/' + database;
    }
}