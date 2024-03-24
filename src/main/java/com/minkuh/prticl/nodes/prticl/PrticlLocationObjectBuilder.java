package com.minkuh.prticl.nodes.prticl;

import org.bukkit.Location;

public class PrticlLocationObjectBuilder {
    private int id;
    private Location location;

    public PrticlLocationObjectBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public PrticlLocationObjectBuilder withLocation(Location location) {
        this.location = location;
        return this;
    }

    public PrticlLocationObject build() {
        PrticlLocationObject obj = new PrticlLocationObject();
        obj.setId(this.id);
        obj.setLocation(this.location);
        return obj;
    }
}