package com.minkuh.prticl.data.caches;

import com.minkuh.prticl.data.database.entities.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpawnedNodesCache {
    private static SpawnedNodesCache INSTANCE;
    private final List<Node> spawnedNodes;

    private SpawnedNodesCache() {
        spawnedNodes = new ArrayList<>();
    }

    public synchronized static SpawnedNodesCache getInstance() {
        if (INSTANCE == null) INSTANCE = new SpawnedNodesCache();

        return INSTANCE;
    }

    public synchronized List<Node> getAll() {
        List<Node> output = new ArrayList();
        output.addAll(spawnedNodes);

        return output;
    }

    public synchronized Optional<Node> get(int nodeId) {
        return spawnedNodes.stream().filter(node -> node.getId() == nodeId).findFirst();
    }

    public synchronized Optional<Node> get(String nodeName) {
        return spawnedNodes.stream().filter(node -> node.getName().equalsIgnoreCase(nodeName)).findFirst();
    }

    public synchronized void addToCache(Node node) {
        if (!node.isEnabled() || node.isSpawned())
            return;

        node.setSpawned(true);
        spawnedNodes.add(node);
    }

    public synchronized boolean isInCache(Node node) {
        return spawnedNodes.stream().anyMatch(lNode -> lNode.getId() == node.getId());
    }

    public synchronized boolean any() {
        return !spawnedNodes.isEmpty();
    }

    public synchronized boolean remove(Node node) {
        node.setSpawned(false);

        return this.spawnedNodes.remove(node);
    }
}