package com.minkuh.prticl.data.entity_util;

import com.minkuh.prticl.data.entities.Player;

import java.util.UUID;

public class PlayerBuilder {
    private int id;
    private UUID uuid;
    private String username;

    public PlayerBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public PlayerBuilder setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public PlayerBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public Player build() {
        Player player = new Player();

        player.setId(id);
        player.setUUID(uuid);
        player.setUsername(username);

        return player;
    }

    public static Player fromBukkitPlayer(org.bukkit.entity.Player bukkitPlayer) {
        Player player = new Player();

        player.setUsername(bukkitPlayer.getName());
        player.setUUID(bukkitPlayer.getUniqueId());

        return player;
    }
}