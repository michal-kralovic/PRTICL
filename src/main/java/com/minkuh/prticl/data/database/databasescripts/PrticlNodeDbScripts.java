package com.minkuh.prticl.data.database.databasescripts;

import com.minkuh.prticl.nodes.prticl.PrticlLocationObjectBuilder;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.prticl.PrticlNodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrticlNodeDbScripts {
    private final Connection connection;

    public PrticlNodeDbScripts(Connection connection) {
        this.connection = connection;
    }

    public boolean createNode(String name, int repeatDelay, int particleDensity, String particleType, int locationId, int playerId) throws SQLException {
        String query = "INSERT INTO nodes (name, repeat_delay, particle_density, particle_type, location_id, player_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, repeatDelay);
            statement.setInt(3, particleDensity);
            statement.setString(4, particleType);
            statement.setInt(5, locationId);
            statement.setInt(6, playerId);
            return statement.executeUpdate() == 1;
        }
    }

    public PrticlNode getNodeById(int nodeId) throws SQLException {
        String query = "SELECT nodes.*, locations.id AS 'location_pk', locations.x, locations.y, locations.z, locations.world\n" +
                "FROM nodes\n" +
                "JOIN locations ON nodes.location_id = locations.id\n" +
                "WHERE nodes.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, nodeId);

            ResultSet dbResult = statement.executeQuery();
            return new PrticlNodeBuilder()
                    .setId(dbResult.getInt("id"))
                    .setName(dbResult.getString("name"))
                    .setRepeatDelay(dbResult.getInt("repeat_delay"))
                    .setParticleDensity(dbResult.getInt("particle_density"))
                    .setParticleType(Enum.valueOf(Particle.class, dbResult.getString("particle_type")))
                    .setLocationObject(
                            new PrticlLocationObjectBuilder()
                                    .withId(dbResult.getInt("location_pk"))
                                    .withLocation(
                                            new Location(
                                                    Bukkit.getServer().getWorld(dbResult.getString("world")),
                                                    dbResult.getDouble("x"),
                                                    dbResult.getDouble("y"),
                                                    dbResult.getDouble("z")
                                            )
                                    ).build()
                    ).build();
        }
    }

    public PrticlNode getNodeByName(String nodeName) throws SQLException {
        String query = "SELECT nodes.*, locations.id AS 'location_pk', locations.x, locations.y, locations.z, locations.world\n" +
                "FROM nodes\n" +
                "JOIN locations ON nodes.location_id = locations.id\n" +
                "WHERE nodes.name = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nodeName);

            ResultSet dbResult = statement.executeQuery();
            return new PrticlNodeBuilder()
                    .setId(dbResult.getInt("id"))
                    .setName(dbResult.getString("name"))
                    .setRepeatDelay(dbResult.getInt("repeat_delay"))
                    .setParticleDensity(dbResult.getInt("particle_density"))
                    .setParticleType(Enum.valueOf(Particle.class, dbResult.getString("particle_type")))
                    .setLocationObject(
                            new PrticlLocationObjectBuilder()
                                    .withId(dbResult.getInt("location_pk"))
                                    .withLocation(
                                            new Location(
                                                    Bukkit.getWorld(dbResult.getString("world")),
                                                    dbResult.getDouble("x"),
                                                    dbResult.getDouble("y"),
                                                    dbResult.getDouble("z")
                                            )
                                    ).build()
                    ).build();
        }
    }

    public List<String> getNodeNamesList() throws SQLException {
        String query = "SELECT name FROM nodes";
        List<String> nodesNamesList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                nodesNamesList.add(rs.getString("name"));
            }

            return nodesNamesList;
        }
    }

    public List<PrticlNode> getNodesListByWorld(World world) throws SQLException {
        String query = "SELECT nodes.*, \n" +
                "locations.id AS location_pk, locations.x, locations.y, locations.z, locations.world,\n" +
                "players.id AS player_pk, players.uuid, players.username\n" +
                "FROM nodes\n" +
                "JOIN locations ON nodes.location_id = locations.id\n" +
                "JOIN players ON nodes.player_id = players.id\n" +
                "WHERE locations.world = ?;\n";

        List<PrticlNode> nodesList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, world.getName());

            ResultSet dbResult = statement.executeQuery();

            while (dbResult.next()) {
                nodesList.add(
                        PrticlNode.deserialize(
                                dbResult.getInt("id"),
                                dbResult.getString("name"),
                                dbResult.getInt("repeat_delay"),
                                dbResult.getInt("particle_density"),
                                dbResult.getString("particle_type"),
                                new PrticlLocationObjectBuilder()
                                        .withId(dbResult.getInt("location_pk"))
                                        .withLocation(
                                                new Location(
                                                        Bukkit.getWorld(dbResult.getString("world")),
                                                        dbResult.getDouble("x"),
                                                        dbResult.getDouble("y"),
                                                        dbResult.getDouble("z")
                                                )
                                        ).build(),
                                dbResult.getString("username")
                        )
                );
            }

            return nodesList;
        }
    }
}