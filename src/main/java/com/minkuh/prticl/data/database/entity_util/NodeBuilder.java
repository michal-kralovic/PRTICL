package com.minkuh.prticl.data.database.entity_util;

import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entities.Player;
import org.bukkit.Location;

import java.util.UUID;

public class NodeBuilder {
    private int id;
    private String name;
    private int repeatDelay;
    private int repeatCount;
    private int particleDensity;
    private String particleType;
    private boolean isEnabled;
    private boolean isSpawned;
    private UUID worldUUID;
    private double x;
    private double y;
    private double z;
    private Player player;

    public NodeBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public NodeBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public NodeBuilder setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
        return this;
    }

    public NodeBuilder setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }

    public NodeBuilder setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
        return this;
    }

    public NodeBuilder setParticleType(String particleType) {
        this.particleType = particleType;
        return this;
    }

    public NodeBuilder setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    public NodeBuilder setSpawned(boolean isSpawned) {
        this.isSpawned = isSpawned;
        return this;
    }

    public NodeBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public NodeBuilder setLocation(UUID worldUUID, double x, double y, double z) {
        this.worldUUID = worldUUID;
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public NodeBuilder setLocation(UUID worldUUID, Location location) {
        this.worldUUID = worldUUID;
        this.x = location.x();
        this.y = location.y();
        this.z = location.z();

        return this;
    }

    public Node build() {
        Node node = new Node();
        node.setId(id);
        node.setName(name);
        node.setRepeatDelay(repeatDelay);
        node.setRepeatCount(repeatCount);
        node.setParticleDensity(particleDensity);
        node.setParticleType(particleType);
        node.setEnabled(isEnabled);
        node.setSpawned(isSpawned);
        node.setPlayer(player);
        node.setWorldUUID(worldUUID);
        node.setX(x);
        node.setY(y);
        node.setZ(z);
        return node;
    }
}