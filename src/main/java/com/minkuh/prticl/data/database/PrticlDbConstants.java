package com.minkuh.prticl.data.database;

public class PrticlDbConstants {
    // Table Names
    public static final String LOCATIONS_TABLE = "locations";
    public static final String PLAYERS_TABLE = "players";
    public static final String NODES_TABLE = "nodes";

    // Table Columns
    // Prticl Locations
    public static final String LOCATION_ID = "id";
    public static final String LOCATION_X = "x";
    public static final String LOCATION_Y = "y";
    public static final String LOCATION_Z = "z";
    public static final String LOCATION_WORLD_NAME = "world_name";
    public static final String LOCATION_WORLD_ID = "world_id";

    // Prticl Players
    public static final String PLAYER_ID = "id";
    public static final String PLAYER_UUID = "uuid";
    public static final String PLAYER_USERNAME = "username";

    // Prticl Nodes
    public static final String NODE_ID = "id";
    public static final String NODE_NAME = "name";
    public static final String NODE_REPEAT_DELAY = "repeat_delay";
    public static final String NODE_PARTICLE_DENSITY = "particle_density";
    public static final String NODE_PARTICLE_TYPE = "particle_type";
    public static final String NODE_IS_ENABLED = "is_enabled";
    public static final String NODE_LOCATION_ID = "location_id";
    public static final String NODE_PLAYER_ID = "player_id";
}