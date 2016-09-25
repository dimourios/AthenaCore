package com.github.u3games.eventengine.core.model.entity;

import com.github.u3games.eventengine.api.adapter.APIPlayer;
import com.github.u3games.eventengine.core.model.ELocation;
import com.github.u3games.eventengine.core.model.holder.EItemHolder;
import com.github.u3games.eventengine.core.model.holder.ESkillHolder;
import com.github.u3games.eventengine.datatables.MessageData;
import com.github.u3games.eventengine.enums.TeamType;
import com.github.u3games.eventengine.events.holders.TeamHolder;
import com.github.u3games.eventengine.interfaces.ParticipantHolder;

import java.util.Collection;
import java.util.Set;

public class EPlayer extends EEntity implements ParticipantHolder {

    // Max delay time for reuse skill
    private static final int MAX_DELAY_TIME_SKILL = 900000;

    // Player kills in current event
    private int _kills = 0;
    // Player deaths in current event
    private int _deaths = 0;
    // Original title color before teleporting to the event
    private int _oriColorTitle;
    // Original title before teleporting to the event
    private String _oriTitle;
    // Player's team in the event
    private TeamHolder _team;
    // Previous location before participating in the event
    private ELocation _returnLocation;
    private int _dinamicInstanceId = 0;

    // Variable used to know the protection time inside events
    private long _protectionTimeEnd = 0;

    public static EPlayer newInstance(int playerId) {
        EPlayer player = new EPlayer();
        player.setId(playerId);

        return player;
    }

    private EPlayer() {}

    // ------------------- Engine logic -------------------

    public void increaseKills()
    {
        _kills++;
    }

    @Override
    public int getKills()
    {
        return _kills;
    }

    public void increaseDeaths()
    {
        _deaths++;
    }

    @Override
    public int getDeaths()
    {
        return _deaths;
    }

    @Override
    public int getPoints()
    {
        return _kills - _deaths;
    }

    public long getProtectionTimeEnd()
    {
        return _protectionTimeEnd;
    }

    public boolean isProtected()
    {
        return _protectionTimeEnd > System.currentTimeMillis();
    }

    public void setProtectionTimeEnd(long time)
    {
        _protectionTimeEnd = time;
    }

    public ELocation getReturnLoc()
    {
        return _returnLocation;
    }

    public void setReturnLoc(ELocation location)
    {
        _returnLocation = location;
    }

    public void recoverOriginalTitle()
    {
        setTitle(_oriTitle);
    }

    public void recoverOriginalColorTitle()
    {
        setTitleColor(_oriColorTitle);
    }

    public TeamHolder getTeam()
    {
        return _team;
    }

    public void setTeam(TeamHolder team)
    {
        // Set original title character
        _oriTitle = getTitle();
        // Set original title color character
        _oriColorTitle = getTitleColor();
        // Set team character
        _team = team;
        // Set team title character
        setTitle(team.getName());
        // Set team color character
        setTitleColor(team.getTeamType().getColor());
    }

    public TeamType getTeamType()
    {
        return _team.getTeamType();
    }

    public int getDinamicInstanceId()
    {
        return _dinamicInstanceId;
    }

    public void setDinamicInstanceId(int dinamicInstanceId)
    {
        _dinamicInstanceId = dinamicInstanceId;
    }

    // ------------------- This is Ok -------------------

    public final void cancelAllPlayerActions()
    {
        APIPlayer.getInstance().cancelAllPlayerActions(this);
    }

    public final void cancelAllEffects()
    {
        APIPlayer.getInstance().cancelAllEffects(this, MAX_DELAY_TIME_SKILL);
    }

    public final void applyEffects(Set<ESkillHolder> skills)
    {
        APIPlayer.getInstance().applyEffects(this, skills);
    }

    public final void addItems(Collection<EItemHolder> itemHolders)
    {
        APIPlayer.getInstance().addItems(this, itemHolders);
    }

    public final void teleToLocation(ELocation location)
    {
        APIPlayer.getInstance().teleToLocation(this, location);
    }

    public final void revive()
    {
        APIPlayer.getInstance().revive(this);
    }

    public boolean isDead()
    {
        return APIPlayer.getInstance().isDead(this);
    }

    public ELocation getLocation()
    {
        return APIPlayer.getInstance().getLocation(this);
    }

    public void updateAndBroadcastStatus(int value)
    {
        APIPlayer.getInstance().updateAndBroadcastStatus(this, value);
    }

    public void setFullHealth()
    {
        APIPlayer.getInstance().setFullHealth(this);
    }

    public void setCurrentHealth(int cp, int hp, int mp)
    {
        APIPlayer.getInstance().setHealth(this, cp, hp, mp);
    }

    public void cancelDecay()
    {
        APIPlayer.getInstance().cancelDecay(this);
    }

    public String getName()
    {
        return APIPlayer.getInstance().getName(this);
    }

    public String getTitle() {
        return APIPlayer.getInstance().getTitle(this);
    }

    public void setTitle(String title)
    {
        APIPlayer.getInstance().setTitle(this, title);
    }

    public int getTitleColor() {
        return APIPlayer.getInstance().getTitleColor(this);
    }

    public void setTitleColor(int color) {
        APIPlayer.getInstance().setTitleColor(this, color);
    }

    public void sendCustomMessage(String message)
    {
        APIPlayer.getInstance().sendMessage(this, message);
    }

    public void sendPartyRoomCommander(String key, boolean tag) {
        APIPlayer.getInstance().sendPartyRoomCommander(this, MessageData.getInstance().getMsgByLang(this, key, tag));
    }

    public void sendMessage(String key) {
        APIPlayer.getInstance().sendMessage(this, MessageData.getInstance().getMsgByLang(this, key, false));
    }

    public void sendMessage(String key, boolean tag) {
        APIPlayer.getInstance().sendMessage(this, MessageData.getInstance().getMsgByLang(this, key, tag));
    }

    public void removePlayerFromEvent()
    {
        recoverOriginalColorTitle();
        recoverOriginalTitle();
        APIPlayer.getInstance().removeFromEventInstance(this, getReturnLoc());
    }

    public boolean isInOlympiadMode()
    {
        return APIPlayer.getInstance().isInOlympiadMode(this);
    }

    public boolean isInDuel()
    {
        return APIPlayer.getInstance().isInDuel(this);
    }

    public boolean isInObserverMode()
    {
        return APIPlayer.getInstance().isInObserverMode(this);
    }

    public void addEventListener()
    {

    }

    @Override
    public boolean isPlayer()
    {
        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof EPlayer)) return false;

        EPlayer player = (EPlayer) o;

        return getId() == player.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
