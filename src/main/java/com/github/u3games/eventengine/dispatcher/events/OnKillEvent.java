package com.github.u3games.eventengine.dispatcher.events;

import com.github.u3games.eventengine.core.model.entity.EEntity;
import com.github.u3games.eventengine.enums.ListenerType;

public class OnKillEvent extends ListenerEvent {

    private final EEntity mAttacker;
    private final EEntity mTarget;

    public OnKillEvent(EEntity attacker, EEntity target) {
        mAttacker = attacker;
        mTarget = target;
    }

    public EEntity getAttacker() {
        return mAttacker;
    }

    public EEntity getTarget() {
        return mTarget;
    }

    public ListenerType getType() {
        return ListenerType.ON_KILL;
    }
}
