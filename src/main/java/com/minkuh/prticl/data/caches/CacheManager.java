package com.minkuh.prticl.data.caches;

import com.minkuh.prticl.data.database.entities.Node;

import java.util.function.Predicate;

public class CacheManager {
    private static final NodeChunkLocationsCache nodeChunkLocationsCache = NodeChunkLocationsCache.getInstance();
    private static final SpawnedNodesCache spawnedNodesCache = SpawnedNodesCache.getInstance();

    public static synchronized void spawnInAllCaches(Node node) {
        spawnedNodesCache.add(node);

        if (node.isEnabled() && !nodeChunkLocationsCache.isInCache(node)) {
            nodeChunkLocationsCache.add(node);
        }
    }

    public static synchronized void removeFromAllCaches(Predicate<Node> condition) {
        spawnedNodesCache.remove(condition);
        nodeChunkLocationsCache.remove(condition);
    }
}