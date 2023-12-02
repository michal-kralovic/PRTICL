package com.minkuh.prticl.particles.schedulers;

import com.minkuh.prticl.particles.PrticlNode;
import org.bukkit.World;

public class PrticlVectorScheduler implements Runnable {

    private PrticlNode nodeA;
    private PrticlNode nodeB;

    public PrticlVectorScheduler(PrticlNode nodeA, PrticlNode nodeB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }

    @Override
    public void run() {
        World world = nodeA.getLocation().getWorld();
        world.spawnParticle(nodeA.getParticleType(), nodeA.getLocation(), 1);
        world.spawnParticle(nodeB.getParticleType(), nodeB.getLocation(), 1);
    }
}
