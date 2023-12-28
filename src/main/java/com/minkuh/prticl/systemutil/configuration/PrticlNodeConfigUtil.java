package com.minkuh.prticl.systemutil.configuration;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A class for general configuration file manipulation.
 */
public class PrticlNodeConfigUtil {
    private static Plugin plugin;

    public PrticlNodeConfigUtil(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves the given node to the configuration file.
     *
     * @return TRUE if successful.
     */
    public static boolean saveNodeToConfig(FileConfiguration config, PrticlNode node) {
        config.set("particles.particle-" + node.getId(), node.serialize());
        try {
            config.save("plugins/Prticl/config.yml");
        } catch (IOException e) {
            return false;
        }
        plugin.reloadConfig();
        return true;
    }

    public static Map<String, Object> loadConfigNodes() throws NullPointerException {

        Map<String, Object> section;
        section = plugin.getConfig().getConfigurationSection("particles").getValues(true);

        section.entrySet().removeIf(entry -> !(entry.getValue() instanceof MemorySection));

        return section;
    }

    /**
     * Gets the Prticl node from the config based on the provided ID of the Node.
     *
     * @param config The config file to look through
     * @param id     The ID to look for
     * @return The Prticl node.
     * @throws NodeNotFoundException If the node can't be found within the config.
     */
    public static PrticlNode getNodeFromConfigById(FileConfiguration config, int id) throws NodeNotFoundException {
        Object deserializedNode = config.getConfigurationSection("particles").get("particle-" + id);
        if (deserializedNode == null) {
            throw new NodeNotFoundException("The specified node couldn't be found.");
        }

        if (deserializedNode instanceof MemorySection) {
            Map<String, Object> memorySectionDeserializedNode = new HashMap<>();
            memorySectionDeserializedNode.put("owner", ((MemorySection) deserializedNode).get("owner"));
            memorySectionDeserializedNode.put("particle-density", ((MemorySection) deserializedNode).get("particle-density"));
            memorySectionDeserializedNode.put("particle-type", ((MemorySection) deserializedNode).get("particle-type"));
            memorySectionDeserializedNode.put("repeat-delay", ((MemorySection) deserializedNode).get("repeat-delay"));
            memorySectionDeserializedNode.put("name", ((MemorySection) deserializedNode).get("name"));
            memorySectionDeserializedNode.put("id", ((MemorySection) deserializedNode).get("id"));

            return PrticlNode.deserialize(memorySectionDeserializedNode);
        }

        Map<String,Object> aa = (Map<String, Object>) deserializedNode;

        return PrticlNode.deserialize(aa);
    }
}