package com.minkuh.prticl.common;

import org.bukkit.Particle;

import static com.minkuh.prticl.common.resources.PrticlConstants.NODE_DEFAULT_NAME;

/**
 * A builder pattern class for building Prticl nodes.
 */
public class PrticlNodeBuilder {
    private int id;
    private String name = NODE_DEFAULT_NAME;
    private int repeatDelay = 20;
    private int particleDensity = 1;
    private boolean isEnabled = false;
    private org.bukkit.Particle particleType = Particle.HEART;
    private PrticlLocationObject location;
    private String createdBy;

    public PrticlNodeBuilder() {
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

    public PrticlNodeBuilder setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    public PrticlNode build() {
        return new PrticlNode(id, name, repeatDelay, particleDensity, isEnabled, particleType, location, createdBy);
    }
}