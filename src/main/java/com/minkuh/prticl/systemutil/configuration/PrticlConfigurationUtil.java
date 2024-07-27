package com.minkuh.prticl.systemutil.configuration;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.wrappers.PrticlDataSource;

public class PrticlConfigurationUtil {
    private final Prticl plugin;

    public PrticlConfigurationUtil(Prticl plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
    }

    public PrticlDataSource getDataSource() {
        var dataSourceConfigSection = plugin.getConfig().getConfigurationSection("data-source");

        String serverName = dataSourceConfigSection.getString("server-name");
        int port = dataSourceConfigSection.getInt("port");
        String database = dataSourceConfigSection.getString("database");
        String user = dataSourceConfigSection.getString("user");
        String password = dataSourceConfigSection.getString("password");
        String schema = dataSourceConfigSection.getString("schema");

        return new PrticlDataSource(serverName, port, database, user, password, schema);
    }
}