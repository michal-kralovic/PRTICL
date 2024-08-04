package com.minkuh.prticl.systemutil.configuration;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.wrappers.PrticlDataSource;
import org.bukkit.configuration.file.FileConfiguration;

public class PrticlConfigurationUtil {
    private final FileConfiguration config;

    public PrticlConfigurationUtil(Prticl plugin) {
        this.config = plugin.getConfig();
    }

    public boolean usingMySql() {
        return config.getConfigurationSection("database").getBoolean("use-mysql");
    }

    public PrticlDataSource getDataSource() {
        return PrticlDataSource.getFromConfig(config);
    }
}