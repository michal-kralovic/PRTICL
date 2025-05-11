package com.minkuh.prticl.data.repositories;

import com.minkuh.prticl.common.PaginatedResult;
import com.minkuh.prticl.data.Query;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.entities.Player;
import com.minkuh.prticl.data.entities.Trigger;
import com.minkuh.prticl.data.repositories.base.Repository;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TriggerRepository extends Repository {
    private final PlayerRepository playerRepository;

    public TriggerRepository(Logger logger) {
        super(logger);
        playerRepository = new PlayerRepository(logger);
    }

    public Optional<Trigger> getById(int triggerId, boolean loadNodes) {
        var playerId = new AtomicInteger();

        var triggerOpt = new Query("SELECT * FROM triggers WHERE id = ?")
                .withParam(1, triggerId)
                .toSingle(catchyMapper(rs -> {
                    var trigger = new Trigger();

                    trigger.setId(rs.getInt("id"));
                    trigger.setName(rs.getString("name"));
                    trigger.setX(rs.getDouble("x"));
                    trigger.setY(rs.getDouble("y"));
                    trigger.setZ(rs.getDouble("z"));
                    trigger.setBlockName(rs.getString("block_name"));
                    trigger.setWorldUUID(UUID.fromString(rs.getString("world_uuid")));

                    playerId.set(rs.getInt("player_id"));

                    return trigger;
                }));

        if (triggerOpt.isEmpty())
            return triggerOpt;

        var trigger = triggerOpt.get();
        trigger.setPlayer(playerRepository.getById(playerId.get()).orElseThrow());

        if (loadNodes)
            trigger.setNodes(getNodesByTriggerId(triggerId));

        return triggerOpt;
    }

    public Optional<Trigger> getByName(String triggerName, boolean loadNodes) {
        var triggerId = new Query("SELECT id FROM triggers WHERE name = ?")
                .withParam(1, triggerName)
                .toSingle(catchyMapper(rs -> rs.getInt("id")));

        Validate.isTrue(triggerId.isPresent());

        return getById(triggerId.get(), loadNodes);
    }

    public Set<Node> getNodesByTriggerId(int triggerId) {
        var players = playerRepository.getAllPlayers();
        return new HashSet<>(
                new Query("""
                        SELECT n.* FROM nodes n
                        JOIN node_trigger nt ON n.id = nt.node_id
                        WHERE nt.trigger_id = ?
                        """)
                        .withParam(1, triggerId)
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
                            var nodePlayerId = rs.getInt("player_id");
                            node.setPlayer(players.stream().filter(p -> p.getId() == nodePlayerId).findFirst().orElseThrow());

                            return node;
                        }))
                        .orElse(Collections.emptyList()));
    }

    public Optional<Trigger> getByLocation(@NotNull Location location, boolean loadNodes) {
        var playerId = new AtomicInteger();
        var x = location.x();
        var y = location.y();
        var z = location.z();

        var triggerOpt = new Query("SELECT * FROM triggers t WHERE t.x = ? AND t.y = ? AND t.z = ?")
                .withParam(1, x)
                .withParam(2, y)
                .withParam(3, z)
                .toSingle(catchyMapper(rs -> {
                    var trigger = new Trigger();

                    trigger.setId(rs.getInt("id"));
                    trigger.setName(rs.getString("name"));
                    trigger.setX(rs.getDouble("x"));
                    trigger.setY(rs.getDouble("y"));
                    trigger.setZ(rs.getDouble("z"));
                    trigger.setBlockName(rs.getString("block_name"));
                    trigger.setWorldUUID(UUID.fromString(rs.getString("world_uuid")));

                    playerId.set(rs.getInt("player_id"));

                    return trigger;
                }));

        if (triggerOpt.isEmpty()) {
            return triggerOpt;
        }

        var trigger = triggerOpt.get();
        trigger.setPlayer(playerRepository.getById(playerId.get()).orElseThrow());

        if (loadNodes) {
            var nodes = getNodesByTriggerId(trigger.getId());
            trigger.setNodes(nodes);
        }

        return triggerOpt;
    }

    public PaginatedResult<Trigger> getByPage(int page) {
        return getByPage(page, PaginatedResult.DEFAULT_PAGE_SIZE);
    }

    public PaginatedResult<Trigger> getByPage(int page, int pageSize) {
        int offset = page * pageSize;
        int totalItems = getTotalCount("triggers");

        List<Trigger> triggers = new Query("SELECT * FROM triggers ORDER BY id LIMIT ? OFFSET ?")
                .withParam(1, pageSize)
                .withParam(2, offset)
                .toList(catchyMapper(rs -> {
                    var trigger = new Trigger();
                    trigger.setId(rs.getInt("id"));
                    trigger.setName(rs.getString("name"));
                    trigger.setX(rs.getDouble("x"));
                    trigger.setY(rs.getDouble("y"));
                    trigger.setZ(rs.getDouble("z"));
                    trigger.setBlockName(rs.getString("block_name"));
                    trigger.setWorldUUID(UUID.fromString(rs.getString("world_uuid")));

                    // You might need to set player here as well or fetch it separately
                    return trigger;
                }))
                .orElse(new ArrayList<>());

        return new PaginatedResult<>(triggers, totalItems, page, pageSize);
    }

    public Optional<Trigger> add(Trigger trigger) {
        try {
            Optional<Player> persistedPlayer = playerRepository.persistPlayer(trigger.getPlayer());
            if (persistedPlayer.isEmpty())
                return Optional.empty();

            trigger.setPlayer(persistedPlayer.get());

            Optional<Integer> generatedId = insertTrigger(trigger);

            if (generatedId.isEmpty())
                return Optional.empty();

            trigger.setId(generatedId.get());

            return Optional.of(trigger);
        } catch (Exception e) {
            logger.severe("Error persisting node: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean addNodeTriggerLink(int nodeId, int triggerId) {
        int rowsAffected = new Query("INSERT INTO node_trigger (node_id, trigger_id) VALUES (?, ?)")
                .withParam(1, nodeId)
                .withParam(2, triggerId)
                .execute();

        return rowsAffected > 0;
    }

    public void removeNodeTriggerLink(int nodeId, int triggerId) {
        new Query("DELETE FROM node_trigger WHERE node_id = ? AND trigger_id = ?")
                .withParam(nodeId)
                .withParam(triggerId)
                .execute();
    }

    public Optional<Boolean> doesLinkAlreadyExist(int nodeId, int triggerId) {
        return new Query("""
                SELECT COUNT(*)
                FROM node_trigger
                WHERE node_id = ? AND trigger_id = ?
                """)
                .withParam(1, nodeId)
                .withParam(2, triggerId)
                .toSingle(catchyMapper(rs -> rs.getInt(1) > 0));
    }

    public boolean isTriggerNameUnique(String name) {
        Validate.notNull(name);

        return new Query("SELECT CASE WHEN COUNT(*) = 0 THEN 1 ELSE 0 END FROM triggers WHERE name = ?")
                .withParam(1, name)
                .toSingle(catchyMapper(rs -> rs.getBoolean(1)))
                .orElseThrow();
    }

    private Optional<Integer> insertTrigger(Trigger trigger) {
        String query = "INSERT INTO triggers (name, x, y, z, block_name, world_uuid, player_id) VALUES (?, ?, ?, ?, ?, ?, ?);";

        var rowsAffected = new Query(query)
                .withParam(1, trigger.getName())
                .withParam(2, trigger.getX())
                .withParam(3, trigger.getY())
                .withParam(4, trigger.getZ())
                .withParam(5, trigger.getBlockName())
                .withParam(6, trigger.getWorldUUID().toString())
                .withParam(7, trigger.getPlayer().getId())
                .execute();

        if (rowsAffected > 0) {
            var getIdOfTriggerQuery = "SELECT last_insert_rowid() as id";
            return new Query(getIdOfTriggerQuery)
                    .toSingle(catchyMapper(rs -> rs.getInt("id")));
        }

        return Optional.empty();
    }
}