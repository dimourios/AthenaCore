package com.github.u3games.eventengine.core.model;

public class ELocation {

    private int x;
    private int y;
    private int z;
    private int heading;
    private int instanceId;

    public ELocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ELocation(int x, int y, int z, int heading, int instanceId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.heading = heading;
        this.instanceId = instanceId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }
}
