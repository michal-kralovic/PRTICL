package com.minkuh.prticl.eventlisteners;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.schedulers.PrticlScheduler;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public class TerrainStateChangeEventListener implements Listener {
    private final Prticl plugin;
    private final PrticlDatabase prticlDatabase;

    public TerrainStateChangeEventListener(Prticl plugin) throws SQLException {
        this.plugin = plugin;
        this.prticlDatabase = new PrticlDatabase(plugin);
    }

//    @EventHandler
//    public void onWorldLoad(WorldLoadEvent event) throws SQLException {
//        World world = event.getWorld();
//        List<PrticlNode> filteredNodeList = prticlDatabase.getNodeFunctions().getNodesListByWorld(world);
//
//        for (PrticlNode node : filteredNodeList) {
//            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlScheduler(node), 0, node.getRepeatDelay());
//        }
//    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) throws SQLException, NodeNotFoundException {
        Chunk chunk = event.getChunk();
        List<PrticlNode> nodesInTheChunk;
        nodesInTheChunk = prticlDatabase.getNodeFunctions().getNodesListByChunk(chunk);

        if (nodesInTheChunk == null) {
            Bukkit.getLogger().log(Level.FINE, "No nodes found in chunk: x:" + chunk.getX() + ", z:" + chunk.getZ());
            return;
        }

        for (PrticlNode node : nodesInTheChunk) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlScheduler(node), 0, node.getRepeatDelay());
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) throws SQLException {
        // TODO: Add the de-spawning of nodes of the unloading world
    }
}