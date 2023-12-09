package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A builder pattern class for building Prticl nodes.
 */
public class PrticlNodeBuilder {
    private static Plugin plugin;
    private static SortedMap<Integer, PrticlNode> prticlNodes = new TreeMap<>();
    private static List<PrticlNode> nodes = new ArrayList<>();

    private int id;
    private String name = "node";
    private int repeatDelay = 20;
    private int particleDensity = 1;
    private org.bukkit.Particle particleType = Particle.HEART;
    private Location location;
    private String createdBy;

    public PrticlNodeBuilder(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets the name of the new Prticl node.
     * @param name The name to call the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the particle type of the new Prticl node.
     * @param particleType The particle type to assign to the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setParticleType(Particle particleType) {
        this.particleType = particleType;
        return this;
    }

    /**
     * Sets the location of the new Prticl node.
     * @param location The location to assign to the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }

    /**
     * Sets the author of the new Prticl node.
     * @param createdBy The author of the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    /**
     * Sets the respawn frequency of the new Prticl node.
     * @param repeatDelay The respawn frequency of the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
        return this;
    }

    /**
     * Sets the particle density of the new Prticl node.
     * @param particleDensity The particle density of the new Prticl node
     * @return This builder.
     */
    public PrticlNodeBuilder setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
        return this;
    }

    /**
     * Builds the Prticl node.
     * @return The freshly-baked Prticl node.
     */
    public PrticlNode build() {
        PrticlNode node = new PrticlNode(id, name, repeatDelay, particleDensity, particleType, location, createdBy);
        return addNodeToMapAndReturn(node);
    }

    private static PrticlNode addNodeToMapAndReturn(PrticlNode node) {
        nodes.clear();
        nodes.addAll(prticlNodes.values());
        nodes.add(node);

        for (PrticlNode prticlNode : nodes) {
            node.setId(nodes.size());
            prticlNodes.put(nodes.size(), prticlNode);
        }

        return node;
    }
}
