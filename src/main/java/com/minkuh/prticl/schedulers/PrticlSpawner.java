package com.minkuh.prticl.schedulers;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlNode;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class PrticlSpawner {
    private final Prticl plugin;

    public PrticlSpawner(Prticl plugin) {
        this.plugin = plugin;
    }

    public void spawnNode(PrticlNode node) {
        new PrticlNodeScheduler(node, node.getLocationObject().getLocation().getWorld()).runTaskTimer(plugin, 0, node.getRepeatDelay());
    }

    private static class PrticlNodeScheduler extends BukkitRunnable {
        private final PrticlNode node;
        private final World world;

        public PrticlNodeScheduler(PrticlNode node, World world) {
            this.node = node;
            this.world = world;
        }

        @Override
        public void run() {
            if (this.node.isEnabled()) {
                world.spawnParticle(
                        node.getParticleType(),
                        node.getLocationObject().getLocation(),
                        node.getParticleDensity()
                );

                return;
            }

            cancel();
        }
    }
}