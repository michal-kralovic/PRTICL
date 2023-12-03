package com.minkuh.prticl.particles.prticl;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.NoSuchElementException;
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

        try {
            prticlNodes.put(prticlNodes.lastKey() + 1, node);
        } catch (NoSuchElementException e) {
            prticlNodes.put(0, node);
        }

        node.setId(prticlNodes.get(prticlNodes.lastKey()).getId());

        return node;
    }

    public static PrticlNode createPrticl(Particle particleType, Location location, Player createdBy, int repeatDelay) {
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

    public static PrticlNode createPrticl(Particle particleType, Location location, Player createdBy, double particleDensity) {
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

    public static PrticlNode createPrticl(Particle particleType, Location location, Player createdBy, int repeatDelay, int particleDensity) {
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
