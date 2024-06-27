package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.databasescripts.PrticlLocationDbScripts;
import com.minkuh.prticl.data.database.databasescripts.PrticlNodeDbScripts;
import com.minkuh.prticl.data.database.databasescripts.PrticlPlayerDbScripts;
import com.minkuh.prticl.data.wrappers.PaginatedResult;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PrticlNodeFunctions {
    private final PrticlLocationDbScripts locationScripts;
    private final PrticlNodeDbScripts nodeScripts;
    private final PrticlPlayerDbScripts playerScripts;

    public PrticlNodeFunctions(Connection connection) {
        this.locationScripts = new PrticlLocationDbScripts(connection);
        this.nodeScripts = new PrticlNodeDbScripts(connection);
        this.playerScripts = new PrticlPlayerDbScripts(connection);
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

    public List<PrticlNode> getNodesListByChunk(Chunk chunk) throws SQLException, NodeNotFoundException {
        if (!nodeScripts.chunkHasNodes(chunk))
            return null;

        return nodeScripts.getNodesListByChunk(chunk);
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

        int locationId = locationScripts.createLocation(node.getLocationObject().getLocation());
        int playerId = playerScripts.getPlayerIdByPlayerUuid(player.getUniqueId());

        return nodeScripts.createNode(node.getName(), node.getRepeatDelay(), node.getParticleDensity(), node.getParticleType().toString(), locationId, playerId);
    }
}