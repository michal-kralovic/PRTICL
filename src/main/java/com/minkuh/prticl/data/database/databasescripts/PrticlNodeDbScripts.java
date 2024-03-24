package com.minkuh.prticl.data.database.databasescripts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrticlNodeDbScripts {
    private final Connection connection;

    public PrticlNodeDbScripts(Connection connection) {
        this.connection = connection;
    }

    public boolean createNode(String name, int repeatDelay, int particleDensity, String particleType, int locationId, int playerId) {
        String query = "INSERT INTO nodes (name, repeat_delay, particle_density, particle_type, location_id, player_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, repeatDelay);
            statement.setInt(3, particleDensity);
            statement.setString(4, particleType);
            statement.setInt(5, locationId);
            statement.setInt(6, playerId);
            return statement.executeUpdate() == 1;
        } catch (Exception ex) {
            // TODO: Handle better exception output
            System.out.println(ex);
            return false;
        }
    }
}
