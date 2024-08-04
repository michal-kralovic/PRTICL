package com.minkuh.prticl.data.caches;

import com.minkuh.prticl.common.builders.LocationBuilder;
import com.minkuh.prticl.data.entities.Node;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A singleton HashMap cache. <br/>
 * <b>KEY:</b> A custom two integer object, holding the X and Z coordinates of a chunk. <br/>
 * <b>VALUE:</b> A List of the nodes in the respective chunk.
 */
public final class NodeChunkLocationsCache {
    private static NodeChunkLocationsCache INSTANCE;
    private final Map<ChunkKey, List<Node>> map;

    private NodeChunkLocationsCache() {
        map = new ConcurrentHashMap<>();
    }

    public static synchronized NodeChunkLocationsCache getInstance() {
        if (INSTANCE == null) INSTANCE = new NodeChunkLocationsCache();

        return INSTANCE;
    }

    public synchronized List<Node> getNodesFromCacheByChunk(Chunk chunk) {
        return getNodesFromCacheByChunkKey(new ChunkKey(chunk.getX(), chunk.getZ()));
    }

    public synchronized List<Node> getNodesFromCacheByChunkCoordinates(int x, int z) {
        return getNodesFromCacheByChunkKey(new ChunkKey(x, z));
    }

    public synchronized List<Node> getNodesFromCacheByChunkKey(ChunkKey chunkKey) {
        return map.get(chunkKey);
    }

    public synchronized void add(Node node) {
        ChunkKey chunkKey = nodeToKey(node);

        if (map.containsKey(chunkKey)) {
            map.get(chunkKey).add(node);
        } else {
            map.put(
                    chunkKey,
                    new ArrayList<>() {{
                        add(node);
                    }}
            );
        }
    }

    public synchronized boolean remove(Node node) {
        ChunkKey key = nodeToKey(node);
        List<Node> nodes = map.get(key);

        try {
            if (nodes != null) {
                if (!nodes.isEmpty()) {
                    nodes.remove(node);
                } else {
                    map.remove(key);
                }
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private ChunkKey nodeToKey(Node node) {
        var location = LocationBuilder.fromNode(node);

        return new ChunkKey(
                location.getChunk().getX(),
                location.getChunk().getZ()
        );
    }
}