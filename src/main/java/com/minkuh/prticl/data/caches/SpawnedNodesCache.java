package com.minkuh.prticl.data.caches;

import com.minkuh.prticl.data.database.entities.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class SpawnedNodesCache {
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
        return spawnedNodes;
    }

    public synchronized Optional<Node> get(int nodeId) {
        return spawnedNodes.stream().filter(node -> node.getId() == nodeId).findFirst();
    }

    public synchronized Optional<Node> get(String nodeName) {
        return spawnedNodes.stream().filter(node -> node.getName().equalsIgnoreCase(nodeName)).findFirst();
    }

    public synchronized void add(Node node) {
        if (!node.isEnabled())
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

        try {
            spawnedNodes.removeIf(fNode -> fNode.getId() == node.getId());
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    public synchronized void remove(Predicate<Node> condition) {
        for (var node : spawnedNodes.stream().filter(condition).toList()) {
            node.setSpawned(false);

            this.spawnedNodes.removeIf(lNode -> lNode.getId() == node.getId());
        }
    }
}