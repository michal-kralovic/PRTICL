package com.minkuh.prticl.event_listeners;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlSpawner;
import com.minkuh.prticl.data.caches.CacheManager;
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
    private final PrticlDatabase prticlDb = new PrticlDatabase();

    public TerrainStateChangeEventListener(Prticl plugin) {
        this.plugin = plugin;
        this.spawner = new PrticlSpawner(plugin);
    }

    @EventHandler
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        List<Node> nodes = prticlDb.getNodeFunctions().getByWorld(event.getWorld().getUID());
        List<Node> enabledNodes = prticlDb.getNodeFunctions().getEnabledNodes();

        for (var node : nodes) {
            NodeChunkLocationsCache.getInstance().add(node);
        }

        for (var node : enabledNodes) {
            SpawnedNodesCache.getInstance().add(node);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        var eventWorldUuid = event.getWorld().getUID();

        // Set every node's spawned value to false
        SpawnedNodesCache.getInstance().getAll().stream()
                .filter(node -> node.isSpawned() && node.getWorldUUID().equals(eventWorldUuid))
                .forEach(node -> node.setSpawned(false));

        // Remove them from the caches
        CacheManager.removeFromAllCaches(node -> node.isSpawned() && node.getWorldUUID().equals(eventWorldUuid));
    }

    @EventHandler
    public void onChunkLoad(@NotNull ChunkLoadEvent event) {
        Chunk eventChunk = event.getChunk();
        List<Node> nodesInTheChunk = NodeChunkLocationsCache.getInstance().getNodesFromCacheByChunk(eventChunk);

        if (nodesInTheChunk == null) return;

        for (Node node : nodesInTheChunk) {
            if (!node.isEnabled() || node.isSpawned()) continue;

            spawner.spawnNode(node);
            node.setSpawned(true);
            node.setEnabled(true);
            SpawnedNodesCache.getInstance().add(node);
        }
    }

    @EventHandler
    public void onChunkUnload(@NotNull ChunkUnloadEvent event) {
        Chunk eventChunk = event.getChunk();
        List<Node> nodesInTheChunk = NodeChunkLocationsCache.getInstance().getNodesFromCacheByChunk(eventChunk);

        if (nodesInTheChunk == null) return;

        for (Node node : nodesInTheChunk) {
            if (!node.isEnabled() || !node.isSpawned()) continue;

            node.setSpawned(false);
            SpawnedNodesCache.getInstance().remove(node);
        }
    }
}