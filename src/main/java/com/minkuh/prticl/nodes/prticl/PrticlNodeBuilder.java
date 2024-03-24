package com.minkuh.prticl.nodes.prticl;

import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import org.bukkit.Particle;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.NODE_DEFAULT_NAME;
import static com.minkuh.prticl.systemutil.resources.PrticlStrings.NODE_PARAM_ID;

/**
 * A builder pattern class for building Prticl nodes.
 */
public class PrticlNodeBuilder {
    private static Plugin plugin;
    private static Map<Integer, PrticlNode> prticlNodes = new TreeMap<>();
    private static List<PrticlNode> nodes = new ArrayList<>();
    private static PrticlNodeConfigUtil configUtil;

    private int id;
    private String name = NODE_DEFAULT_NAME;
    private int repeatDelay = 20;
    private int particleDensity = 1;
    private org.bukkit.Particle particleType = Particle.HEART;
    private PrticlLocationObject location;
    private String createdBy;

    public PrticlNodeBuilder(Plugin plugin) {
        this.plugin = plugin;
        this.configUtil = new PrticlNodeConfigUtil(plugin);
    }

    /**
     * Sets the name of the new Prticl node.
     *
     * @param name The name to call the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the particle type of the new Prticl node.
     *
     * @param particleType The particle type to assign to the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setParticleType(Particle particleType) {
        this.particleType = particleType;
        return this;
    }

    /**
     * Sets the location of the new Prticl node.
     *
     * @param location The location to assign to the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setLocationObject(PrticlLocationObject location) {
        this.location = location;
        return this;
    }

    /**
     * Sets the author of the new Prticl node.
     *
     * @param createdBy The author of the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    /**
     * Sets the respawn frequency of the new Prticl node.
     *
     * @param repeatDelay The respawn frequency of the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
        return this;
    }

    /**
     * Sets the particle density of the new Prticl node.
     *
     * @param particleDensity The particle density of the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
        return this;
    }

    /**
     * Builds the Prticl node.
     *
     * @return The freshly-baked Prticl node.
     */
    public PrticlNode build() {
        PrticlNode node = new PrticlNode(id, name, repeatDelay, particleDensity, particleType, location, createdBy);
        return addNodeToMapAndReturn(node);
    }

    /**
     * To manage every created node, we need to save them to an in-memory storage, as well as the one on the drive. <br>
     *
     * @param node The node to save
     * @return The unchanged node that was passed into this method.
     */
    private static PrticlNode addNodeToMapAndReturn(PrticlNode node) {

        Map<String, Object> nodesInConfig = configUtil.getConfigNodes();
        for (Map.Entry<String, Object> entry : nodesInConfig.entrySet()) {
            MemorySection particle = (MemorySection) entry.getValue();

            try {
                int nodeId = (int) particle.get(NODE_PARAM_ID);
                PrticlNode nodeForSaving = configUtil.getNodeFromConfigById(plugin.getConfig(), nodeId);

                prticlNodes.put(nodeId, nodeForSaving);
            } catch (Exception ignored) {
                plugin.getLogger().log(Level.SEVERE, "HORRIBLE STUFF HAPPENED--------------------");
            }
        }

        nodes.clear();
        nodes.addAll(prticlNodes.values());
        nodes.add(node);

        for (PrticlNode prticlNode : nodes) {
            node.setId(nodes.size());
            prticlNodes.put(nodes.size(), prticlNode);
        }

        plugin.saveConfig();
        return node;
    }
}
