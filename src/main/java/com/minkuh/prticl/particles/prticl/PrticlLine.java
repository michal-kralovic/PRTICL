package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * A model class for a PrticlLine (a line between two points).
 */
public class PrticlLine {
    private Vector vector;
    private Location loc1;
    private Location loc2;
    private double density = 0.1;

    public PrticlLine() {
    }

    public Vector getVector() {
        return vector;
    }

    public void setVector(Vector vector) {
        this.vector = vector;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public Location getLoc1() {
        return loc1;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public void setLoc1(double x, double y, double z, World world) {
        this.loc1 = new Location(world, x, y, z);
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(double x, double y, double z, World world) {
        this.loc2 = new Location(world, x, y, z);
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
    }
}
