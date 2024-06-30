package com.minkuh.prticl.data.database;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.functions.PrticlLocationFunctions;
import com.minkuh.prticl.data.database.functions.PrticlNodeFunctions;
import com.minkuh.prticl.data.wrappers.PrticlDataSource;
import com.minkuh.prticl.systemutil.configuration.PrticlConfigurationUtil;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;

public class PrticlDatabase {
    private final PrticlNodeFunctions nodeFunctions;
    private final PrticlLocationFunctions locationFunctions;

    public PrticlDatabase(Prticl plugin) throws SQLException {
        PrticlConfigurationUtil configUtil = new PrticlConfigurationUtil(plugin);
        PrticlDataSource prticlDataSource = configUtil.getDataSource();
        PGSimpleDataSource pgDataSource = new PGSimpleDataSource();

        pgDataSource.setServerNames(new String[]{prticlDataSource.serverName()});
        pgDataSource.setPortNumbers(new int[]{prticlDataSource.port()});
        pgDataSource.setDatabaseName(prticlDataSource.database());
        pgDataSource.setUser(prticlDataSource.user());
        pgDataSource.setPassword(prticlDataSource.password());
        pgDataSource.setCurrentSchema(prticlDataSource.schema());

        this.nodeFunctions = new PrticlNodeFunctions(pgDataSource);
        this.locationFunctions = new PrticlLocationFunctions(pgDataSource);

        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();
    }

    public PrticlNodeFunctions getNodeFunctions() {
        return nodeFunctions;
    }

    public PrticlLocationFunctions getLocationFunctions() {
        return locationFunctions;
    }
}