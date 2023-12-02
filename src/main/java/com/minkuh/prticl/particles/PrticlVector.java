package com.minkuh.prticl.particles;

import io.papermc.paper.math.FinePosition;
import org.bukkit.Location;
import org.bukkit.World;

public class PrticlVector {

    private Location positionA;
    private Location positionB;

    public PrticlVector() {
    }

    public PrticlVector(Location positionA, Location positionB) {
        this.positionA = positionA;
        this.positionB = positionB;
    }

    public FinePosition getPositionA() {
        return positionA;
    }

    public void setPositionA(double x, double y, double z, World world) {
        this.positionA = new Location(world, x, y, z);
    }
    public void setPositionA(double x, double y, double z, World world, float pitch) {
        this.positionA.set(x, y, z);
        this.positionA.setWorld(world);
        this.positionA.setPitch(pitch);
    }
    public void setPositionA(double x, double y, double z, World world, float pitch, float yaw) {
        this.positionA.set(x, y, z);
        this.positionA.setWorld(world);
        this.positionA.setPitch(pitch);
        this.positionA.setYaw(yaw);
    }

    public FinePosition getPositionB() {
        return positionB;
    }

    public void setPositionB(double x, double y, double z, World world) {
        this.positionB = new Location(world, x, y, z);
    }

    public void setPositionB(double x, double y, double z, World world, float pitch) {
        this.positionB.set(x, y, z);
        this.positionB.setWorld(world);
        this.positionB.setPitch(pitch);
    }

    public void setPositionB(double x, double y, double z, World world, float pitch, float yaw) {
        this.positionB.set(x, y, z);
        this.positionB.setWorld(world);
        this.positionB.setPitch(pitch);
        this.positionB.setYaw(yaw);
    }
}
