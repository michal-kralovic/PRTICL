package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.databasescripts.PrticlLocationDbScripts;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.SQLException;

public class PrticlLocationFunctions {
    private final PrticlLocationDbScripts locationDbScripts;

    public PrticlLocationFunctions(Connection connection) {
        this.locationDbScripts = new PrticlLocationDbScripts(connection);
    }

    public boolean createLocation(Location location) {
        return locationDbScripts.createLocation(location);
    }

    public int getLocationId(Location location) throws SQLException {
        return locationDbScripts.getLocationId(location);
    }

    public boolean isLocationInDatabase(Location location) throws SQLException {
        return locationDbScripts.isLocationInDatabase(location);
    }
}
