package com.minkuh.prticl.event_listeners;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlSpawner;
import com.minkuh.prticl.data.caches.NodeChunkLocationsCache;
import com.minkuh.prticl.data.caches.SpawnedNodesCache;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.database.entities.Node;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.List;

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
        List<Node> nodesInTheChunk = NodeChunkLocationsCache.getInstance().getNodesFromCacheByChunk(eventChunk);

        if (nodesInTheChunk == null) {
            plugin.getLogger().fine("No nodes found in chunk: x:" + eventChunk.getX() + ", z:" + eventChunk.getZ());
            return;
        }

        for (Node node : nodesInTheChunk) {
            if (!node.isEnabled()) continue;

            spawner.spawnNode(node);
            node.setSpawned(true);
            node.setEnabled(true);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        // TODO: Add the de-spawning of nodes of the unloading world
    }

    private void setupCachesOnWorldLoad(WorldLoadEvent event) {
        List<Node> nodes;
        List<Node> enabledNodes;

        PrticlDatabase db = new PrticlDatabase(plugin);
        nodes = db.getNodeFunctions().getByWorld(event.getWorld().getUID());
        enabledNodes = db.getNodeFunctions().getEnabledNodes();

        for (var node : nodes) {
            NodeChunkLocationsCache.getInstance().add(node);
        }

        for (var node : enabledNodes) {
            SpawnedNodesCache.getInstance().addToCache(node);
        }
    }
}