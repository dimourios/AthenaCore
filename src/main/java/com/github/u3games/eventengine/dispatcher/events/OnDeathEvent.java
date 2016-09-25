package com.github.u3games.eventengine.dispatcher.events;

import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.enums.ListenerType;

public class OnDeathEvent extends ListenerEvent {

    private final EPlayer mTarget;

    public OnDeathEvent(EPlayer target) {
        mTarget = target;
    }

    public EPlayer getTarget() {
        return mTarget;
    }

    public ListenerType getType() {
        return ListenerType.ON_DEATH;
    }
}
