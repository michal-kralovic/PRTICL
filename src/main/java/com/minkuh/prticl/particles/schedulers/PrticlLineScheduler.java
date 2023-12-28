package com.minkuh.prticl.particles.schedulers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class PrticlLineScheduler implements Runnable {

    private double length;
    private double distance;
    private double space;
    private Location p1;
    private Vector vector;

    public PrticlLineScheduler(double length, double distance, Location p1, Vector vector, double space) {
        this.length = length;
        this.distance = distance;
        this.p1 = p1;
        this.vector = vector;
        this.space = space;
    }

    @Override
    public void run() {
        World world = p1.getWorld();
        for (; length < distance; p1.add(vector)) {
            world.spawnParticle(Particle.HEART, p1.getX(), p1.getY(), p1.getZ(), 1);
            length += space;
        }
    }
}
