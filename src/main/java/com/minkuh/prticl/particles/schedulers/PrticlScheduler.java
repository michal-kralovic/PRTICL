package com.minkuh.prticl.particles.schedulers;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import org.bukkit.World;

public class PrticlScheduler implements Runnable {

    private PrticlNode node;

    public PrticlScheduler(PrticlNode node) {
        this.node = node;
    }

    @Override
    public void run() {
        World world = node.getLocation().getWorld();
        world.spawnParticle(node.getParticleType(), node.getLocation(), node.getParticleDensity());
    }
}
