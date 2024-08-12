package com.minkuh.prticl.common;

import com.minkuh.prticl.data.database.entities.Node;
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