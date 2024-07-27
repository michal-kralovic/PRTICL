package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.queries.PrticlNodeQueries;
import com.minkuh.prticl.data.database.queries.PrticlPlayerQueries;
import com.minkuh.prticl.common.wrappers.PaginatedResult;
import com.minkuh.prticl.common.PrticlNode;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PrticlNodeFunctions {
    private final PrticlNodeQueries nodeQueries;
    private final PrticlPlayerQueries playerQueries;

    public PrticlNodeFunctions(PGSimpleDataSource pgDataSource) {
        this.nodeQueries = new PrticlNodeQueries(pgDataSource);
        this.playerQueries = new PrticlPlayerQueries(pgDataSource);
    }

    public List<PrticlNode> getNodes() throws SQLException {
        return nodeQueries.getNodes();
    }

    public PaginatedResult<PrticlNode> getNodesByPage(int page) throws SQLException {
        return nodeQueries.getNodesByPage(page);
    }

    public PaginatedResult<PrticlNode> getNodesByPageByPlayer(int page, UUID playerUUID) throws SQLException {
        return nodeQueries.getNodesByPageByPlayer(page, playerUUID);
    }

    public PrticlNode getNodeById(int nodeId) throws SQLException {
        return nodeQueries.getNodeById(nodeId);
    }

    public PrticlNode getNodeByName(String nodeName) throws SQLException {
        return nodeQueries.getNodeByName(nodeName);
    }

    public List<String> getNodeNames() throws SQLException {
        return nodeQueries.getNodeNamesList();
    }

    public List<PrticlNode> getNodesByWorld(World world) throws SQLException {
        return nodeQueries.getNodesByWorld(world);
    }

    public List<PrticlNode> getNodesByCoordinates(int x, int z, World world) throws SQLException {
        return nodeQueries.getNodesByCoordinates(x, z, world);
    }

    public List<PrticlNode> getNodesListByChunk(Chunk chunk) throws SQLException {
        if (!nodeQueries.chunkHasNodes(chunk))
            return null;

        return nodeQueries.getNodesListByChunk(chunk);
    }

    public List<PrticlNode> getEnabledNodes() throws SQLException {
        return nodeQueries.getEnabledNodes();
    }

    public boolean setEnabled(PrticlNode node, boolean state) throws SQLException {
        node.setEnabled(state);
        return nodeQueries.setEnabled(node, state);
    }

    public boolean isNodeNameTaken(String nodeName) throws SQLException {
        return nodeQueries.isNodeNameTaken(nodeName);
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
        if (!playerQueries.isPlayerInDatabase(player))
            playerQueries.createPlayer(player);

        int locationId = node.getLocationObject().getId();
        int playerId = playerQueries.getPlayerIdByPlayerUUID(player.getUniqueId());

        return nodeQueries.createNode(
                node.getName(),
                node.getRepeatDelay(),
                node.getParticleDensity(),
                node.getParticleType().toString(),
                node.isEnabled(),
                locationId,
                playerId
        );
    }
}