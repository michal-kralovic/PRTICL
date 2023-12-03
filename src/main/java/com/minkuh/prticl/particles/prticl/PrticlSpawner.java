package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.SortedMap;
import java.util.TreeMap;

public class PrticlSpawner {
    private static SortedMap<Integer, PrticlNode> prticlNodes = new TreeMap<>();

    public static PrticlNode createPrticl(Particle particleType, Location location, Player createdBy) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
        }};

        prticlNodes.put(0, node);

        node.setId(prticlNodes.get(0).getId());

        return node;
    }
}
