package com.minkuh.prticl.data.database;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.wrappers.PrticlDataSource;
import com.minkuh.prticl.data.database.functions.PrticlNodeFunctions;
import com.minkuh.prticl.systemutil.configuration.PrticlConfigurationUtil;

import java.sql.SQLException;

public class PrticlDatabase {
    private final PrticlNodeFunctions nodeFunctions;
    private final PrticlDataSource PRTICL_DATA_SOURCE;

    public PrticlDatabase(Prticl plugin) throws SQLException {
        this.PRTICL_DATA_SOURCE = new PrticlConfigurationUtil(plugin).getDataSource();

        this.nodeFunctions = new PrticlNodeFunctions();
    }

    public PrticlNodeFunctions getNodeFunctions() {
        return nodeFunctions;
    }

    public PrticlDataSource getDataSource() {
        return PRTICL_DATA_SOURCE;
    }
}