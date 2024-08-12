package com.minkuh.prticl.data.database;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.systemutil.configuration.PrticlConfigurationUtil;
import com.minkuh.prticl.common.wrappers.PrticlDataSource;
import com.minkuh.prticl.data.database.functions.PrticlNodeFunctions;
import com.minkuh.prticl.data.database.functions.PrticlTriggerFunctions;

public class PrticlDatabase {
    private final PrticlDataSource prticlDataSource;

    private final PrticlNodeFunctions nodeFunctions;
    private final PrticlTriggerFunctions triggerFunctions;

    public PrticlDatabase(Prticl plugin) {
        var prticlDataSourceOpt = new PrticlConfigurationUtil(plugin).getDataSource();

        if (prticlDataSourceOpt.isEmpty()) {
            plugin.getLogger().severe("Couldn't obtain the database configuration!");
            throw new RuntimeException("Couldn't obtain the database configuration!");
        }

        this.prticlDataSource = prticlDataSourceOpt.get();
        this.nodeFunctions = new PrticlNodeFunctions();
        this.triggerFunctions = new PrticlTriggerFunctions();
    }

    public PrticlDataSource getDataSource() {
        return prticlDataSource;
    }

    public PrticlNodeFunctions getNodeFunctions() {
        return nodeFunctions;
    }

    public PrticlTriggerFunctions getTriggerFunctions() {
        return triggerFunctions;
    }
}