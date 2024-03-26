package com.minkuh.prticl.data.database;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.databasescripts.PrticlNodeDbScripts;
import com.minkuh.prticl.data.database.databasescripts.PrticlPlayerDbScripts;
import com.minkuh.prticl.data.database.functions.PrticlLocationFunctions;
import com.minkuh.prticl.data.database.functions.PrticlNodeFunctions;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PrticlDatabase {
    private final PrticlPlayerDbScripts playerScripts;
    private final PrticlNodeDbScripts nodeScripts;
    private final PrticlNodeFunctions nodeFunctions;
    private final PrticlLocationFunctions locationFunctions;

    public PrticlDatabase(Prticl plugin) throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "prticl.db");
        this.playerScripts = new PrticlPlayerDbScripts(connection);
        this.nodeScripts = new PrticlNodeDbScripts(connection);
        this.nodeFunctions = new PrticlNodeFunctions(connection);
        this.locationFunctions = new PrticlLocationFunctions(connection);

        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();
    }

    @Deprecated
    public PrticlPlayerDbScripts getPlayerScripts() {
        return playerScripts;
    }

    @Deprecated
    public PrticlNodeDbScripts getNodeScripts() {
        return nodeScripts;
    }

    public PrticlNodeFunctions getNodeFunctions() {
        return nodeFunctions;
    }

    public PrticlLocationFunctions getLocationFunctions() {
        return locationFunctions;
    }
}