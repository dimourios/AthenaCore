package com.github.u3games.eventengine.dispatcher.events;

import com.github.u3games.eventengine.core.model.EItem;
import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.enums.ListenerType;

public class OnUseItemEvent extends ListenerEvent {

    private final EPlayer mPlayer;
    private final EItem mItem;

    public OnUseItemEvent(EPlayer player, EItem item) {
        mPlayer = player;
        mItem = item;
    }

    public EPlayer getPlayer() {
        return mPlayer;
    }

    public EItem getItem() {
        return mItem;
    }

    public ListenerType getType() {
        return ListenerType.ON_USE_ITEM;
    }
}
