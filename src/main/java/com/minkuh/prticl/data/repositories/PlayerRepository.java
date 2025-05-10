package com.minkuh.prticl.data.repositories;

import com.minkuh.prticl.common.PaginatedResult;
import com.minkuh.prticl.data.Query;
import com.minkuh.prticl.data.entities.Player;
import com.minkuh.prticl.data.repositories.base.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerRepository extends Repository {
    public PlayerRepository(Logger logger) {
        super(logger);
    }

    public List<Player> getAllPlayers() {
        return new Query("SELECT * FROM players")
                .toList(catchyMapper(rs -> {
                            var player = new Player();
                            player.setId(rs.getInt("id"));
                            player.setUUID(UUID.fromString(rs.getString("uuid")));
                            player.setUsername(rs.getString("username"));
                            return player;
                        })
                ).orElseThrow();
    }

    public Optional<Player> getById(int playerId) {
        return new Query("SELECT * FROM players WHERE id = ?")
                .withParam(1, playerId)
                .toSingle(catchyMapper(rs -> {
                    var player = new Player();

                    player.setId(rs.getInt("id"));
                    player.setUUID(UUID.fromString(rs.getString("uuid")));
                    player.setUsername(rs.getString("username"));

                    return player;
                }));
    }

    public PaginatedResult<Player> getByPage(int page) {
        return getByPage(page, PaginatedResult.DEFAULT_PAGE_SIZE);
    }

    public PaginatedResult<Player> getByPage(int page, int pageSize) {
        // Convert from 1-based user page to 0-based internal page if needed
        int internalPage = page;

        int offset = internalPage * pageSize;
        int totalItems = getTotalCount("players");

        List<Player> players = new Query("SELECT * FROM players ORDER BY id LIMIT ? OFFSET ?")
                .withParam(1, pageSize)
                .withParam(2, offset)
                .toList(catchyMapper(rs -> {
                    var player = new Player();
                    player.setId(rs.getInt("id"));
                    player.setUUID(UUID.fromString(rs.getString("uuid")));
                    player.setUsername(rs.getString("username"));
                    return player;
                }))
                .orElse(new ArrayList<>());

        return new PaginatedResult<>(players, totalItems, internalPage, pageSize);
    }

    public Optional<Player> persistPlayer(Player player) {
        // Check if player exists by UUID
        var existingId = new Query("SELECT id FROM players WHERE uuid = ?")
                .withParam(1, player.getUUID().toString())
                .toSingle(catchyMapper(rs -> rs.getInt("id")));

        if (existingId.isPresent()) {
            player.setId(existingId.get());
            int updated = new Query("UPDATE players SET username = ? WHERE id = ?")
                    .withParam(1, player.getUsername())
                    .withParam(2, player.getId())
                    .execute();

            if (updated > 0) {
                return Optional.of(player);
            } else {
                logger.warning("Failed to update player with id " + player.getId());
                return Optional.empty();
            }
        } else {
            int rowsAffected = new Query("INSERT INTO players (uuid, username) VALUES (?, ?)")
                    .withParam(1, player.getUUID().toString())
                    .withParam(2, player.getUsername())
                    .execute();

            if (rowsAffected > 0) {
                Optional<Integer> generatedId = new Query("SELECT last_insert_rowid() as id")
                        .toSingle(catchyMapper(rs -> rs.getInt("id")));

                if (generatedId.isPresent()) {
                    player.setId(generatedId.get());
                    return Optional.of(player);
                }
            }

            logger.warning("Failed to insert new player");
            return Optional.empty();
        }
    }
}