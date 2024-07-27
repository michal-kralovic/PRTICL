package com.minkuh.prticl.data.database.queries;

import com.minkuh.prticl.common.PrticlLocationObjectBuilder;
import com.minkuh.prticl.common.PrticlNode;
import com.minkuh.prticl.common.wrappers.PaginatedResult;
import com.minkuh.prticl.data.database.PrticlDbConstants;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PrticlNodeQueries {
    private final PGSimpleDataSource pgDataSource;

    public PrticlNodeQueries(PGSimpleDataSource pgDataSource) {
        this.pgDataSource = pgDataSource;
    }

    private static final String GET_NODES_QUERY =
            "SELECT n.*, l.%s AS \"location_id\", l.%s, l.%s, l.%s, l.%s, p.%s\n" +
                    "FROM %s n\n" +
                    "JOIN %s l ON n.%s = l.%s\n" +
                    "JOIN %s p ON n.%s = p.%s\n" +
                    "ORDER BY n.%s;".formatted(
                            PrticlDbConstants.LOCATION_ID,
                            PrticlDbConstants.LOCATION_WORLD_NAME,
                            PrticlDbConstants.LOCATION_X,
                            PrticlDbConstants.LOCATION_Y,
                            PrticlDbConstants.LOCATION_Z,
                            PrticlDbConstants.PLAYER_USERNAME,
                            PrticlDbConstants.NODES_TABLE,
                            PrticlDbConstants.LOCATIONS_TABLE,
                            PrticlDbConstants.LOCATION_ID,
                            PrticlDbConstants.LOCATION_ID,
                            PrticlDbConstants.PLAYERS_TABLE,
                            PrticlDbConstants.PLAYER_ID,
                            PrticlDbConstants.PLAYER_ID,
                            PrticlDbConstants.NODE_ID
                    );


    public List<PrticlNode> getNodes() throws SQLException {
        List<PrticlNode> output = new ArrayList<>();

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(GET_NODES_QUERY); ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                PrticlNode node = deserializeNodeFromResultSet(rs);
                output.add(node);
            }
        }

        return output;
    }

    private static final String GET_NODES_BY_PAGE_QUERY =
            "SELECT n.*, l.id AS \"location_id\", l.world_name, l.world_id, l.x, l.y, l.z, p.username\n" +
                    "FROM nodes n\n" +
                    "JOIN locations l ON n.location_id = l.id\n" +
                    "JOIN players p ON n.player_id = p.id\n" +
                    "ORDER BY n.id\n" +
                    "LIMIT 10\n" +
                    "OFFSET ?";


    public PaginatedResult<PrticlNode> getNodesByPage(int page) throws SQLException {
        List<PrticlNode> output = new ArrayList<>();

        int pageStart = (page == 0) ? 0 : ((page * 10) - 10);

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(GET_NODES_BY_PAGE_QUERY)) {
            statement.setInt(1, pageStart);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    PrticlNode node = PrticlNode.deserialize(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("repeat_delay"),
                            rs.getInt("particle_density"),
                            rs.getBoolean("is_enabled"),
                            rs.getString("particle_type"),
                            new PrticlLocationObjectBuilder().withId(rs.getInt("location_id")).withLocation(
                                    new Location(
                                            Bukkit.getWorld(rs.getObject("world_id", UUID.class)),
                                            rs.getDouble("x"),
                                            rs.getDouble("y"),
                                            rs.getDouble("z")
                                    )
                            ).build(),
                            rs.getString("name"));

                    output.add(node);
                }
            }

            final double PAGE_SIZE = 10.0;
            int totalPages = (int) Math.ceil(getTotalNodesCount() / PAGE_SIZE);
            return new PaginatedResult<>(output, page, totalPages);
        }
    }

    private static final String GET_NODES_BY_PAGE_BY_PLAYER_QUERY =
            "SELECT n.*, l.%s AS \"location_id\", l.%s, l.%s, l.%s, l.%s, p.%s\n" +
                    "FROM %s n\n" +
                    "JOIN %s l ON n.%s = l.%s\n" +
                    "JOIN %s p ON n.%s = p.%s\n" +
                    "WHERE p.%s = ?\n" +
                    "ORDER BY n.%s\n" +
                    "LIMIT 10\n" +
                    "OFFSET ?;".formatted(
                            PrticlDbConstants.LOCATION_ID,
                            PrticlDbConstants.LOCATION_WORLD_NAME,
                            PrticlDbConstants.LOCATION_X,
                            PrticlDbConstants.LOCATION_Y,
                            PrticlDbConstants.LOCATION_Z,
                            PrticlDbConstants.PLAYER_USERNAME,
                            PrticlDbConstants.NODES_TABLE,
                            PrticlDbConstants.LOCATIONS_TABLE,
                            PrticlDbConstants.LOCATION_ID,
                            PrticlDbConstants.LOCATION_ID,
                            PrticlDbConstants.PLAYERS_TABLE,
                            PrticlDbConstants.PLAYER_ID,
                            PrticlDbConstants.PLAYER_UUID,
                            PrticlDbConstants.NODE_ID
                    );

    public PaginatedResult<PrticlNode> getNodesByPageByPlayer(int page, UUID playerUUID) throws SQLException {
        List<PrticlNode> output = new ArrayList<>();

        // if page is 1, return 0 (start of page 1, which is 0 - 10). If it's, e.g. 3, return 20 (start of page 3), etc.
        int pageStart = (page == 1) ? 0 : ((page * 10) - 10);

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(GET_NODES_BY_PAGE_BY_PLAYER_QUERY)) {
            statement.setObject(1, playerUUID);
            statement.setInt(2, pageStart);
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    PrticlNode node = PrticlNode.deserialize(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("repeat_delay"),
                            rs.getInt("particle_density"),
                            rs.getBoolean("is_enabled"),
                            rs.getString("particle_type"),
                            new PrticlLocationObjectBuilder().withId(rs.getInt("location_id")).withLocation(
                                    new Location(
                                            Bukkit.getWorld(rs.getObject("world_id", UUID.class)),
                                            rs.getDouble("x"),
                                            rs.getDouble("y"),
                                            rs.getDouble("z")
                                    )
                            ).build(),
                            rs.getString("name"));

                    output.add(node);
                }
            }

            int totalPages = (int) Math.ceil(getTotalNodesCountByPlayer(playerUUID.toString()) / 10.0);
            return new PaginatedResult<>(output, page, totalPages);
        }
    }

    private static final String CREATE_NODE_NEW_QUERY = ("INSERT INTO nodes (%s, %s, %s, %s, %s, %s, %s)\n " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)").formatted(
                    PrticlDbConstants.NODE_NAME,
                    PrticlDbConstants.NODE_REPEAT_DELAY,
                    PrticlDbConstants.NODE_PARTICLE_DENSITY,
                    PrticlDbConstants.NODE_PARTICLE_TYPE,
                    PrticlDbConstants.NODE_IS_ENABLED,
                    PrticlDbConstants.NODE_LOCATION_ID,
                    PrticlDbConstants.NODE_PLAYER_ID);

    public boolean createNode(String name,
                              int repeatDelay,
                              int particleDensity,
                              String particleType,
                              boolean isEnabled,
                              int locationId,
                              int playerId) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(CREATE_NODE_NEW_QUERY)) {
            statement.setString(1, name);
            statement.setInt(2, repeatDelay);
            statement.setInt(3, particleDensity);
            statement.setString(4, particleType);
            statement.setBoolean(5, isEnabled);
            statement.setInt(6, locationId);
            statement.setInt(7, playerId);

            return statement.executeUpdate() == 1;
        }
    }

    public PrticlNode getNodeById(int nodeId) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(
                getAllNodes("WHERE %s.%s = ?".formatted(PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_ID))
        )) {
            statement.setInt(1, nodeId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return deserializeNodeFromResultSet(rs);
                else
                    throw new SQLException("Couldn't obtain the node!");
            }
        }
    }

    public PrticlNode getNodeByName(String nodeName) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(
                getAllNodes("WHERE %s.%s = ?".formatted(PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_NAME))
        )) {
            statement.setString(1, nodeName);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return deserializeNodeFromResultSet(rs);
                else
                    throw new SQLException("Couldn't obtain the node!");
            }
        }
    }

    public List<String> getNodeNamesList() throws SQLException {
        List<String> nodesNamesList = new ArrayList<>();

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement("SELECT name FROM nodes")) {
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    nodesNamesList.add(rs.getString("name"));
                }
            }

            return nodesNamesList;
        }
    }

    public List<PrticlNode> getNodesByWorld(World world) throws SQLException {
        List<PrticlNode> nodesList = new ArrayList<>();

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(
                getAllNodes("WHERE %s.%s = ?".formatted(PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_WORLD_ID))
        )) {
            statement.setObject(1, world.getUID());

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    nodesList.add(deserializeNodeFromResultSet(rs));
                }
            }

            return nodesList;
        }
    }

    public boolean chunkHasNodes(Chunk chunk) throws SQLException {
        try (Connection connection = pgDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     getAllNodes("WHERE %s.%s = ?".formatted(PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_WORLD_ID))
             )) {
            statement.setObject(1, chunk.getWorld().getUID());
            int counter;

            try (ResultSet rs = statement.executeQuery()) {

                counter = 0;
                while (rs.next()) {
                    Location nodeLocation = new Location(
                            Bukkit.getWorld(rs.getObject("world_id", UUID.class)),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z")
                    );

                    if (chunk.getX() == nodeLocation.getChunk().getX() && chunk.getZ() == nodeLocation.getChunk().getZ())
                        counter++;
                }
                return counter > 0;
            }
        }
    }

    private static final String GET_NODES_BY_CHUNK_QUERY =
            ("SELECT %s.*, \n" +
                    "%s.%s AS location_pk, %s.%s, %s.%s, %s.%s, %s.%s,\n" +
                    "%s.%s AS player_pk, %s.%s, %s.%s\n" +
                    "FROM %s\n" +
                    "JOIN %s ON %s.%s = %s.%s\n" +
                    "JOIN %s ON %s.%s = %s.%s\n" +
                    "WHERE %s.%s = ?").formatted(
                    PrticlDbConstants.NODES_TABLE,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_ID,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_X,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_Y,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_Z,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_WORLD_ID,
                    PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_ID,
                    PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_UUID,
                    PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_USERNAME,
                    PrticlDbConstants.NODES_TABLE,
                    PrticlDbConstants.LOCATIONS_TABLE,
                    PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_LOCATION_ID,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_ID,
                    PrticlDbConstants.PLAYERS_TABLE,
                    PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_PLAYER_ID,
                    PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_ID,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_WORLD_ID
            );

    public List<PrticlNode> getNodesListByChunk(Chunk chunk) throws SQLException {
        try (Connection connection = pgDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_NODES_BY_CHUNK_QUERY)) {
            statement.setObject(1, chunk.getWorld().getUID());

            List<PrticlNode> nodesList;
            try (ResultSet rs = statement.executeQuery()) {

                nodesList = new ArrayList<>();
                while (rs.next()) {
                    Location nodeLocation = new Location(
                            Bukkit.getWorld(rs.getObject("world_id", UUID.class)),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z")
                    );

                    if (chunk.getX() != nodeLocation.getChunk().getX() || chunk.getZ() != nodeLocation.getChunk().getZ())
                        continue;

                    nodesList.add(deserializeNodeFromResultSet(rs));
                }
            }

            return nodesList;
        }
    }

    public List<PrticlNode> getNodesByCoordinates(int x, int z, World world) throws SQLException {
        List<PrticlNode> nodesList = new ArrayList<>();

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(GET_NODES_BY_CHUNK_QUERY)) {
            statement.setString(1, world.getName());

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int nodeX = (int) rs.getDouble("x") >> 4;
                    int nodeZ = (int) rs.getDouble("z") >> 4;

                    if (!(x <= nodeX && nodeX < x + 16) || !(z <= nodeZ && nodeZ < z + 16))
                        continue;

                    nodesList.add(deserializeNodeFromResultSet(rs));
                }
            }

            return nodesList;
        }
    }

    private static final String GET_ENABLED_NODES =
            ("SELECT %s.*, \n" +
                    "%s.%s AS \"location_pk\", %s.%s, %s.%s, %s.%s, %s.%s,\n" +
                    "%s.%s AS player_pk, %s.%s, %s.%s\n" +
                    "FROM %s\n" +
                    "JOIN %s ON %s.%s = %s.%s\n" +
                    "JOIN %s ON %s.%s = %s.%s\n" +
                    "WHERE %s.%s = TRUE;").formatted(
                    PrticlDbConstants.NODES_TABLE,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_ID,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_X,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_Y,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_Z,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_WORLD_ID,
                    PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_ID,
                    PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_UUID,
                    PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_USERNAME,
                    PrticlDbConstants.NODES_TABLE,
                    PrticlDbConstants.LOCATIONS_TABLE,
                    PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_LOCATION_ID,
                    PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_ID,
                    PrticlDbConstants.PLAYERS_TABLE,
                    PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_PLAYER_ID,
                    PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_ID,
                    PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_IS_ENABLED
            );

    public List<PrticlNode> getEnabledNodes() throws SQLException {
        var enabledNodes = new ArrayList<PrticlNode>();

        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(GET_ENABLED_NODES)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    var node = deserializeNodeFromResultSet(rs);
                    Bukkit.getLogger().log(Level.FINE, "Spawning: " + node.getName() + '(' + node.getId() + ')');
                    enabledNodes.add(node);
                }
            }
        }

        return enabledNodes;
    }

    private static final String SET_ENABLED_QUERY =
            "UPDATE %s SET %s = ? WHERE %s = ?;".formatted(
                    PrticlDbConstants.NODES_TABLE,
                    PrticlDbConstants.NODE_IS_ENABLED,
                    PrticlDbConstants.NODE_ID
            );

    public boolean setEnabled(PrticlNode node, boolean state) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(SET_ENABLED_QUERY)) {
            statement.setBoolean(1, state);
            statement.setInt(2, node.getId());

            return statement.executeUpdate() == 1;
        }
    }

    private static final String IS_NODE_NAME_TAKEN_QUERY =
            "SELECT 1 FROM %s n WHERE n.%s = ?".formatted(
                    PrticlDbConstants.NODES_TABLE,
                    PrticlDbConstants.NODE_NAME
            );

    public boolean isNodeNameTaken(String nodeName) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(IS_NODE_NAME_TAKEN_QUERY)) {
            statement.setString(1, nodeName);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return rs.getInt(1) == 1;
                else return false;
            }
        }
    }

    private static final String GET_TOTAL_NODES_COUNT_QUERY =
            "SELECT COUNT(%s) FROM %s".formatted(
                    PrticlDbConstants.NODE_ID,
                    PrticlDbConstants.NODES_TABLE
            );

    private int getTotalNodesCount() throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(GET_TOTAL_NODES_COUNT_QUERY)) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
                else return 0;
            }
        }
    }

    private static final String GET_TOTAL_NODES_COUNT_BY_PLAYER_QUERY =
            "SELECT COUNT(%s) FROM %s JOIN %s p ON %s.%s = p.%s WHERE p.%s = ?".formatted(
                    PrticlDbConstants.NODE_ID,
                    PrticlDbConstants.NODES_TABLE,
                    PrticlDbConstants.PLAYERS_TABLE,
                    PrticlDbConstants.NODES_TABLE,
                    PrticlDbConstants.NODE_PLAYER_ID,
                    PrticlDbConstants.PLAYER_ID,
                    PrticlDbConstants.PLAYER_UUID
            );

    private int getTotalNodesCountByPlayer(String playerUUID) throws SQLException {
        try (PreparedStatement statement = pgDataSource.getConnection().prepareStatement(GET_TOTAL_NODES_COUNT_BY_PLAYER_QUERY)) {
            statement.setString(1, playerUUID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
                else return 0;
            }
        }
    }

    //
    // Utils
    //

    private synchronized String getAllNodes(String condition) {
        return ("SELECT %s.*, \n" +
                "%s.%s AS \"location_pk\", %s.%s, %s.%s, %s.%s, %s.%s,\n" +
                "%s.%s AS player_pk, %s.%s, %s.%s\n" +
                "FROM %s\n" +
                "JOIN %s ON %s.%s = %s.%s\n" +
                "JOIN %s ON %s.%s = %s.%s\n").formatted(
                PrticlDbConstants.NODES_TABLE,
                PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_ID,
                PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_X,
                PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_Y,
                PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_Z,
                PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_WORLD_ID,
                PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_ID,
                PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_UUID,
                PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_USERNAME,
                PrticlDbConstants.NODES_TABLE,
                PrticlDbConstants.LOCATIONS_TABLE,
                PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_LOCATION_ID,
                PrticlDbConstants.LOCATIONS_TABLE, PrticlDbConstants.LOCATION_ID,
                PrticlDbConstants.PLAYERS_TABLE,
                PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_PLAYER_ID,
                PrticlDbConstants.PLAYERS_TABLE, PrticlDbConstants.PLAYER_ID,
                PrticlDbConstants.NODES_TABLE, PrticlDbConstants.NODE_IS_ENABLED
        ) + (condition == null || condition.isBlank() ? ';' : " " + condition + ';');
    }

    private synchronized PrticlNode deserializeNodeFromResultSet(ResultSet rs) throws SQLException {
        return PrticlNode.deserialize(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("repeat_delay"),
                rs.getInt("particle_density"),
                rs.getBoolean("is_enabled"),
                rs.getString("particle_type"),
                new PrticlLocationObjectBuilder()
                        .withId(rs.getInt("location_pk"))
                        .withLocation(
                                new Location(
                                        Bukkit.getWorld(rs.getObject("world_id", UUID.class)),
                                        rs.getDouble("x"),
                                        rs.getDouble("y"),
                                        rs.getDouble("z")
                                )
                        ).build(),
                rs.getString("username")
        );
    }
}