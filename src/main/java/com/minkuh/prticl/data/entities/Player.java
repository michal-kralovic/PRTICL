package com.minkuh.prticl.data.entities;

import java.util.UUID;

public class Player implements IPrticlEntity {
    private int id;

    private UUID uuid;

    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getListView() {
        String output = "ID: " + getId() + "; " +
                "Username: " + getUsername() + ';';

        return output;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static Player fromBukkitPlayer(org.bukkit.entity.Player bukkitPlayer) {
        Player player = new Player();

        player.setUsername(bukkitPlayer.getName());
        player.setUUID(bukkitPlayer.getUniqueId());

        return player;
    }
}