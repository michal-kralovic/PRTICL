package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.queries.PrticlNodeQueries;
import com.minkuh.prticl.data.database.queries.PrticlPlayerQueries;
import com.minkuh.prticl.data.wrappers.PaginatedResult;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PrticlNodeFunctions {
    private final PrticlNodeQueries nodeScripts;
    private final PrticlPlayerQueries playerScripts;

    public PrticlNodeFunctions(PGSimpleDataSource pgDataSource) {
        this.nodeScripts = new PrticlNodeQueries(pgDataSource);
        this.playerScripts = new PrticlPlayerQueries(pgDataSource);
    }

    public PaginatedResult<PrticlNode> getNodesByPage(int page) throws SQLException {
        return nodeScripts.getNodesByPage(page);
    }

    public PaginatedResult<PrticlNode> getNodesByPageByPlayer(int page, UUID playerUUID) throws SQLException {
        return nodeScripts.getNodesByPageByPlayer(page, playerUUID);
    }

    public PrticlNode getNodeById(int nodeId) throws SQLException {
        return nodeScripts.getNodeById(nodeId);
    }

    public PrticlNode getNodeByName(String nodeName) throws SQLException {
        return nodeScripts.getNodeByName(nodeName);
    }

    public List<String> getNodeNames() throws SQLException {
        return nodeScripts.getNodeNamesList();
    }

    public List<PrticlNode> getNodesByWorld(World world) throws SQLException {
        return nodeScripts.getNodesByWorld(world);
    }

    public List<PrticlNode> getNodesByCoordinates(int x, int z, World world) throws SQLException {
        return nodeScripts.getNodesByCoordinates(x, z, world);
    }

    public List<PrticlNode> getNodesListByChunk(Chunk chunk) throws SQLException {
        if (!nodeScripts.chunkHasNodes(chunk))
            return null;

        return nodeScripts.getNodesListByChunk(chunk);
    }

    public boolean setSpawnedAndEnabled(PrticlNode node, boolean newState) throws SQLException {
        return nodeScripts.setSpawnedAndEnabled(node, newState);
    }

    public boolean isNodeNameTaken(String nodeName) throws SQLException {
        return nodeScripts.isNodeNameTaken(nodeName);
    }

    /**
     * Creates an entry for a node in the database.<br>
     * Additionally, creates a Player entry if non-existent, and a Location entry.
     *
     * @param player A player to create the node for
     * @param node   The node to create
     * @return TRUE, if the operation was successful.
     */
    public boolean addNodeToDatabase(Player player, PrticlNode node) throws SQLException {
        if (!playerScripts.isPlayerInDatabase(player))
            playerScripts.createPlayer(player);

        int locationId = node.getLocationObject().getId();
        int playerId = playerScripts.getPlayerIdByPlayerUUID(player.getUniqueId());

        return nodeScripts.createNode(
                node.getName(),
                node.getRepeatDelay(),
                node.getParticleDensity(),
                node.getParticleType().toString(),
                node.isSpawned(),
                node.isEnabled(),
                locationId,
                playerId
        );
    }
}