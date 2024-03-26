package com.minkuh.prticl.nodes.prticl;

import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.NODE_DEFAULT_NAME;

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

    public PrticlNodeBuilder() {
    }

    public PrticlNodeBuilder(Plugin plugin) {
        this.plugin = plugin;
        this.configUtil = new PrticlNodeConfigUtil(plugin);
    }

    public PrticlNodeBuilder setId(int id) {
        this.id = id;
        return this;
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

    public PrticlNode build() {
        return new PrticlNode(id, name, repeatDelay, particleDensity, particleType, location, createdBy);
    }
}
