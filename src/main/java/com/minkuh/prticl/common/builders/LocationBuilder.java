package com.minkuh.prticl.common.builders;

import com.minkuh.prticl.data.entities.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationBuilder {
    public static Location fromNode(Node node) {
        return new Location(
                Bukkit.getWorld(node.getWorldUUID()),
                node.getX(),
                node.getY(),
                node.getZ()
        );
    }
}