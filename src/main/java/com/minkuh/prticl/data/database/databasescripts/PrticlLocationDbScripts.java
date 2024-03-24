package com.minkuh.prticl.data.database.databasescripts;

import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrticlLocationDbScripts {
    private final Connection connection;

    public PrticlLocationDbScripts(Connection connection) {
        this.connection = connection;
    }

    public int getLocationId(Location location) throws SQLException {
        String query = "SELECT id FROM locations WHERE x = ? AND y = ? AND z = ? AND world = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setString(4, location.getWorld().toString());

            return statement.executeQuery().getInt("id");
        }
    }

    public boolean isLocationInDatabase(Location location) throws SQLException {
        String query = "SELECT 1 FROM locations WHERE x = ? AND y = ? AND z = ? AND world = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setString(4, location.getWorld().toString());

            return statement.executeQuery().next();
        }
    }

    public boolean createLocation(Location location) {
        try (PreparedStatement statement = connection.prepareStatement(getLocationInsertQuery(true))) {
            statement.setDouble(1, location.x());
            statement.setDouble(2, location.y());
            statement.setDouble(3, location.z());
            statement.setString(4, location.getWorld().toString());

            var result = statement.executeUpdate();
            return result == 1;
        } catch (Exception ex) {
            // TODO: Handle better exception output
            System.out.println(ex);
            return false;
        }
    }

    public boolean createLocation(double x, double y, double z, String world) {
        try (PreparedStatement statement = connection.prepareStatement(getLocationInsertQuery(true))) {
            statement.setDouble(1, x);
            statement.setDouble(2, y);
            statement.setDouble(3, z);
            statement.setString(4, world);

            return statement.executeUpdate() == 1;
        } catch (Exception ex) {
            // TODO: Handle better exception output
            System.out.println(ex);
            return false;
        }
    }

    private String getLocationInsertQuery(boolean isWithWorld) {
        return isWithWorld
                ? "INSERT INTO locations (x, y, z, world) VALUES (?, ?, ?, ?)"
                : "INSERT INTO locations (x, y, z) VALUES (?, ?, ?)";
    }
}
