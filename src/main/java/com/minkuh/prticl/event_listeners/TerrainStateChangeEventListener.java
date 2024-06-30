package com.minkuh.prticl.event_listeners;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.schedulers.PrticlScheduler;
import com.minkuh.prticl.systemutil.message.PrticlMessages;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class TerrainStateChangeEventListener implements Listener {
    private final Prticl plugin;
    private final PrticlDatabase prticlDatabase;

    public TerrainStateChangeEventListener(Prticl plugin) {
        this.plugin = plugin;
        try {
            this.prticlDatabase = new PrticlDatabase(plugin);
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) throws SQLException {
        Chunk chunk = event.getChunk();
        List<PrticlNode> nodesInTheChunk = prticlDatabase.getNodeFunctions().getNodesListByChunk(chunk);

        if (nodesInTheChunk == null) {
            Bukkit.getLogger().log(Level.FINE, "No nodes found in chunk: x:" + chunk.getX() + ", z:" + chunk.getZ());
            return;
        }

        plugin.getServer().sendMessage(new PrticlMessages().system(Arrays.toString(nodesInTheChunk.toArray())));

        for (PrticlNode node : nodesInTheChunk) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlScheduler(node), 0, node.getRepeatDelay());
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) throws SQLException {
        // TODO: Add the de-spawning of nodes of the unloading world
    }
}