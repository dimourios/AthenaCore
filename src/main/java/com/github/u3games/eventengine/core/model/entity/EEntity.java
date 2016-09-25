package com.github.u3games.eventengine.core.model.entity;

public abstract class EEntity {

    private int mId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public boolean isMonster() {
        return false;
    }

    public boolean isNpc() {
        return false;
    }

    public boolean isPlayer() {
        return false;
    }

    public boolean isSummon() {
        return false;
    }
}
