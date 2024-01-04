package com.minkuh.prticl.systemutil.configuration;

import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.*;

import static com.minkuh.prticl.nodes.prticl.PrticlNode.deserialize;
import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * A class for general configuration file manipulation.
 */
public class PrticlNodeConfigUtil {
    private static Plugin plugin;
    private static FileConfiguration config;

    public PrticlNodeConfigUtil(Plugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    /**
     * Saves the given node to the configuration file.
     *
     * @return TRUE if successful.
     */
    public boolean saveNodeToConfig(FileConfiguration config, PrticlNode node) {
        config.set(NODE_CONFIGURATION_SECTION + "." + NODE_CHILD + "-" + node.getId(), node.serialize());
        try {
            config.save("plugins/Prticl/config.yml");
            // plugin.saveConfig();
        } catch (IOException e) {
            return false;
        }
        plugin.reloadConfig();
        return true;
    }

    /**
     * Loads every single node out of the config file. <br>
     * The nodes are loaded like: <br><br>
     * - key: node-1 (automatically generated)<br>
     * - value (Object): < node parameters ><br><br>
     * Example:<br><b>
     * - node-1 -> {id=1, name="my node!", owner="Steve", ...} <br>
     * - node-2 -> {id=2, name="heart node", owner="Alex", ...} <br> </b>
     * ...
     *
     * @return a Map with the resulting nodes.
     * @throws NullPointerException if config can't be found
     */
    public Map<String, Object> getConfigNodes() throws NullPointerException {
        Map<String, Object> nodes;
        nodes = plugin.getConfig().getConfigurationSection(NODE_CONFIGURATION_SECTION).getValues(true);

        nodes.entrySet().removeIf(entry -> !(entry.getValue() instanceof MemorySection));

        return nodes;
    }

    /**
     * Loads every single node out of the config file. <br>
     *
     * @return a List< PrticlNode > with the resulting nodes.
     * @throws NullPointerException if config can't be found
     */
    public List<PrticlNode> getConfigNodesList() throws NullPointerException {
        Map<String, Object> nodes;
        nodes = plugin.getConfig().getConfigurationSection(NODE_CONFIGURATION_SECTION).getValues(true);

        nodes.entrySet().removeIf(entry -> !(entry.getValue() instanceof MemorySection));

        List<PrticlNode> listOfNodes = new ArrayList<>();

        for (Map.Entry<String, Object> entry : nodes.entrySet()) {
            listOfNodes.add(deserialize(entry));
        }

        return listOfNodes;
    }

    /**
     * Gets the Prticl node from the config based on the provided ID of the Node.
     *
     * @param config The config file to look through
     * @param id     The ID to look for
     * @return The Prticl node.
     * @throws NodeNotFoundException If the node can't be found in the config
     */
    public PrticlNode getNodeFromConfigById(FileConfiguration config, int id) throws NodeNotFoundException {
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

            return deserialize(memorySectionDeserializedNode);
        }

        return deserialize((Map<String, Object>) deserializedNode);
    }

    /**
     * Gets the Prticl node from the config based on the provided Name of the Node.
     *
     * @param name The Name of the Node to look for
     * @return The PrticlNode, if found.
     * @throws NodeNotFoundException If the node can't be found in the config
     */
    public PrticlNode getNodeFromConfigByName(String name) throws NodeNotFoundException {
        List<PrticlNode> listOfNodes = getConfigNodesList();
        Optional<PrticlNode> result = listOfNodes.stream()
                .filter(p -> p.getName().toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT)))
                .findFirst();
        if (result.isEmpty())
            throw new NodeNotFoundException(NODE_NOT_FOUND);
        return result.get();
    }

    /**
     * Checks for the existence of the configuration section where nodes are stored.
     *
     * @return TRUE if exists.
     */
    public boolean configNodeSectionExists() {
        boolean result = true;
        if (config.getConfigurationSection(NODE_CONFIGURATION_SECTION) == null) {
            return false;
        }
        return result;
    }

    /**
     * Checks for the existence of the configuration section where nodes are stored. <br>
     * Overload for configNodeSectionExists which also sends the command sender an error message.
     *
     * @param sender The sender who sent the command
     * @return TRUE if exists.
     */
    public boolean configNodeSectionExists(CommandSender sender) {
        BaseMessageComponents prticlMessage = new BaseMessageComponents();
        boolean result = true;
        if (config.getConfigurationSection(NODE_CONFIGURATION_SECTION) == null) {
            sender.sendMessage(prticlMessage.error(CONFIG_SECTION_NOT_FOUND));
            return false;
        }
        return result;
    }
}