package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.queries.PrticlLocationQueries;
import org.bukkit.Location;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;

public class PrticlLocationFunctions {
    private final PrticlLocationQueries locationDbScripts;

    public PrticlLocationFunctions(PGSimpleDataSource pgDataSource) {
        this.locationDbScripts = new PrticlLocationQueries(pgDataSource);
    }

    public int createLocation(Location location) throws SQLException {
        return locationDbScripts.createLocation(location);
    }

    public int getLocationId(Location location) throws SQLException {
        return locationDbScripts.getLocationId(location);
    }

    public boolean isLocationInDatabase(Location location) throws SQLException {
        return locationDbScripts.isLocationInDatabase(location);
    }
}