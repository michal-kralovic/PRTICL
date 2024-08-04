package com.minkuh.prticl.data.database;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.wrappers.PrticlDataSource;
import com.minkuh.prticl.data.database.functions.PrticlNodeFunctions;
import com.minkuh.prticl.systemutil.configuration.PrticlConfigurationUtil;

import java.sql.SQLException;

public class PrticlDatabase {
    private final PrticlNodeFunctions nodeFunctions;
    private final PrticlDataSource prticlDataSource;

    public PrticlDatabase(Prticl plugin) throws SQLException {
        var prticlDataSourceOpt = new PrticlConfigurationUtil(plugin).getDataSource();

        if (prticlDataSourceOpt.isEmpty()) {
            plugin.getLogger().severe("Couldn't obtain the database configuration!");
            throw new RuntimeException("Couldn't obtain the database configuration!");
        }

        this.prticlDataSource = prticlDataSourceOpt.get();
        this.nodeFunctions = new PrticlNodeFunctions();
    }

    public PrticlNodeFunctions getNodeFunctions() {
        return nodeFunctions;
    }

    public PrticlDataSource getDataSource() {
        return prticlDataSource;
    }
}