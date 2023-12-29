package com.minkuh.prticl.systemutil.resources;

public class PrticlStrings {

    //
    // Configuration
    //

    public static final String NODE_CONFIGURATION_SECTION = "nodes";
    public static final String NODE_CHILD = "node";

    //
    // Command names
    //

    public static final String PRTICL_COMMAND = "prticl";
    public static final String CREATE_COMMAND = "create";
    public static final String LINE_COMMAND = "line";
    public static final String LIST_COMMAND = "list";
    public static final String SPAWN_COMMAND = "spawn";

    //
    // Node Model
    //

    public static final String NODE_DEFAULT_NAME = NODE_CHILD;
    public static final String NODE_PARAM_ID = "id";
    public static final String NODE_PARAM_OWNER = "owner";
    public static final String NODE_PARAM_NAME = "name";
    public static final String NODE_PARAM_PARTICLE_TYPE = "particle-type";
    public static final String NODE_PARAM_PARTICLE_DENSITY = "particle-density";
    public static final String NODE_PARAM_LOCATION = "location";
    public static final String NODE_PARAM_REPEAT_DELAY = "repeat-delay";

    //
    // Error messages
    //

    public static final String FAILED_SAVE_TO_CONFIG = "Couldn't save the node to config!";
    public static final String CONFIG_SECTION_NOT_FOUND = "Couldn't find the configuration file!";
    public static final String CREATED_NODE = "Created the node.";
    public static final String MISMATCHING_WORLDS = "Points cannot be in different worlds!";
    public static final String PLAYER_COMMAND_ONLY = "This command can only be executed by a player!";
    public static final String INCORRECT_COORDINATES_INPUT = "Incorrect coordinates!";
    public static final String INCORRECT_PAGE_INPUT = "Incorrect page number!";
    public static final String INCORRECT_COMMAND_SYNTAX_OR_OTHER = "Incorrect command syntax or other error!";
    public static final String NODE_NOT_FOUND = "The specified node couldn't be found!";
}
