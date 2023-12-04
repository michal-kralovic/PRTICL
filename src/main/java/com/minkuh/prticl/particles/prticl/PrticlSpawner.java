package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.*;

public class PrticlSpawner {
    private static SortedMap<Integer, PrticlNode> prticlNodes = new TreeMap<>();
    private static List<PrticlNode> nodes = new ArrayList<>();

    public static PrticlNode createPrticl(Particle particleType, Location location, String createdBy) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
        }};

        nodes.clear();
        nodes.addAll(prticlNodes.values());
        nodes.add(node);

        for (PrticlNode prticlNode : nodes) {
            node.setId(nodes.size());
            prticlNodes.put(nodes.size(), prticlNode);
        }

        return node;
    }

    public static PrticlNode createPrticl(Particle particleType, Location location, String createdBy, int repeatDelay) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
            setRepeatDelay(repeatDelay);
        }};

        try {
            prticlNodes.put(prticlNodes.lastKey() + 1, node);
        } catch (NoSuchElementException e) {
            prticlNodes.put(0, node);
        }

        node.setId(prticlNodes.get(prticlNodes.lastKey()).getId());

        return node;
    }

    public static PrticlNode createPrticl(Particle particleType, Location location, String createdBy, double particleDensity) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
            setParticleDensity((int) particleDensity);
        }};

        try {
            prticlNodes.put(prticlNodes.lastKey() + 1, node);
        } catch (NoSuchElementException e) {
            prticlNodes.put(0, node);
        }

        node.setId(prticlNodes.get(prticlNodes.lastKey()).getId());

        return node;
    }

    public static PrticlNode createPrticl(Particle particleType, Location location, String createdBy, int repeatDelay, int particleDensity) {
        PrticlNode node = new PrticlNode() {{
            setParticleType(particleType);
            setLocation(location);
            setCreatedBy(createdBy);
            setRepeatDelay(repeatDelay);
            setParticleDensity(particleDensity);
        }};

        try {
            prticlNodes.put(prticlNodes.lastKey() + 1, node);
        } catch (NoSuchElementException e) {
            prticlNodes.put(0, node);
        }

        node.setId(prticlNodes.get(prticlNodes.lastKey()).getId());

        return node;
    }
}
