package com.github.u3games.eventengine.dispatcher.events;

import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.enums.ListenerType;

public class OnLogOutEvent extends ListenerEvent {

    private final EPlayer mPlayer;

    public OnLogOutEvent(EPlayer player) {
        mPlayer = player;
    }

    public EPlayer getPlayer() {
        return mPlayer;
    }

    public ListenerType getType() {
        return ListenerType.ON_LOG_OUT;
    }
}
