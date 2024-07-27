package com.minkuh.prticl.data.database.queries;

import org.bukkit.entity.Player;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PrticlPlayerQueries {
    private final PGSimpleDataSource pgDataSource;

    public PrticlPlayerQueries(PGSimpleDataSource pgDataSource) {
        this.pgDataSource = pgDataSource;
    };

    public int getPlayerIdByPlayerUUID(UUID playerUUID) throws SQLException {
        String query = "SELECT id FROM players WHERE uuid = ? LIMIT 1";

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(query)) {
            statement.setObject(1, playerUUID);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return rs.getInt("id");
                else
                    throw new NoSuchElementException();
            }
        }
    }

    public boolean isPlayerInDatabase(Player player) throws SQLException {
        String query = "SELECT 1 FROM players WHERE uuid = ? LIMIT 1";

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(query)) {
            statement.setObject(1, player.getUniqueId());
            return statement.executeQuery().next();
        }
    }

    public boolean createPlayer(Player player) throws SQLException {
        String query = "INSERT INTO players (uuid, username) VALUES (?, ?)";

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(query)) {
            statement.setObject(1, player.getUniqueId());
            statement.setString(2, player.getName());
            return statement.executeUpdate() == 1;
        }
    }
}