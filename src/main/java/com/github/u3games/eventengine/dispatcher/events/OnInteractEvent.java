package com.github.u3games.eventengine.dispatcher.events;

import com.github.u3games.eventengine.core.model.entity.ENpc;
import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.enums.ListenerType;

public class OnInteractEvent extends ListenerEvent {

    private final EPlayer mPlayer;
    private final ENpc mNpc;

    public OnInteractEvent(EPlayer player, ENpc npc) {
        mPlayer = player;
        mNpc = npc;
    }

    public EPlayer getPlayer() {
        return mPlayer;
    }

    public ENpc getNpc() {
        return mNpc;
    }

    public ListenerType getType() {
        return ListenerType.ON_INTERACT;
    }
}
