package com.minkuh.prticl.common;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.repositories.NodeRepository;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeSpawnManager {
    private final NodeRepository nodeRepository;
    private final Prticl plugin;

    public NodeSpawnManager(Prticl plugin) {
        this.plugin = plugin;
        this.nodeRepository = new NodeRepository(plugin.getLogger());
    }

    public boolean spawnNode(Node node) {
        Validate.isTrue(node.getId() != 0, "Node ID can't be 0!");
        if (SpawnedNodes.getNode(node.getId()) != null)
            return false;

        var putSuccessfulNode = SpawnedNodes.putNode(node);

        var task = new PrticlNodeScheduler(node.getId(), this).runTaskTimer(plugin, 0, node.getRepeatDelay());

        var putSuccessfulTask = SpawnedNodes.putTask(node.getId(), task);

        if (!putSuccessfulTask || !putSuccessfulNode) {
            task.cancel();
        }

        Validate.isTrue(putSuccessfulNode || putSuccessfulTask, "Failed to spawn node!");

        nodeRepository.setEnabled(node, true);
        nodeRepository.setSpawned(node, true);
        node.setEnabled(true);
        node.setSpawned(true);

        return true;
    }

    public void despawnNode(Node node, boolean disable) {
        Validate.isTrue(
                SpawnedNodes.deleteAndCancelNode(node.getId()),
                "Failed to despawn node (id " + node.getId() + ')'
        );

        nodeRepository.setEnabled(node, !disable);
        nodeRepository.setSpawned(node, false);
        node.setEnabled(!disable);
        node.setSpawned(false);
    }

    private static class PrticlNodeScheduler extends BukkitRunnable {
        private final Node node;
        private final int nodeId;
        private final NodeSpawnManager manager;
        private int counter = 0;

        public PrticlNodeScheduler(int nodeId, NodeSpawnManager manager) {
            this.nodeId = nodeId;
            this.manager = manager;
            this.node = SpawnedNodes.getNode(nodeId);

            if (node != null) {
                node.setSpawned(true);
            }
        }

        @Override
        public void run() {
            if (node == null) {
                cancel();
                return;
            }

            // If repeat count is greater than 0, we impose a repeat count limit on the node
            if (node.getRepeatCount() > 0) {
                if (counter < node.getRepeatCount()) {
                    counter++;
                } else {
                    manager.despawnNode(node, true);
                    cancel();
                    return;
                }
            }

            if (node.isEnabled()) {
                var location = new Location(
                        Bukkit.getWorld(node.getWorldUUID()),
                        node.getX(),
                        node.getY(),
                        node.getZ()
                );

                var world = location.getWorld();
                if (world != null) {
                    world.spawnParticle(
                            node.getParticleType(),
                            location,
                            node.getParticleDensity()
                    );
                }
                return;
            }

            node.setSpawned(false);
            SpawnedNodes.deleteAndCancelNode(nodeId);
            cancel();
        }
    }


    public static class SpawnedNodes {
        private static final Map<Integer, BukkitTask> spawnedNodeTasks = new ConcurrentHashMap<>();
        private static final Map<Integer, Node> spawnedNodes = new ConcurrentHashMap<>();

        /**
         * Creates a new node with the specified ID and associated task.
         *
         * @param nodeId   The ID for the new node
         * @param task     The BukkitTask to associate with this node
         * @return true if the node was created, false if a node with this ID already exists
         */
        public static boolean putTask(int nodeId, BukkitTask task) {
            if (spawnedNodeTasks.containsKey(nodeId)) {
                return false;
            }
            spawnedNodeTasks.put(nodeId, task);
            return true;
        }

        /**
         * Creates a new node map entry with the specified ID.
         *
         * @param node   The node
         * @return true if the node was created, false if a node with this ID already exists
         */
        public static boolean putNode(Node node) {
            if (spawnedNodes.containsKey(node.getId())) {
                return false;
            }
            spawnedNodes.put(node.getId(), node);
            return true;
        }

        /**
         * Retrieves the BukkitTask associated with the specified node ID.
         *
         * @param nodeId The ID of the node to retrieve
         * @return The BukkitTask associated with the node, or null if no such node exists
         */
        public static BukkitTask getTask(int nodeId) {
            return spawnedNodeTasks.get(nodeId);
        }

        /**
         * Retrieves the node with the specified node ID.
         *
         * @param nodeId The ID of the node to retrieve
         * @return The BukkitTask associated with the node, or null if no such node exists
         */
        public static Node getNode(int nodeId) {
            return spawnedNodes.get(nodeId);
        }

        /**
         * Checks if a node with the specified ID exists.
         *
         * @param nodeId The ID to check
         * @return true if a node with this ID exists, false otherwise
         */
        public static boolean hasTask(int nodeId) {
            return spawnedNodeTasks.containsKey(nodeId);
        }

        /**
         * Gets all spawned nodes as a Map.
         *
         * @return An unmodifiable Map of all node IDs to their associated tasks
         */
        public static Map<Integer, BukkitTask> getAllTasks() {
            return Collections.unmodifiableMap(spawnedNodeTasks);
        }

        /**
         * Gets all spawned nodes' names as a List.
         *
         * @return An unmodifiable list of all node IDs to their associated tasks
         */
        public static Map<Integer, Node> getAllNodes() {
            return Collections.unmodifiableMap(spawnedNodes);
        }

        /**
         * Gets the number of spawned nodes.
         *
         * @return The count of spawned nodes
         */
        public static int getTaskCount() {
            return spawnedNodeTasks.size();
        }

        /**
         * Removes a node with the specified ID.
         * Note: This method does not cancel the BukkitTask, it only removes it from the map.
         *
         * @param nodeId The ID of the node to remove
         * @return The removed BukkitTask, or null if no node with this ID exists
         */
        public static BukkitTask deleteTask(int nodeId) {
            spawnedNodes.remove(nodeId);
            return spawnedNodeTasks.remove(nodeId);
        }

        /**
         * Removes a node with the specified ID and cancels its associated task.
         *
         * @param nodeId The ID of the node to remove and cancel
         * @return true if a node was found and removed, false otherwise
         */
        public static boolean deleteAndCancelNode(int nodeId) {
            spawnedNodes.remove(nodeId);
            BukkitTask task = spawnedNodeTasks.remove(nodeId);

            if (task != null) {
                task.cancel();
                return true;
            }

            return false;
        }

        /**
         * Clears all nodes from the map.
         * Note: This method does not cancel the BukkitTasks.
         */
        public static void clearNodes() {
            spawnedNodeTasks.clear();
            spawnedNodes.clear();
        }

        /**
         * Clears all nodes from the map and cancels all associated tasks.
         */
        public static void clearAndCancelAllNodes() {
            for (BukkitTask task : spawnedNodeTasks.values()) {
                task.cancel();
            }
            spawnedNodeTasks.clear();
            spawnedNodes.clear();
        }
    }
}