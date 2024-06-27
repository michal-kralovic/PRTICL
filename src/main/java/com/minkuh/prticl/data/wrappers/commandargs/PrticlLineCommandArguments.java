package com.minkuh.prticl.data.wrappers.commandargs;

public class PrticlLineCommandArguments {
    private String x1;
    private String y1;
    private String z1;
    private String x2;
    private String y2;
    private String z2;
    private Double particleDensity;

    public PrticlLineCommandArguments(String[] args) {
        setX1(args[0]);
        setY1(args[1]);
        setZ1(args[2]);
        setX2(args[3]);
        setY2(args[4]);
        setZ2(args[5]);
        try {
            setParticleDensity(Double.valueOf(args[6]));
        } catch (NumberFormatException ex) {

        }
    }

    public String getX1() {
        return x1;
    }

    public void setX1(String x1) {
        this.x1 = x1;
    }

    public String getY1() {
        return y1;
    }

    public void setY1(String y1) {
        this.y1 = y1;
    }

    public String getZ1() {
        return z1;
    }

    public void setZ1(String z1) {
        this.z1 = z1;
    }

    public String getX2() {
        return x2;
    }

    public void setX2(String x2) {
        this.x2 = x2;
    }

    public String getY2() {
        return y2;
    }

    public void setY2(String y2) {
        this.y2 = y2;
    }

    public String getZ2() {
        return z2;
    }

    public void setZ2(String z2) {
        this.z2 = z2;
    }

    public Double getParticleDensity() {
        return particleDensity;
    }

    public void setParticleDensity(Double particleDensity) {
        this.particleDensity = particleDensity;
    }
}