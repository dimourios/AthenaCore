package com.github.u3games.eventengine.repository;

import com.github.u3games.eventengine.core.model.entity.EPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerRepository {

    private static PlayerRepository sInstance;

    private Map<Integer, EPlayer> mPlayers = new ConcurrentHashMap<>();

    public EPlayer getPlayer(int id) {
        return mPlayers.get(id);
    }

    public void addPlayer(EPlayer player) {
        mPlayers.put(player.getId(), player);
    }

    public void removePlayer(EPlayer player) {
        mPlayers.remove(player.getId());
    }

    public static PlayerRepository getInstance() {
        if (sInstance == null) sInstance = new PlayerRepository();
        return sInstance;
    }
}
