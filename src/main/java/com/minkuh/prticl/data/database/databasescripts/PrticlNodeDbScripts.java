package com.minkuh.prticl.data.database.databasescripts;

import com.minkuh.prticl.data.wrappers.PaginatedResult;
import com.minkuh.prticl.nodes.prticl.PrticlLocationObjectBuilder;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.prticl.PrticlNodeBuilder;
import org.bukkit.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PrticlNodeDbScripts {
    private final Connection connection;

    public PrticlNodeDbScripts(Connection connection) {
        this.connection = connection;
    }

    private static final String GET_NODES_BY_PAGE_QUERY =
            "SELECT n.*, l.id AS 'location_id', l.world, l.x, l.y, l.z, p.username\n" +
                    "FROM nodes n\n" +
                    "JOIN locations l ON n.location_id = l.id\n" +
                    "JOIN players p ON n.player_id = p.id\n" +
                    "ORDER BY n.id\n" +
                    "LIMIT 10\n" +
                    "OFFSET ?;";

    public PaginatedResult<PrticlNode> getNodesByPage(int page) throws SQLException {
        List<PrticlNode> output = new ArrayList<>();

        // if page is 1, return 0 (start of page 1, which is 0 - 10). If it's, e.g. 3, return 20 (start of page 3), etc.
        int pageStart = (page == 1) ? 0 : ((page * 10) - 10);

        try (PreparedStatement statement = connection.prepareStatement(GET_NODES_BY_PAGE_QUERY)) {
            statement.setInt(1, pageStart);
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    PrticlNode node = PrticlNode.deserialize(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("repeat_delay"),
                            rs.getInt("particle_density"),
                            rs.getString("particle_type"),
                            new PrticlLocationObjectBuilder().withId(rs.getInt("location_id")).withLocation(
                                    new Location(
                                            Bukkit.getWorld(rs.getString("world")),
                                            rs.getDouble("x"),
                                            rs.getDouble("y"),
                                            rs.getDouble("z")
                                    )
                            ).build(),
                            rs.getString("name"));

                    output.add(node);
                }
            }

            int totalPages = getTotalNodesCount();
            return new PaginatedResult<>(output, page, totalPages);
        }
    }

    private static final String GET_NODES_BY_PAGE_BY_PLAYER_QUERY =
            "SELECT n.*, l.id AS 'location_id', l.world, l.x, l.y, l.z, p.username\n" +
                    "FROM nodes n\n" +
                    "JOIN locations l ON n.location_id = l.id\n" +
                    "JOIN players p ON n.player_id = p.id\n" +
                    "WHERE p.uuid = '?'\n" +
                    "ORDER BY n.id\n" +
                    "LIMIT 10\n" +
                    "OFFSET ?;";

    public PaginatedResult<PrticlNode> getNodesByPageByPlayer(int page, UUID playerUUID) throws SQLException {
        List<PrticlNode> output = new ArrayList<>();

        // if page is 1, return 0 (start of page 1, which is 0 - 10). If it's, e.g. 3, return 20 (start of page 3), etc.
        int pageStart = (page == 1) ? 0 : ((page * 10) - 10);

        try (PreparedStatement statement = connection.prepareStatement(GET_NODES_BY_PAGE_BY_PLAYER_QUERY)) {
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, pageStart);
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    PrticlNode node = PrticlNode.deserialize(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("repeat_delay"),
                            rs.getInt("particle_density"),
                            rs.getString("particle_type"),
                            new PrticlLocationObjectBuilder().withId(rs.getInt("location_id")).withLocation(
                                    new Location(
                                            Bukkit.getWorld(rs.getString("world")),
                                            rs.getDouble("x"),
                                            rs.getDouble("y"),
                                            rs.getDouble("z")
                                    )
                            ).build(),
                            rs.getString("name"));

                    output.add(node);
                }
            }

            int totalPages = getTotalNodesCountByPlayer(playerUUID.toString());
            return new PaginatedResult<>(output, page, totalPages);
        }
    }

    private static final String CREATE_NODE_QUERY = "INSERT INTO nodes (name, repeat_delay, particle_density, particle_type, location_id, player_id) VALUES (?, ?, ?, ?, ?, ?)";

    public boolean createNode(String name, int repeatDelay, int particleDensity, String particleType, int locationId, int playerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_NODE_QUERY)) {
            statement.setString(1, name);
            statement.setInt(2, repeatDelay);
            statement.setInt(3, particleDensity);
            statement.setString(4, particleType);
            statement.setInt(5, locationId);
            statement.setInt(6, playerId);
            return statement.executeUpdate() == 1;
        }
    }

    private static final String CREATE_NODE_NEW_QUERY = "INSERT INTO nodes (name, repeat_delay, particle_density, particle_type, location_id, is_enabled, is_disabled, player_id) VALUES (?, ?, ?, ?, ?, ?)";

    public boolean createNode(String name, int repeatDelay, int particleDensity, String particleType, int locationId, boolean isEnabled, boolean isVisible, int playerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_NODE_NEW_QUERY)) {
            statement.setString(1, name);
            statement.setInt(2, repeatDelay);
            statement.setInt(3, particleDensity);
            statement.setString(4, particleType);
            statement.setInt(5, locationId);
            statement.setInt(6, playerId);
            return statement.executeUpdate() == 1;
        }
    }

    private static final String GET_NODE_BY_ID_QUERY = "SELECT nodes.*, locations.id AS 'location_pk', locations.x, locations.y, locations.z, locations.world\n" +
            "FROM nodes\n" +
            "JOIN locations ON nodes.location_id = locations.id\n" +
            "WHERE nodes.id = ?";

    public PrticlNode getNodeById(int nodeId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_NODE_BY_ID_QUERY)) {
            statement.setInt(1, nodeId);

            try (ResultSet rs = statement.executeQuery()) {
                return new PrticlNodeBuilder()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setRepeatDelay(rs.getInt("repeat_delay"))
                        .setParticleDensity(rs.getInt("particle_density"))
                        .setParticleType(Enum.valueOf(Particle.class, rs.getString("particle_type")))
                        .setLocationObject(
                                new PrticlLocationObjectBuilder()
                                        .withId(rs.getInt("location_pk"))
                                        .withLocation(
                                                new Location(
                                                        Bukkit.getServer().getWorld(rs.getString("world")),
                                                        rs.getDouble("x"),
                                                        rs.getDouble("y"),
                                                        rs.getDouble("z")
                                                )
                                        ).build()
                        ).build();
            }
        }
    }

    private static final String GET_NODE_BY_NAME_QUERY = "SELECT nodes.*, locations.id AS 'location_pk', locations.x, locations.y, locations.z, locations.world\n" +
            "FROM nodes\n" +
            "JOIN locations ON nodes.location_id = locations.id\n" +
            "WHERE nodes.name = ?";

    public PrticlNode getNodeByName(String nodeName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_NODE_BY_NAME_QUERY)) {
            statement.setString(1, nodeName);

            try (ResultSet rs = statement.executeQuery()) {
                return new PrticlNodeBuilder()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setRepeatDelay(rs.getInt("repeat_delay"))
                        .setParticleDensity(rs.getInt("particle_density"))
                        .setParticleType(Enum.valueOf(Particle.class, rs.getString("particle_type")))
                        .setLocationObject(
                                new PrticlLocationObjectBuilder()
                                        .withId(rs.getInt("location_pk"))
                                        .withLocation(
                                                new Location(
                                                        Bukkit.getWorld(rs.getString("world")),
                                                        rs.getDouble("x"),
                                                        rs.getDouble("y"),
                                                        rs.getDouble("z")
                                                )
                                        ).build()
                        ).build();
            }
        }
    }

    public List<String> getNodeNamesList() throws SQLException {
        List<String> nodesNamesList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM nodes")) {
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    nodesNamesList.add(rs.getString("name"));
                }
            }

            return nodesNamesList;
        }
    }

    private static final String GET_NODES_BY_WORLD_QUERY = "SELECT nodes.*, \n" +
            "locations.id AS location_pk, locations.x, locations.y, locations.z, locations.world,\n" +
            "players.id AS player_pk, players.uuid, players.username\n" +
            "FROM nodes\n" +
            "JOIN locations ON nodes.location_id = locations.id\n" +
            "JOIN players ON nodes.player_id = players.id\n" +
            "WHERE locations.world = ?;\n";

    public List<PrticlNode> getNodesByWorld(World world) throws SQLException {
        List<PrticlNode> nodesList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(GET_NODES_BY_WORLD_QUERY)) {
            statement.setString(1, world.getName());

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    nodesList.add(
                            PrticlNode.deserialize(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getInt("repeat_delay"),
                                    rs.getInt("particle_density"),
                                    rs.getString("particle_type"),
                                    new PrticlLocationObjectBuilder()
                                            .withId(rs.getInt("location_pk"))
                                            .withLocation(
                                                    new Location(
                                                            Bukkit.getWorld(rs.getString("world")),
                                                            rs.getDouble("x"),
                                                            rs.getDouble("y"),
                                                            rs.getDouble("z")
                                                    )
                                            ).build(),
                                    rs.getString("username")
                            )
                    );
                }
            }

            return nodesList;
        }
    }

    private static final String CHUNK_HAS_NODES_QUERY = "SELECT 1, \n" +
            "locations.id AS location_pk, locations.x, locations.y, locations.z, locations.world,\n" +
            "players.id AS player_pk\n" +
            "FROM nodes\n" +
            "JOIN locations ON nodes.location_id = locations.id\n" +
            "JOIN players ON nodes.player_id = players.id\n" +
            "WHERE locations.world = ?";

    public boolean chunkHasNodes(Chunk chunk) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CHUNK_HAS_NODES_QUERY)) {
            statement.setString(1, chunk.getWorld().getName());

            int counter;
            try (ResultSet rs = statement.executeQuery()) {

                counter = 0;
                while (rs.next()) {
                    Location nodeLocation = new Location(
                            Bukkit.getWorld(rs.getString("world")),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z")
                    );

                    if (chunk.getX() == nodeLocation.getChunk().getX() && chunk.getZ() == nodeLocation.getChunk().getZ())
                        counter++;
                }
            }
            return counter > 0;
        }
    }

    private static final String GET_NODES_BY_CHUNK_QUERY = "SELECT nodes.*, \n" +
            "locations.id AS location_pk, locations.x, locations.y, locations.z, locations.world,\n" +
            "players.id AS player_pk, players.uuid, players.username\n" +
            "FROM nodes\n" +
            "JOIN locations ON nodes.location_id = locations.id\n" +
            "JOIN players ON nodes.player_id = players.id\n" +
            "WHERE locations.world = ?";

    public List<PrticlNode> getNodesListByChunk(Chunk chunk) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_NODES_BY_CHUNK_QUERY)) {
            statement.setString(1, chunk.getWorld().getName());

            List<PrticlNode> nodesList;
            try (ResultSet rs = statement.executeQuery()) {

                nodesList = new ArrayList<>();
                while (rs.next()) {
                    Location nodeLocation = new Location(
                            Bukkit.getWorld(rs.getString("world")),
                            rs.getDouble("x"),
                            rs.getDouble("y"),
                            rs.getDouble("z")
                    );

                    if (chunk.getX() != nodeLocation.getChunk().getX() || chunk.getZ() != nodeLocation.getChunk().getZ())
                        continue;

                    nodesList.add(
                            PrticlNode.deserialize(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getInt("repeat_delay"),
                                    rs.getInt("particle_density"),
                                    rs.getString("particle_type"),
                                    new PrticlLocationObjectBuilder()
                                            .withId(rs.getInt("location_pk"))
                                            .withLocation(nodeLocation).build(),
                                    rs.getString("username")
                            )
                    );
                }
            }
            System.out.println(Arrays.toString(nodesList.toArray()));
            return nodesList;
        }
    }

    public List<PrticlNode> getNodesByCoordinates(int x, int z, World world) throws SQLException {
        List<PrticlNode> nodesList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(GET_NODES_BY_CHUNK_QUERY)) {
            statement.setString(1, world.getName());

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int nodeX = (int) rs.getDouble("x") >> 4;
                    int nodeZ = (int) rs.getDouble("z") >> 4;

                    if (!(x <= nodeX && nodeX < x + 16) || !(z <= nodeZ && nodeZ < z + 16))
                        continue;

                    nodesList.add(
                            PrticlNode.deserialize(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getInt("repeat_delay"),
                                    rs.getInt("particle_density"),
                                    rs.getString("particle_type"),
                                    new PrticlLocationObjectBuilder()
                                            .withId(rs.getInt("location_pk"))
                                            .withLocation(
                                                    new Location(
                                                            Bukkit.getWorld(rs.getString("world")),
                                                            rs.getDouble("x"),
                                                            rs.getDouble("y"),
                                                            rs.getDouble("z")
                                                    )
                                            ).build(),
                                    rs.getString("username")
                            )
                    );
                }
            }

            return nodesList;
        }
    }

    private static final String GET_TOTAL_NODES_COUNT_QUERY = "SELECT COUNT(id) FROM nodes";
    private int getTotalNodesCount() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_TOTAL_NODES_COUNT_QUERY)) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
                else return 0;
            }
        }
    }

    private static final String GET_TOTAL_NODES_COUNT_BY_PLAYER_QUERY = "SELECT COUNT(id) FROM nodes JOIN players p ON nodes.player_id = players.id WHERE p.uuid = '?'";
    private int getTotalNodesCountByPlayer(String playerUUID) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_TOTAL_NODES_COUNT_BY_PLAYER_QUERY)) {
            statement.setString(1, playerUUID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
                else return 0;
            }
        }
    }
}