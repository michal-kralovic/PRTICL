package com.minkuh.prticl.common;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.repositories.NodeRepository;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
        if (SpawnedNodes.getName(node.getId()) != null)
            return false;

        var task = new PrticlNodeScheduler(node, this).runTaskTimer(plugin, 0, node.getRepeatDelay());

        var putSuccessful = SpawnedNodes.putTask(node.getId(), node.getName(), task);
        if (!putSuccessful)
            task.cancel();

        Validate.isTrue(putSuccessful, "Failed to spawn node!");

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
        private final Location location;
        private final Node node;
        private final World world;
        private final NodeSpawnManager manager;
        private int counter = 0;

        public PrticlNodeScheduler(Node node, NodeSpawnManager manager) {
            this.node = node;
            this.manager = manager;
            node.setSpawned(true);
            this.location = new Location(
                    Bukkit.getWorld(node.getWorldUUID()),
                    node.getX(),
                    node.getY(),
                    node.getZ()
            );
            this.world = location.getWorld();
        }

        @Override
        public void run() {
            // If repeat count is greater than 0, we impose a repeat count limit on the node
            if (node.getRepeatCount() > 0) {
                if (counter < node.getRepeatCount() - 1) {
                    counter++;
                } else {
                    manager.despawnNode(node, false);
                    cancel();
                }
            }

            if (node.isEnabled()) {
                world.spawnParticle(
                        node.getParticleType(),
                        location,
                        node.getParticleDensity()
                );

                return;
            }

            node.setSpawned(false);
            SpawnedNodes.deleteAndCancelNode(node.getId());
            cancel();
        }
    }

    public static class SpawnedNodes {
        private static final Map<Integer, BukkitTask> spawnedNodes = new ConcurrentHashMap<>();
        private static final Map<Integer, String> spawnedNodeNames = new ConcurrentHashMap<>();

        /**
         * Creates a new node with the specified ID and associated task.
         *
         * @param nodeId   The ID for the new node
         * @param nodeName The name for the new node
         * @param task     The BukkitTask to associate with this node
         * @return true if the node was created, false if a node with this ID already exists
         */
        public static boolean putTask(int nodeId, String nodeName, BukkitTask task) {
            if (spawnedNodes.containsKey(nodeId)) {
                return false;
            }
            spawnedNodes.put(nodeId, task);
            spawnedNodeNames.put(nodeId, nodeName);
            return true;
        }

        /**
         * Retrieves the BukkitTask associated with the specified node ID.
         *
         * @param nodeId The ID of the node to retrieve
         * @return The BukkitTask associated with the node, or null if no such node exists
         */
        public static BukkitTask getTask(int nodeId) {
            return spawnedNodes.get(nodeId);
        }

        /**
         * Retrieves the name associated with the specified node ID.
         *
         * @param nodeId The ID of the node to retrieve
         * @return The BukkitTask associated with the node, or null if no such node exists
         */
        public static String getName(int nodeId) {
            return spawnedNodeNames.get(nodeId);
        }

        /**
         * Checks if a node with the specified ID exists.
         *
         * @param nodeId The ID to check
         * @return true if a node with this ID exists, false otherwise
         */
        public static boolean hasTask(int nodeId) {
            return spawnedNodes.containsKey(nodeId);
        }

        /**
         * Gets all spawned nodes as a Map.
         *
         * @return An unmodifiable Map of all node IDs to their associated tasks
         */
        public static Map<Integer, BukkitTask> getAllTasks() {
            return Collections.unmodifiableMap(spawnedNodes);
        }

        /**
         * Gets all spawned nodes' names as a List.
         *
         * @return An unmodifiable list of all node IDs to their associated tasks
         */
        public static Map<Integer, String> getAllNames() {
            return Collections.unmodifiableMap(spawnedNodeNames);
        }

        /**
         * Gets the number of spawned nodes.
         *
         * @return The count of spawned nodes
         */
        public static int getTaskCount() {
            return spawnedNodes.size();
        }

        /**
         * Removes a node with the specified ID.
         * Note: This method does not cancel the BukkitTask, it only removes it from the map.
         *
         * @param nodeId The ID of the node to remove
         * @return The removed BukkitTask, or null if no node with this ID exists
         */
        public static BukkitTask deleteTask(int nodeId) {
            spawnedNodeNames.remove(nodeId);
            return spawnedNodes.remove(nodeId);
        }

        /**
         * Removes a node with the specified ID and cancels its associated task.
         *
         * @param nodeId The ID of the node to remove and cancel
         * @return true if a node was found and removed, false otherwise
         */
        public static boolean deleteAndCancelNode(int nodeId) {
            spawnedNodeNames.remove(nodeId);
            BukkitTask task = spawnedNodes.remove(nodeId);

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
            spawnedNodes.clear();
            spawnedNodeNames.clear();
        }

        /**
         * Clears all nodes from the map and cancels all associated tasks.
         */
        public static void clearAndCancelAllNodes() {
            for (BukkitTask task : spawnedNodes.values()) {
                task.cancel();
            }
            spawnedNodes.clear();
            spawnedNodeNames.clear();
        }
    }
}