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
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TerrainStateChangeEventListener implements Listener {
    private final Prticl plugin;
    private final PrticlSpawner spawner;

    public TerrainStateChangeEventListener(Prticl plugin) {
        this.plugin = plugin;
        this.spawner = new PrticlSpawner(plugin);
    }

    @EventHandler
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        PrticlDatabase db = new PrticlDatabase(plugin);
        List<Node> nodes = db.getNodeFunctions().getByWorld(event.getWorld().getUID());
        List<Node> enabledNodes = db.getNodeFunctions().getEnabledNodes();

        for (var node : nodes) {
            NodeChunkLocationsCache.getInstance().add(node);
        }

        for (var node : enabledNodes) {
            SpawnedNodesCache.getInstance().addToCache(node);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        var eventWorldUuid = event.getWorld().getUID();

        // Set every node's spawned value to false
        SpawnedNodesCache.getInstance().getAll().stream()
                .filter(node -> node.isSpawned() && node.getWorldUUID().equals(eventWorldUuid))
                .forEach(node -> node.setSpawned(false));

        // Remove them from the SpawnedNodes cache
        SpawnedNodesCache.getInstance().removeWhere(node -> node.isSpawned() && node.getWorldUUID().equals(eventWorldUuid));

        // Remove them from the NodeChunkLocations cache
        NodeChunkLocationsCache.getInstance().removeWhere(node -> node.isSpawned() && node.getWorldUUID().equals(eventWorldUuid));
    }

    @EventHandler
    public void onChunkLoad(@NotNull ChunkLoadEvent event) {
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
    public void onChunkUnload(@NotNull ChunkUnloadEvent event) {
        // TODO: Implement chunk unload spawned nodes removal
    }
}