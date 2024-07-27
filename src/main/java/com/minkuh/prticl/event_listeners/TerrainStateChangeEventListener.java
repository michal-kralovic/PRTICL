package com.minkuh.prticl.event_listeners;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlNode;
import com.minkuh.prticl.data.caches.NodeChunkLocationsCache;
import com.minkuh.prticl.data.caches.SpawnedNodesCache;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.schedulers.PrticlSpawner;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public class TerrainStateChangeEventListener implements Listener {
    private final Prticl plugin;
    private final PrticlSpawner spawner;

    public TerrainStateChangeEventListener(Prticl plugin) {
        this.plugin = plugin;
        this.spawner = new PrticlSpawner(plugin);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        setupCachesOnWorldLoad(event);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk eventChunk = event.getChunk();
        List<PrticlNode> nodesInTheChunk = NodeChunkLocationsCache.getInstance().getNodesFromCacheByChunk(eventChunk);

        if (nodesInTheChunk == null) {
            plugin.getLogger().log(Level.FINE, "No nodes found in chunk: x:" + eventChunk.getX() + ", z:" + eventChunk.getZ());
            return;
        }

        for (PrticlNode node : nodesInTheChunk) {
            if (!node.isEnabled()) continue;

            spawner.spawnNode(node);
            node.setSpawned(true);
            node.setEnabled(true);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) throws SQLException {
        // TODO: Add the de-spawning of nodes of the unloading world
    }

    private void setupCachesOnWorldLoad(WorldLoadEvent event) {
        List<PrticlNode> nodes;
        List<PrticlNode> enabledNodes;
        try {
            PrticlDatabase db = new PrticlDatabase(plugin);
            nodes = db.getNodeFunctions().getNodesByWorld(event.getWorld());
            enabledNodes = db.getNodeFunctions().getEnabledNodes();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to initialize the database while preparing the cache!");
        }

        for (var node : nodes) {
            NodeChunkLocationsCache.getInstance().add(node);
        }

        for (var node : enabledNodes) {
            SpawnedNodesCache.getInstance().addToCache(node);
        }
    }
}