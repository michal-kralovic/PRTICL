package com.minkuh.prticl.nodes.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * The most important element of PRTICL - its Nodes.
 * <br>A model class for a PRTICL node.
 */
public class PrticlNode implements ConfigurationSerializable {
    private int id;
    private String name = NODE_DEFAULT_NAME;
    private int repeatDelay = 20;
    private int particleDensity = 1;
    private Particle particleType = Particle.HEART;
    private PrticlLocationObject locationObject;
    private String createdBy;

    public PrticlNode() {
    }

    public PrticlNode(int id, String name, int repeatDelay, int particleDensity, Particle particleType, PrticlLocationObject locationObject, String createdBy) {
        this.id = id;
        this.name = name;
        this.repeatDelay = repeatDelay;
        this.particleDensity = particleDensity;
        this.particleType = particleType;
        this.locationObject = locationObject;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.length() <= 50 && !name.isBlank())
            this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public Particle getParticleType() {
        return particleType;
    }

    public void setParticleType(Particle particleType) {
        this.particleType = particleType;
    }

    public PrticlLocationObject getLocationObject() {
        return locationObject;
    }

    public void setLocationObject(PrticlLocationObject locationObject) {
        this.locationObject = locationObject;
    }

    public int getParticleDensity() {
        return particleDensity;
    }

    public void setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PrticlNode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", repeatDelay=" + repeatDelay +
                ", particleDensity=" + particleDensity +
                ", particleType=" + particleType +
                ", location=" + locationObject +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        if (this.locationObject != null) {
            data.put(NODE_PARAM_LOCATION, this.locationObject.getLocation().serialize());
        }
        data.put(NODE_PARAM_PARTICLE_DENSITY, this.particleDensity);
        data.put(NODE_PARAM_REPEAT_DELAY, this.repeatDelay);
        data.put(NODE_PARAM_PARTICLE_TYPE, this.particleType.toString());
        data.put(NODE_PARAM_OWNER, this.createdBy);
        data.put(NODE_PARAM_NAME, this.name);
        data.put(NODE_PARAM_ID, this.id);

        return data;
    }

    /**
     * Creates a Prticl node from the config.
     *
     * @param args The node in the config
     * @return The deserialized Prticl node.
     */
    public static PrticlNode deserialize(Map<String, Object> args) throws NullPointerException {
        PrticlLocationObject locationDto = new PrticlLocationObject();
        locationDto.setLocation((Location) args.get(NODE_PARAM_LOCATION));
        locationDto.setId((int) args.get("location_id"));

        return new PrticlNode(
                (int) args.get(NODE_PARAM_ID),
                (String) args.get(NODE_PARAM_NAME),
                (int) args.get(NODE_PARAM_REPEAT_DELAY),
                (int) args.get(NODE_PARAM_PARTICLE_DENSITY),
                Particle.valueOf((String) args.get(NODE_PARAM_PARTICLE_TYPE)),
                locationDto,
                (String) args.get(NODE_PARAM_OWNER)
        );
    }

    /**
     * Creates a Prticl node from the passed arguments.
     *
     * @return The deserialized Prticl node.
     */
    public static PrticlNode deserialize(int id, String name, int repeatDelay, int particleDensity, String nameOfParticleType, PrticlLocationObject locationObject, String createdBy) throws NullPointerException {
        PrticlLocationObject locationDto = new PrticlLocationObject();
        locationDto.setLocation(locationObject.getLocation());
        locationDto.setId(locationObject.getId());

        return new PrticlNode(id, name, repeatDelay, particleDensity, Particle.valueOf(nameOfParticleType), locationDto, createdBy);
    }
}