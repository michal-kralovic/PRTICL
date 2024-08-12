package com.minkuh.prticl.common;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.entities.Node;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class PrticlSpawner {
    private final Prticl plugin;

    public PrticlSpawner(Prticl plugin) {
        this.plugin = plugin;
    }

    public void spawnNode(Node node) {
        new PrticlNodeScheduler(node).runTaskTimer(plugin, 0, node.getRepeatDelay());
    }

    private static class PrticlNodeScheduler extends BukkitRunnable {
        private final Location location;
        private final Node node;
        private final World world;
        private int counter = 0;

        public PrticlNodeScheduler(Node node) {
            this.node = node;
            this.location = LocationBuilder.fromNode(node);
            this.world = location.getWorld();
        }

        @Override
        public void run() {
            if (node.getRepeatCount() > 0) {
                if (counter < node.getRepeatCount() - 1) {
                    counter++;
                } else {
                    cancel();
                }
            }

            if (node.isEnabled()) {
                world.spawnParticle(
                        node.getParticleType(),
                        location,
                        node.getParticleDensity()
                );

                return;
            }

            cancel();
        }
    }
}