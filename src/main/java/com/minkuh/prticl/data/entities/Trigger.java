package com.minkuh.prticl.data.entities;

import com.minkuh.prticl.common.PrticlMessages;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Trigger implements IPrticlEntity {
    private int id;

    private String name;

    private double x;

    private double y;

    private double z;

    private String blockName;

    private UUID worldUUID;

    private Set<Node> nodes = new HashSet<>();

    private Player player;

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

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public void setWorldUUID(UUID worldUUID) {
        this.worldUUID = worldUUID;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
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