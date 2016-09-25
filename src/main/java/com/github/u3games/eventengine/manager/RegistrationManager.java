package com.github.u3games.eventengine.manager;

import com.github.u3games.eventengine.core.model.entity.EPlayer;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RegistrationManager {

    private static RegistrationManager sInstance;

    // List of players at the event
    private final Set<Integer> mEventRegisterdPlayers = ConcurrentHashMap.newKeySet();

    /**
     * Get the collection of registered players.
     * @return Collection<L2PcInstance>
     */
    public Collection<Integer> getAllRegisteredPlayers() {
        return mEventRegisterdPlayers;
    }

    /**
     * Clean collection of players.
     */
    public void clearRegisteredPlayers() {
        mEventRegisterdPlayers.clear();
    }

    /**
     * Get if the number of registered players is 0.
     * @return
     *         <li>True No registered players.</li>
     *         <li>False There is at least one registered player.</li>
     */
    public boolean isEmptyRegisteredPlayers() {
        return mEventRegisterdPlayers.isEmpty();
    }

    /**
     * We get if the player is registered.
     * @param player
     * @return
     *         <li>True It is registered.</li>
     *         <li>False It's not registered.</li>
     */
    public boolean isRegistered(EPlayer player) {
        return mEventRegisterdPlayers.contains(player.getId());
    }

    /**
     * Add a player to register.
     * @param player
     * @return
     *         <li>True If the registration is successful.</li>
     *         <li>False If the player already registered.</li>
     */
    public boolean registerPlayer(EPlayer player) {
        return mEventRegisterdPlayers.add(player.getId());
    }

    /**
     * Remove one player from register.
     * @param player
     * @return
     *         <li>True If the player was registered.</li>
     *         <li>False If the player was not registered.</li>
     */
    public boolean unRegisterPlayer(EPlayer player) {
        return mEventRegisterdPlayers.remove(player.getId());
    }

    public static RegistrationManager getInstance() {
        if (sInstance == null) sInstance = new RegistrationManager();
        return sInstance;
    }
}
