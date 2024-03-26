package com.minkuh.prticl.data.database.databasescripts;

import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class PrticlLocationDbScripts {
    private final Connection connection;

    public PrticlLocationDbScripts(Connection connection) {
        this.connection = connection;
    }

    private static final String GET_LOCATION_ID_QUERY = "SELECT id FROM locations WHERE x = ? AND y = ? AND z = ? AND world = ?";
    private static final String IS_LOCATION_IN_DATABASE_QUERY = "SELECT 1 FROM locations WHERE x = ? AND y = ? AND z = ? AND world = ? LIMIT 1";
    private static final String CREATE_LOCATION_QUERY = "INSERT INTO locations (x, y, z, world) VALUES (?, ?, ?, ?)";


    public int getLocationId(Location location) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_LOCATION_ID_QUERY)) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setString(4, location.getWorld().toString());

            return statement.executeQuery().getInt(1);
        }
    }


    public boolean isLocationInDatabase(Location location) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(IS_LOCATION_IN_DATABASE_QUERY)) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setString(4, location.getWorld().toString());

            return statement.executeQuery().next();
        }
    }

    public int createLocation(Location location) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_LOCATION_QUERY)) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setString(4, location.getWorld().getName());

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);

            throw new NoSuchElementException("Column ID not returned!");
        }
    }

    public boolean createLocation(double x, double y, double z, String world) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_LOCATION_QUERY)) {
            statement.setDouble(1, x);
            statement.setDouble(2, y);
            statement.setDouble(3, z);
            statement.setString(4, world);

            return statement.executeUpdate() == 1;
        }
    }
}