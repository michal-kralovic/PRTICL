package com.minkuh.prticl.data.entity_util;

import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.entities.Player;
import com.minkuh.prticl.data.entities.Trigger;

import java.util.List;

public class TriggerBuilder {
    private int id;
    private String name;
    private double x;
    private double y;
    private double z;
    private String blockName;
    private List<Node> nodes;
    private Player player;

    public TriggerBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public TriggerBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TriggerBuilder setX(double x) {
        this.x = x;
        return this;
    }

    public TriggerBuilder setY(double y) {
        this.y = y;
        return this;
    }

    public TriggerBuilder setZ(int z) {
        this.z = z;
        return this;
    }

    public TriggerBuilder setBlockName(String blockName) {
        this.blockName = blockName;
        return this;
    }

    public TriggerBuilder setNodes(List<Node> nodes) {
        this.nodes = nodes;
        return this;
    }

    public TriggerBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public Trigger build() {
        Trigger trigger = new Trigger();

        trigger.setId(id);
        trigger.setName(name);
        trigger.setX(x);
        trigger.setY(y);
        trigger.setZ(z);
        trigger.setBlockName(blockName);
        trigger.setNodes(nodes);
        trigger.setPlayer(player);

        return trigger;
    }
}