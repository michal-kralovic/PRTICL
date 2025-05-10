package com.minkuh.prticl.events;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.NodeSpawnManager;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.repositories.NodeRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class TerrainStateChangeEventListener implements Listener {
    private final NodeSpawnManager spawnManager;
    private final NodeRepository nodeRepository;
    public static boolean zeroZeroChunkLoaded = false;

    public TerrainStateChangeEventListener(Prticl prticl) {
        this.spawnManager = new NodeSpawnManager(prticl);
        this.nodeRepository = new NodeRepository(prticl.getLogger());
    }

    @EventHandler
    public void onChunkLoad(@NotNull ChunkLoadEvent event) {
        var eventChunk = event.getChunk();
        var worldUID = event.getWorld().getUID().toString();

        if (!zeroZeroChunkLoaded) {
            var nodesInChunk = nodeRepository.getByChunk(0, 0, worldUID);
            zeroZeroChunkLoaded = true;

            spawnNodes(nodesInChunk);
        }

        var chunkX = eventChunk.getX();
        var chunkZ = eventChunk.getZ();
        var nodesInChunk = nodeRepository.getByChunk(chunkX, chunkZ, worldUID);
        spawnNodes(nodesInChunk);
    }

    @EventHandler
    public void onChunkUnload(@NotNull ChunkUnloadEvent event) {
        var eventChunk = event.getChunk();

        var worldUID = event.getWorld().getUID().toString();
        var chunkX = eventChunk.getX();
        var chunkZ = eventChunk.getZ();

        var nodesInChunk = nodeRepository.getByChunk(chunkX, chunkZ, worldUID);
        if (nodesInChunk.isEmpty() || nodesInChunk.get().isEmpty())
            return;

        for (var node : nodesInChunk.get()) {
            System.out.println("Despawning: " + node.getName());
            spawnManager.despawnNode(node, false);
        }
    }

    private void spawnNodes(Optional<List<Node>> nodesOpt) {
        if (nodesOpt.isEmpty() || nodesOpt.get().isEmpty())
            return;

        for (var node : nodesOpt.get()) {
            spawnManager.spawnNode(node);
        }
    }
}