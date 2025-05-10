package com.minkuh.prticl.data.entities;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Node implements IPrticlEntity {
    private int id;

    private String name;

    private int repeatDelay;

    private int repeatCount;

    private int particleDensity;

    private String particleType;

    private boolean isEnabled;

    private UUID worldUUID;

    private double x;

    private double y;

    private double z;

    private Player player;

    private Set<Trigger> triggers = new HashSet<>();

    private boolean isSpawned;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getParticleDensity() {
        return particleDensity;
    }

    public void setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
    }

    public Particle getParticleType() {
        return Particle.valueOf(particleType);
    }

    public void setParticleType(String particleType) {
        this.particleType = particleType;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public Optional<String> getWorldName() {
        World world = Bukkit.getWorld(this.getWorldUUID());

        if (world == null) {
            return Optional.empty();
        } else {
            return Optional.of(world.getName());
        }
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public void setWorldUUID(UUID worldUUID) {
        this.worldUUID = worldUUID;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Set<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(Set<Trigger> triggers) {
        this.triggers = triggers;
    }

    public void setSpawned(boolean spawned) {
        isSpawned = spawned;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public String getListView() {
        String output = "ID: " + getId() + "; " +
                "Name: " + getName() + ';';

        return output;
    }
}