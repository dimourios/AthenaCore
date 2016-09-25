package com.github.u3games.eventengine.api.adapter;

import com.l2jserver.gameserver.ThreadPoolManager;

import java.util.concurrent.ScheduledFuture;

public class APIThread {

    private static APIThread sInstance;

    public ScheduledFuture<?> createRepeatableTask(Runnable runnable, long initDelay) {
        return ThreadPoolManager.getInstance().scheduleGeneral(runnable, initDelay);
    }

    public ScheduledFuture<?> createRepeatableTask(Runnable runnable, long initDelay, long period) {
        return ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(runnable, initDelay, period);
    }

    public static APIThread getInstance() {
        if (sInstance == null) sInstance = new APIThread();
        return sInstance;
    }
}
