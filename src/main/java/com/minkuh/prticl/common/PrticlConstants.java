package com.minkuh.prticl.common;

public class PrticlConstants {

    //
    // Configuration
    //

    public static final String NODE_CHILD = "node";
    public static final String TRIGGER = "trigger";
    public static final String PLAYER = "player";

    //
    // Command names
    //

    public static final String PRTICL_COMMAND = "prticl";
    public static final String CREATE_COMMAND = "create";
    public static final String CREATE_TRIGGER_COMMAND = "create-trigger";
    public static final String HELP_COMMAND = "help";
    public static final String LIST_COMMAND = "list";
    public static final String SPAWN_COMMAND = "spawn";
    public static final String DESPAWN_COMMAND = "despawn";
    public static final String NODE_ADD_COMMAND = "add";

    //
    // Node Model
    //

    public static final String NODE_DEFAULT_NAME = NODE_CHILD;
    public static final String NODE_PARAM_ID = "id";
    public static final String NODE_PARAM_NAME = "name";
    public static final String NODE_PARAM_PARTICLE_DENSITY = "particle-density";
    public static final String NODE_PARAM_REPEAT_DELAY = "repeat-delay";

    //
    // Warning messages
    //

    public static final String HIGH_PARTICLE_DENSITY = "Particle density >25 can lag the server if overused. Proceed with care!";

    //
    // Error messages
    //

    public static final String PLAYER_COMMAND_ONLY = "This command can only be executed by a player!";
    public static final String INCORRECT_COMMAND_SYNTAX_OR_OTHER = "Incorrect command syntax or other error!";
    public static final String DUPLICATE_ENTITY_NAME = "A Prticl entity with this name already exists!";
    public static final String EMPTY_ENTITY_NAME = "Prticl entity name can't be empty!";
    public static final String ENTITY_NAME_TOO_LONG = "Prticl entity name can't be longer than 50 characters!";
    public static final String ENTITY_NAME_UNAVAILABLE = "This name isn't available!";
    public static final String INCORRECT_NODE_ID_FORMAT = "Incorrect node ID format! (expected a valid integer number)";
    public static final String INCORRECT_NUMBER_INPUT_FORMAT = "Incorrect number input format!";
}