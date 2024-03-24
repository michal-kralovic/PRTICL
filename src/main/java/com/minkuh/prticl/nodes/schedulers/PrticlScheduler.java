package com.minkuh.prticl.nodes.schedulers;

import com.minkuh.prticl.nodes.prticl.PrticlNode;
import org.bukkit.World;

public class PrticlScheduler implements Runnable {

    private PrticlNode node;

    public PrticlScheduler(PrticlNode node) {
        this.node = node;
    }

    @Override
    public void run() {
        World world = node.getLocationObject().getLocation().getWorld();
        world.spawnParticle(node.getParticleType(), node.getLocationObject().getLocation(), node.getParticleDensity());
    }
}
