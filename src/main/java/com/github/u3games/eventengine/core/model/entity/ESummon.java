package com.github.u3games.eventengine.core.model.entity;

public class ESummon extends EEntity {

    private int mOwnerId;

    public ESummon(int ownerId) {
        mOwnerId = ownerId;
    }

    public int getOwnerId() {
        return mOwnerId;
    }

    @Override
    public boolean isSummon() {
        return true;
    }
}
