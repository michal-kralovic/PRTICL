package com.minkuh.prticl.data.entity_util;

import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.entities.Player;
import org.bukkit.Location;

import java.util.UUID;

public class NodeBuilder {
    private int id;
    private String name;
    private int repeatDelay;
    private int particleDensity;
    private String particleType;
    private boolean isEnabled;
    private String worldName;
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

    public NodeBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public NodeBuilder setLocation(String worldName, UUID worldUUID, Location location) {
        this.worldName = worldName;
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
        node.setParticleDensity(particleDensity);
        node.setParticleType(particleType);
        node.setEnabled(isEnabled);
        node.setPlayer(player);
        node.setWorldUUID(worldUUID);
        node.setWorldName(worldName);
        node.setX(x);
        node.setY(y);
        node.setZ(z);
        return node;
    }
}