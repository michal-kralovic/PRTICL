package com.minkuh.prticl.data.database.queries;

import com.minkuh.prticl.data.database.PrticlDbConstants;
import org.bukkit.Location;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class PrticlLocationQueries {
    private final PGSimpleDataSource pgDataSource;

    public PrticlLocationQueries(PGSimpleDataSource pgDataSource) {
        this.pgDataSource = pgDataSource;
    }

    private static final String GET_LOCATION_ID_QUERY =
            "SELECT %s FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ?".formatted(
                    PrticlDbConstants.PLAYER_ID,
                    PrticlDbConstants.LOCATIONS_TABLE,
                    PrticlDbConstants.LOCATION_X,
                    PrticlDbConstants.LOCATION_Y,
                    PrticlDbConstants.LOCATION_Z,
                    PrticlDbConstants.LOCATION_WORLD_ID);

    public int getLocationId(Location location) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(GET_LOCATION_ID_QUERY)) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setString(4, location.getWorld().toString());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
                else throw new NoSuchElementException();
            }
        }
    }

    private static final String IS_LOCATION_IN_DATABASE_QUERY = "SELECT 1 FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? LIMIT 1".formatted(
            PrticlDbConstants.LOCATIONS_TABLE,
            PrticlDbConstants.LOCATION_X,
            PrticlDbConstants.LOCATION_Y,
            PrticlDbConstants.LOCATION_Z,
            PrticlDbConstants.LOCATION_WORLD_ID
    );

    public boolean isLocationInDatabase(Location location) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(IS_LOCATION_IN_DATABASE_QUERY)) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setString(4, location.getWorld().toString());

            return statement.executeQuery().next();
        }
    }

    private static final String CREATE_LOCATION_QUERY = "INSERT INTO locations (x, y, z, world_id, world_name) VALUES (?, ?, ?, ?, ?)";

    public int createLocation(Location location) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(CREATE_LOCATION_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setObject(4, location.getWorld().getUID());
            statement.setString(5, location.getWorld().getName());

            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getInt(1);
                else throw new NoSuchElementException("Column ID not returned!");
            }
        }
    }
}