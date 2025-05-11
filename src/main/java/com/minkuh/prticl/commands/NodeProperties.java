package com.minkuh.prticl.commands;

public enum NodeProperties {
    NAME(true),
    REPEAT_DELAY(true),
    REPEAT_COUNT(true),
    PARTICLE_DENSITY(true),
    PARTICLE_TYPE(true),
    IS_ENABLED(false),
    IS_SPAWNED(false),
    WORLD_UUID(false),
    X(true),
    Y(true),
    Z(true),
    PLAYER_ID(false);

    private final boolean playerEditable;

    NodeProperties(boolean playerEditable) {
        this.playerEditable = playerEditable;
    }

    public boolean isPlayerEditable() {
        return playerEditable;
    }
}