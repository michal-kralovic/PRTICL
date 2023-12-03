package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * The most important element of PRTICL - its Nodes.
 * <br>A model class for a PRTICL node.
 */
public class PrticlNode {
    private int Id;
    private int repeatDelay = 20;
    private int particleDensity;
    private org.bukkit.Particle particleType;
    private Location location;
    private Player createdBy;

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
}
