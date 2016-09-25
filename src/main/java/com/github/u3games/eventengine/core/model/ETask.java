package com.github.u3games.eventengine.core.model;

import com.github.u3games.eventengine.api.adapter.APIThread;

import java.util.concurrent.ScheduledFuture;

public class ETask {

    private ScheduledFuture<?> mSchedule;

    public static ETask newInstance(Runnable runnable, long initDelay) {
        ETask task = new ETask();
        task.setSchedule(APIThread.getInstance().createRepeatableTask(runnable, initDelay));
        return task;
    }

    public static ETask newInstance(Runnable runnable, long initDelay, long period) {
        ETask task = new ETask();
        task.setSchedule(APIThread.getInstance().createRepeatableTask(runnable, initDelay, period));
        return task;
    }

    private void setSchedule(ScheduledFuture<?> schedule) {
        mSchedule = schedule;
    }

    public void cancel(boolean value) {
        mSchedule.cancel(value);
    }
}
