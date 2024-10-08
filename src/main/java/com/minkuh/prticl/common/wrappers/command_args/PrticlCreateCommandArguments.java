package com.minkuh.prticl.common.wrappers.command_args;

import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.Locale;

public class PrticlCreateCommandArguments {
    private String name;
    private Particle particleType;
    private Integer repeatDelay;
    private Integer repeatCount;
    private Integer particleDensity;
    private Location location;

    public PrticlCreateCommandArguments(String[] args) throws IllegalArgumentException {
        Particle particle = Particle.valueOf(getSupportedParticle(args[1]));

        setName(args[0]);
        setParticleType(particle);
        try {
            setRepeatDelay(Integer.parseInt(args[2]));
            setRepeatCount(Integer.parseInt(args[3]));
            setParticleDensity(Integer.parseInt(args[4]));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParticleType() {
        return particleType.toString();
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void setParticleType(Particle particleType) {
        this.particleType = particleType;
    }

    public Integer getRepeatDelay() {
        return repeatDelay;
    }

    public void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public Integer getParticleDensity() {
        return particleDensity;
    }

    public void setParticleDensity(int particleDensity) {
        this.particleDensity = particleDensity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * A utility method to strip the input particle argument of its namespace if necessary.<br><br>
     * E.g.:<br>
     * - input: "minecraft:cloud", "cLoUd"<br>
     * - output (of this method): "CLOUD", "CLOUD"
     *
     * @param arg The input particle from the Player
     * @return The Particle as a support String.
     */
    private String getSupportedParticle(String arg) throws IllegalArgumentException {
        arg = arg.contains(":") ? arg.split(":")[1].toUpperCase(Locale.ROOT) : arg.toUpperCase(Locale.ROOT);

        if (!EnumUtils.isValidEnum(Particle.class, arg))
            throw new IllegalArgumentException("The " + Particle.class.getName() + " enum doesn't contain the input particle \"" + arg + "\"");

        return arg.toUpperCase(Locale.ROOT);
    }
}