package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The most important element of PRTICL - its Nodes.
 * <br>A model class for a PRTICL node.
 */
public class PrticlNode implements ConfigurationSerializable {
    private int Id;

    private int repeatDelay = 20;

    private int particleDensity = 1;
    private org.bukkit.Particle particleType;
    private Location location;
    private Player createdBy;

    public PrticlNode() {

    }

    public PrticlNode(int id, int repeatDelay, int particleDensity, Particle particleType, Location location, Player createdBy) {
        Id = id;
        this.repeatDelay = repeatDelay;
        this.particleDensity = particleDensity;
        this.particleType = particleType;
        this.location = location;
        this.createdBy = createdBy;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Player createdBy) {
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
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    @Override
    public String toString() {
        return "PrticlNode{" +
                "repeatDelay=" + repeatDelay +
                ", particleType=" + particleType +
                ", particleDensity=" + particleDensity +
                ", location=" + location +
                ", creator=" + createdBy +
                '}';
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", this.Id);
        data.put("repeat-delay", this.repeatDelay);
        data.put("particle-density", this.particleDensity);
        data.put("particle-type", this.particleType);
        data.put("location", this.location);
        data.put("owner", this.createdBy);

        return data;
    }

    public static PrticlNode deserialize(Map<String, Object> args) {
        return new PrticlNode(
                (int) args.get("id"),
                (int) args.get("repeat-delay"),
                (int) args.get("particle-density"),
                (Particle) args.get("particle-type"),
                (Location) args.get("location"),
                (Player) args.get("owner")
        );
    }
}
