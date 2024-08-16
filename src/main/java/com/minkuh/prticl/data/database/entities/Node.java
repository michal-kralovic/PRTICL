package com.minkuh.prticl.data.database.entities;

import jakarta.persistence.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "nodes", schema = "prticl")
public class Node implements IPrticlEntity {
    public Node() {
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "repeat_delay")
    private int repeatDelay;

    @Column(name = "repeat_count")
    private int repeatCount;

    @Column(name = "particle_density")
    private int particleDensity;

    @Column(name = "particle_type")
    private String particleType;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "world_uuid")
    private UUID worldUUID;

    @Column(name = "x")
    private double x;

    @Column(name = "y")
    private double y;

    @Column(name = "z")
    private double z;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "node_trigger",
            schema = "prticl",
            joinColumns = @JoinColumn(name = "node_id"),
            inverseJoinColumns = @JoinColumn(name = "trigger_id")
    )
    private Set<Trigger> triggers = new HashSet<>();

    @Transient
    private boolean isSpawned;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getParticleDensity() {
        return particleDensity;
    }

    public void setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
    }

    public Particle getParticleType() {
        return Particle.valueOf(particleType);
    }

    public void setParticleType(String particleType) {
        this.particleType = particleType;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public String getWorldName() {
        World world = Bukkit.getWorld(this.getWorldUUID());

        if (world == null) {
            return null;
        } else {
            return world.getName();
        }
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public void setWorldUUID(UUID worldUUID) {
        this.worldUUID = worldUUID;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Set<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(Set<Trigger> triggers) {
        this.triggers = triggers;
    }

    public void setSpawned(boolean spawned) {
        isSpawned = spawned;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}