package com.github.u3games.eventengine.dispatcher.events;

import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.enums.ListenerType;

public class OnLogInEvent extends ListenerEvent {

    private final EPlayer mPlayer;

    public OnLogInEvent(EPlayer player) {
        mPlayer = player;
    }

    public EPlayer getPlayer() {
        return mPlayer;
    }

    public ListenerType getType() {
        return ListenerType.ON_LOG_IN;
    }
}
