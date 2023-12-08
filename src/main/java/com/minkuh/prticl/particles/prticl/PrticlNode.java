package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The most important element of PRTICL - its Nodes.
 * <br>A model class for a PRTICL node.
 */
public class PrticlNode implements ConfigurationSerializable {
    private int id;
    private String name;
    private int repeatDelay = 20;
    private int particleDensity = 1;
    private org.bukkit.Particle particleType;
    private Location location;
    private String createdBy;

    public PrticlNode() {
    }

    public PrticlNode(int id, String name, int repeatDelay, int particleDensity, Particle particleType, Location location, String createdBy) {
        this.id = id;
        this.name = name;
        this.repeatDelay = repeatDelay;
        this.particleDensity = particleDensity;
        this.particleType = particleType;
        this.location = location;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() <= 50 && !name.isBlank())
            this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public Particle getParticleType() {
        return particleType;
    }

    public void setParticleType(Particle particleType) {
        this.particleType = particleType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getParticleDensity() {
        return particleDensity;
    }

    public void setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PrticlNode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", repeatDelay=" + repeatDelay +
                ", particleDensity=" + particleDensity +
                ", particleType=" + particleType +
                ", location=" + location +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", this.id);
        data.put("name", this.name);
        data.put("repeat-delay", this.repeatDelay);
        data.put("particle-density", this.particleDensity);
        data.put("particle-type", this.particleType.toString());
        data.put("location", this.location.serialize());
        data.put("owner", this.createdBy);

        return data;
    }

    public static PrticlNode deserialize(Map<String, Object> args) {
        return new PrticlNode(
                (int) args.get("id"),
                (String) args.get("name"),
                (int) args.get("repeat-delay"),
                (int) args.get("particle-density"),
                Particle.valueOf((String) args.get("particle-type")),
                (Location) args.get("location"),
                (String) args.get("owner")
        );
    }
}
