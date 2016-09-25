package com.github.u3games.eventengine.manager;

import com.github.u3games.eventengine.api.adapter.ERandom;
import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.datatables.EventData;
import com.github.u3games.eventengine.events.handler.AbstractEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VoteManager {

    private static VoteManager sInstance;

    // Id's list of characters who voted
    private final Set<Integer> mPlayersAlreadyVoted = ConcurrentHashMap.newKeySet();
    // Map of the Id's of the characters who voted
    private final Map<Class<? extends AbstractEvent>, Set<Integer>> mCurrentEventVotes = new HashMap<>();

    /**
     * Init votes
     */
    public void initVotes() {
        for (Class<? extends AbstractEvent> type : EventData.getInstance().getEnabledEvents()) {
            mCurrentEventVotes.put(type, ConcurrentHashMap.newKeySet());
        }
    }

    /**
     * Method responsible of initializing the votes of each event.
     */
    public void clearVotes() {
        // The map is restarted
        for (Class<? extends AbstractEvent> event : mCurrentEventVotes.keySet()) {
            mCurrentEventVotes.get(event).clear();
        }
        // The list of players who voted cleaned
        mPlayersAlreadyVoted.clear();
    }

    /**
     * Increase by 1, the number of votes.
     * @param player The character who is voting.
     * @param event Event voting.
     */
    public void increaseVote(EPlayer player, Class<? extends AbstractEvent> event) {
        // Add character at the list of those who voted
        // If it was, continue
        // If it wasn't, adds a vote to the event
        if (mPlayersAlreadyVoted.add(player.getId())) {
            mCurrentEventVotes.get(event).add(player.getId());
        }
    }

    /**
     * Decrease the number of votes.
     * @param player Character that are voting.
     */
    public void removeVote(EPlayer player)
    {
        // Deletes it from the list of players who voted
        if (mPlayersAlreadyVoted.remove(player.getId())) {
            // If he was on the list, start looking for which event voted
            for (Class<? extends AbstractEvent> event : mCurrentEventVotes.keySet()) {
                mCurrentEventVotes.get(event).remove(player.getId());
            }
        }
    }

    /**
     * Get the number of votes it has a certain event.
     * @param event AVA, TVT, CFT.
     * @return int
     */
    public int getCurrentVotesInEvent(Class<? extends AbstractEvent> event) {
        return mCurrentEventVotes.get(event).size();
    }

    /**
     * Get the amount of total votes.
     * @return
     */
    public int getAllCurrentVotesInEvents() {
        int count = 0;
        for (Set<Integer> set : mCurrentEventVotes.values())
        {
            count += set.size();
        }
        return count;
    }

    /**
     * Get the event with more votes. In case all have the same amount of votes, it will make a random among those most votes have.
     * @return
     */
    public Class<? extends AbstractEvent> getEventMoreVotes() {
        int maxVotes = 0;
        List<Class<? extends AbstractEvent>> topEvents = new ArrayList<>();
        for (Class<? extends AbstractEvent> event : mCurrentEventVotes.keySet()) {
            int eventVotes = mCurrentEventVotes.get(event).size();
            if (eventVotes > maxVotes) {
                topEvents.clear();
                topEvents.add(event);
                maxVotes = eventVotes;
            } else if (eventVotes == maxVotes) {
                topEvents.add(event);
            }
        }

        int topEventsSize = topEvents.size();
        if (topEventsSize > 1) {
            return topEvents.get(ERandom.get(0, topEventsSize - 1));
        }
        return topEvents.get(0);
    }

    public static VoteManager getInstance() {
        if (sInstance == null) sInstance = new VoteManager();
        return sInstance;
    }
}
