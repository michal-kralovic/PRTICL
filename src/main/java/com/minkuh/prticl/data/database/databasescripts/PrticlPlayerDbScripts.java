package com.minkuh.prticl.data.database.databasescripts;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PrticlPlayerDbScripts {
    private final Connection connection;

    public PrticlPlayerDbScripts(Connection connection) {
        this.connection = connection;
    }

    public int getPlayerIdByPlayerUuid(UUID playerUUID) throws SQLException {
        String query = "SELECT id FROM players WHERE uuid = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerUUID.toString());
            return statement.executeQuery().getInt("id");
        }
    }

    public boolean isPlayerInDatabase(Player player) {
        String query = "SELECT 1 FROM players WHERE uuid = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getUniqueId().toString());
            return statement.executeQuery().next();
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean createPlayer(Player player) {
        String query = "INSERT INTO players (uuid, username) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            return statement.executeUpdate() == 1;
        } catch (Exception ex) {
            // TODO: Handle better exception output
            System.out.println(ex);
            return false;
        }
    }
}