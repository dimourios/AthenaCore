package com.github.u3games.eventengine.core.model.holder;

public class EItemHolder {

    private final int mId;
    private final int mCount;

    public EItemHolder(int id, int count) {
        mId = id;
        mCount = count;
    }

    public int getId() {
        return mId;
    }

    public int getCount() {
        return mCount;
    }
}
