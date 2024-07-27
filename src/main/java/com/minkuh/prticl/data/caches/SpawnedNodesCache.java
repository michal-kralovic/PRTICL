package com.minkuh.prticl.data.caches;

import com.minkuh.prticl.common.PrticlNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpawnedNodesCache {
    private static SpawnedNodesCache INSTANCE;
    private final List<PrticlNode> spawnedNodes;

    private SpawnedNodesCache() {
        spawnedNodes = new ArrayList<>();
    }

    public synchronized static SpawnedNodesCache getInstance() {
        if (INSTANCE == null) INSTANCE = new SpawnedNodesCache();

        return INSTANCE;
    }

    public synchronized List<PrticlNode> getAll() {
        List<PrticlNode> output = new ArrayList();
        output.addAll(spawnedNodes);

        return output;
    }

    public synchronized Optional<PrticlNode> get(int nodeId) {
        return spawnedNodes.stream().filter(node -> node.getId() == nodeId).findFirst();
    }

    public synchronized Optional<PrticlNode> get(String nodeName) {
        return spawnedNodes.stream().filter(node -> node.getName().equalsIgnoreCase(nodeName)).findFirst();
    }

    public synchronized void addToCache(PrticlNode node) {
        if (!node.isEnabled() || node.isSpawned())
            return;

        node.setSpawned(true);
        spawnedNodes.add(node);
    }

    public synchronized boolean any() {
        return !spawnedNodes.isEmpty();
    }

    public synchronized boolean remove(PrticlNode node) {
        node.setSpawned(false);

        this.spawnedNodes.remove(node);
        return spawnedNodes.contains(node);
    }
}