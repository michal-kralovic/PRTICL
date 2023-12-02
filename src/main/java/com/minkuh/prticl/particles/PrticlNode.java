package com.minkuh.prticl.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class PrticlNode {
    private int repeatDelay = 20;
    private org.bukkit.Particle particleType;
    private Location location;

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    private Player creator;

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

    @Override
    public String toString() {
        return "Particle Node{" +
                "repeatDelay: " + repeatDelay +
                "\n particleType: " + particleType +
                "\n location: " + location +
                "\n creator: " + creator +
                '}';
    }
}
