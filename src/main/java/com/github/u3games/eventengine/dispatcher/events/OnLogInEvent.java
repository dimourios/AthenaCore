package com.github.u3games.eventengine.dispatcher.events;

import com.github.u3games.eventengine.enums.ListenerType;

public class OnLogInEvent extends ListenerEvent {

    private final int mPlayerId;

    public OnLogInEvent(int playerId) {
        mPlayerId = playerId;
    }

    public int getPlayerId() {
        return mPlayerId;
    }

    public ListenerType getType() {
        return ListenerType.ON_LOG_IN;
    }
}
