package com.minkuh.prticl.systemutil.configuration;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Map;

public class PrticlNodeConfigUtil {
    private Plugin plugin;

    public PrticlNodeConfigUtil(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves the given node to the configuration file.
     *
     * @return TRUE if successful.
     */
    public static boolean saveNodeToConfig(FileConfiguration config, PrticlNode node) {
        config.set("Particle " + node.getId(), node.serialize());
        try {
            config.save("plugins/Prticl/config.yml");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Gets the Prticl node from the config based on the provided ID of the Node.
     *
     * @param config The config file to look through
     * @param id     The ID to look for
     * @return The Prticl node.
     */
    public static PrticlNode getNodeFromConfigById(FileConfiguration config, int id) throws NodeNotFoundException {
        Object deserializedNode = config.get("Particle " + id);
        if (deserializedNode == null) {
            throw new NodeNotFoundException("The specified ");
        }
        return PrticlNode.deserialize((Map<String, Object>) deserializedNode);
    }
}