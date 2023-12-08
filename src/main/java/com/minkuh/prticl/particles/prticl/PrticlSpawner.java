package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.*;

public class PrticlSpawner {
    private static SortedMap<Integer, PrticlNode> prticlNodes = new TreeMap<>();
    private static List<PrticlNode> nodes = new ArrayList<>();

    public static PrticlNode createPrticl(String name, String createdBy) {
        PrticlNode node = new PrticlNode() {{
            setName(name);
            setCreatedBy(createdBy);
        }};

        return addNodeToMapAndReturn(node);
    }

    public static PrticlNode createPrticl(String name, Particle particleType, String createdBy) {
        PrticlNode node = new PrticlNode() {{
            setName(name);
            setParticleType(particleType);
            setCreatedBy(createdBy);
        }};

        return addNodeToMapAndReturn(node);
    }

    public static PrticlNode createPrticl(Particle particleType, Location location, String createdBy) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
        }};

        return addNodeToMapAndReturn(node);
    }

    public static PrticlNode createPrticl(Particle particleType, Location location, String createdBy, int repeatDelay) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
            setRepeatDelay(repeatDelay);
        }};

        return addNodeToMapAndReturn(node);
    }

    public static PrticlNode createPrticl(Particle particleType, Location location, String createdBy, double particleDensity) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
            setParticleDensity((int) particleDensity);
        }};

        return addNodeToMapAndReturn(node);
    }

    public static PrticlNode createPrticl(Particle particleType, Location location, String createdBy, int repeatDelay, int particleDensity) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
            setRepeatDelay(repeatDelay);
            setParticleDensity(particleDensity);
        }};

        return addNodeToMapAndReturn(node);
    }

    private static PrticlNode addNodeToMapAndReturn(PrticlNode node) {
        nodes.clear();
        nodes.addAll(prticlNodes.values());
        nodes.add(node);

        for (PrticlNode prticlNode : nodes) {
            node.setId(nodes.size());
            prticlNodes.put(nodes.size(), prticlNode);
        }

        return node;
    }
}
