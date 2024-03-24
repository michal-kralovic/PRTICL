package com.minkuh.prticl.data.database.functions;

import com.minkuh.prticl.data.database.databasescripts.PrticlLocationDbScripts;
import com.minkuh.prticl.data.database.databasescripts.PrticlNodeDbScripts;
import com.minkuh.prticl.data.database.databasescripts.PrticlPlayerDbScripts;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PrticlNodeFunctions {
    private final Connection connection;
    private final PrticlLocationDbScripts locationScripts;
    private final PrticlNodeDbScripts nodeScripts;
    private final PrticlPlayerDbScripts playerScripts;

    public PrticlNodeFunctions(Connection connection) {
        this.connection = connection;
        this.locationScripts = new PrticlLocationDbScripts(connection);
        this.nodeScripts = new PrticlNodeDbScripts(connection);
        this.playerScripts = new PrticlPlayerDbScripts(connection);
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

        if (!locationScripts.isLocationInDatabase(node.getLocationObject().getLocation()))
            locationScripts.createLocation(node.getLocationObject().getLocation());

        int playerId = playerScripts.getPlayerIdByPlayerUuid(player.getUniqueId().toString());
        int locationId = locationScripts.getLocationId(node.getLocationObject().getLocation());

        return nodeScripts.createNode(node.getName(), node.getRepeatDelay(), node.getParticleDensity(), node.getParticleType().toString(), locationId, playerId);
    }

    public List<String> getNodesList() throws SQLException {
        return nodeScripts.getNodesList();
    }
}
