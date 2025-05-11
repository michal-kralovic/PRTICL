package com.minkuh.prticl.data.repositories;

import com.minkuh.prticl.common.PaginatedResult;
import com.minkuh.prticl.data.Query;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.entities.Player;
import com.minkuh.prticl.data.repositories.base.Repository;
import org.apache.commons.lang3.Validate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class NodeRepository extends Repository {
    private final PlayerRepository playerRepository;

    public NodeRepository(Logger logger) {
        super(logger);
        this.playerRepository = new PlayerRepository(logger);
    }

    public Optional<Node> add(Node node) {
        try {
            Optional<Player> persistedPlayer = playerRepository.persistPlayer(node.getPlayer());
            if (persistedPlayer.isEmpty())
                return Optional.empty();

            node.setPlayer(persistedPlayer.get());

            Optional<Integer> generatedId = insertNode(node);

            if (generatedId.isEmpty())
                return Optional.empty();

            node.setId(generatedId.get());

            return Optional.of(node);
        } catch (Exception e) {
            logger.severe("Error persisting node: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean isNodeNameUnique(String name) {
        Validate.notNull(name);

        return new Query("SELECT CASE WHEN COUNT(*) = 0 THEN 1 ELSE 0 END FROM nodes WHERE name = ?")
                .withParam(1, name)
                .toSingle(catchyMapper(rs -> rs.getBoolean(1)))
                .orElseThrow();
    }

    static final String getNodeBase = "SELECT n.id, n.name, n.repeat_delay, n.repeat_count, n.particle_density, " +
            "n.particle_type, n.is_enabled, n.is_spawned, n.world_uuid, n.x, n.y, n.z, " +
            "p.id as player_id, p.uuid, p.username " +
            "FROM nodes n " +
            "JOIN players p ON n.player_id = p.id ";

    public Optional<Node> getById(int id) {
        String sql = getNodeBase + "WHERE n.id = ?";

        return new Query(sql)
                .withParam(id)
                .toSingle(rs -> mapNodeFromResultSet(rs, true));
    }

    public Optional<Node> getByName(String name) {
        String sql = getNodeBase + "WHERE n.name = ?";

        return new Query(sql)
                .withParam(name)
                .toSingle(rs -> mapNodeFromResultSet(rs, true));
    }

    public PaginatedResult<Node> getByPage(int page) {
        return getByPage(page, PaginatedResult.DEFAULT_PAGE_SIZE);
    }

    public PaginatedResult<Node> getByPage(int page, int pageSize) {
        int offset = page * pageSize;
        int totalItems = getTotalCount("nodes");

        List<Node> nodes = new Query("SELECT * FROM nodes ORDER BY id LIMIT ? OFFSET ?")
                .withParam(pageSize)
                .withParam(offset)
                .toList(catchyMapper(rs -> {
                    var node = new Node();
                    node.setId(rs.getInt("id"));
                    node.setName(rs.getString("name"));
                    node.setRepeatDelay(rs.getInt("repeat_delay"));
                    node.setRepeatCount(rs.getInt("repeat_count"));
                    node.setParticleDensity(rs.getInt("particle_density"));
                    node.setParticleType(rs.getString("particle_type"));
                    node.setEnabled(rs.getBoolean("is_enabled"));
                    node.setWorldUUID(UUID.fromString(rs.getString("world_uuid")));
                    node.setX(rs.getDouble("x"));
                    node.setY(rs.getDouble("y"));
                    node.setZ(rs.getDouble("z"));

                    // You might need to set player here as well or fetch it separately
                    return node;
                }))
                .orElse(new ArrayList<>());

        return new PaginatedResult<>(nodes, totalItems, page, pageSize);
    }

    public void setEnabled(Node node, boolean state) {
        new Query("UPDATE nodes SET is_enabled = ? WHERE id = ?")
                .withParam(1, state)
                .withParam(2, node.getId())
                .execute();
    }

    public void setSpawned(Node node, boolean state) {
        new Query("UPDATE nodes SET is_spawned = ? WHERE id = ?")
                .withParam(1, state)
                .withParam(2, node.getId())
                .execute();
    }

    public Optional<List<Node>> getByChunk(int chunkX, int chunkZ, String worldUID) {
        int minX = chunkX * 16;
        int maxX = minX + 15;
        int minZ = chunkZ * 16;
        int maxZ = minZ + 15;

        return new Query("SELECT * FROM nodes n " +
                "WHERE world_uuid = ? " +
                "AND is_enabled = 1 " +
                "AND is_spawned = 0 " +
                "AND repeat_count = 0 " +
                "AND x >= ? AND x <= ? " +
                "AND z >= ? AND z <= ? ")
                .withParam(worldUID)
                .withParam(minX)
                .withParam(maxX)
                .withParam(minZ)
                .withParam(maxZ)
                .toList(rs -> this.mapNodeFromResultSet(rs, false));
    }

    public boolean updateNode(Node node) {
        var updateQuery = "UPDATE nodes SET " +
                "name = ?, " +
                "repeat_delay = ?, " +
                "repeat_count = ?, " +
                "particle_density = ?, " +
                "particle_type = ?, " +
                "is_enabled = ?, " +
                "is_spawned = ?, " +
                "world_uuid = ?, " +
                "x = ?, y = ?, z = ?, " +
                "player_id = ? " +
                "WHERE id = ?";

        var updated = new Query(updateQuery)
                .withParam(node.getName())
                .withParam(node.getRepeatDelay())
                .withParam(node.getRepeatCount())
                .withParam(node.getParticleDensity())
                .withParam(node.getParticleType().name())
                .withParam(node.isEnabled())
                .withParam(node.isSpawned())
                .withParam(node.getWorldUUID().toString())
                .withParam(node.getX())
                .withParam(node.getY())
                .withParam(node.getZ())
                .withParam(node.getPlayer().getId())
                .withParam(node.getId())
                .execute();

        return updated > 0;
    }

    private Optional<Integer> insertNode(Node node) {
        String insertQuery = "INSERT INTO nodes (name, repeat_delay, repeat_count, particle_density, " +
                "particle_type, is_enabled, world_uuid, x, y, z, player_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int rowsAffected = new Query(insertQuery)
                .withParam(1, node.getName())
                .withParam(2, node.getRepeatDelay())
                .withParam(3, node.getRepeatCount())
                .withParam(4, node.getParticleDensity())
                .withParam(5, node.getParticleType().name())
                .withParam(6, node.isEnabled())
                .withParam(7, node.getWorldUUID().toString())
                .withParam(8, node.getX())
                .withParam(9, node.getY())
                .withParam(10, node.getZ())
                .withParam(11, node.getPlayer().getId())
                .execute();

        if (rowsAffected > 0) {
            // Get the generated ID
            String getIdQuery = "SELECT last_insert_rowid() as id";
            return new Query(getIdQuery).toSingle(catchyMapper(rs -> rs.getInt("id")));
        }

        return Optional.empty();
    }

    private Node mapNodeFromResultSet(ResultSet rs, boolean mapPlayer) {
        try {
            var node = new Node();
            node.setId(rs.getInt("id"));
            node.setName(rs.getString("name"));
            node.setRepeatDelay(rs.getInt("repeat_delay"));
            node.setRepeatCount(rs.getInt("repeat_count"));
            node.setParticleDensity(rs.getInt("particle_density"));
            node.setParticleType(rs.getString("particle_type"));
            node.setEnabled(rs.getBoolean("is_enabled"));
            node.setSpawned(rs.getBoolean("is_spawned"));
            node.setWorldUUID(UUID.fromString(rs.getString("world_uuid")));
            node.setX(rs.getDouble("x"));
            node.setY(rs.getDouble("y"));
            node.setZ(rs.getDouble("z"));

            if (mapPlayer) {
                Player player = new Player();
                player.setId(rs.getInt("player_id"));
                player.setUUID(UUID.fromString(rs.getString("uuid")));
                player.setUsername(rs.getString("username"));
                node.setPlayer(player);
            }

            return node;
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping Node from ResultSet", e);
        }
    }
}