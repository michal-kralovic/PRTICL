package com.minkuh.prticl.systemutil.configuration;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * A class for general configuration file manipulation.
 */
public class PrticlNodeConfigUtil {
    private static Plugin plugin;
    private static FileConfiguration config;

    public PrticlNodeConfigUtil(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /**
     * Saves the given node to the configuration file.
     *
     * @return TRUE if successful.
     */
    public static boolean saveNodeToConfig(FileConfiguration config, PrticlNode node) {
        config.set(NODE_CONFIGURATION_SECTION + "." + NODE_CHILD + "-" + node.getId(), node.serialize());
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
        section = plugin.getConfig().getConfigurationSection(NODE_CONFIGURATION_SECTION).getValues(true);

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
        Object deserializedNode = config.getConfigurationSection(NODE_CONFIGURATION_SECTION).get(NODE_CHILD + "-" + id);
        if (deserializedNode == null) {
            throw new NodeNotFoundException(NODE_NOT_FOUND);
        }

        if (deserializedNode instanceof MemorySection) {
            Map<String, Object> memorySectionDeserializedNode = new HashMap<>();
            memorySectionDeserializedNode.put(NODE_PARAM_OWNER, ((MemorySection) deserializedNode).get(NODE_PARAM_OWNER));
            memorySectionDeserializedNode.put(NODE_PARAM_PARTICLE_DENSITY, ((MemorySection) deserializedNode).get(NODE_PARAM_PARTICLE_DENSITY));
            memorySectionDeserializedNode.put(NODE_PARAM_PARTICLE_TYPE, ((MemorySection) deserializedNode).get(NODE_PARAM_PARTICLE_TYPE));
            memorySectionDeserializedNode.put(NODE_PARAM_REPEAT_DELAY, ((MemorySection) deserializedNode).get(NODE_PARAM_REPEAT_DELAY));
            memorySectionDeserializedNode.put(NODE_PARAM_NAME, ((MemorySection) deserializedNode).get(NODE_PARAM_NAME));
            memorySectionDeserializedNode.put(NODE_PARAM_ID, ((MemorySection) deserializedNode).get(NODE_PARAM_ID));

            return PrticlNode.deserialize(memorySectionDeserializedNode);
        }

        return PrticlNode.deserialize((Map<String, Object>) deserializedNode);
    }

    public static boolean configNodeSectionExists() {
        boolean result = true;
        if (config.getConfigurationSection(NODE_CONFIGURATION_SECTION) == null) {
            return false;
        }
        return result;
    }

    public static boolean configNodeSectionExists(CommandSender sender) {
        BaseMessageComponents prticlMessage = new BaseMessageComponents();
        boolean result = true;
        if (config.getConfigurationSection(NODE_CONFIGURATION_SECTION) == null) {
            sender.sendMessage(prticlMessage.error(CONFIG_SECTION_NOT_FOUND));
            return false;
        }
        return result;
    }
}