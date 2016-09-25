package com.github.u3games.eventengine.api.adapter;

import com.github.u3games.eventengine.core.model.ELocation;
import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.core.model.holder.EItemHolder;
import com.github.u3games.eventengine.core.model.holder.ESkillHolder;
import com.github.u3games.eventengine.api.listeners.EventEngineListener;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2CubicInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.SkillCoolTime;
import com.l2jserver.gameserver.taskmanager.DecayTaskManager;

import java.util.Collection;

public class APIPlayer {

    private static APIPlayer sInstance;

    // ------------- GETTERS -------------

    public String getName(EPlayer ePlayer) {
        return getPlayer(ePlayer).getName();
    }

    public String getTitle(EPlayer ePlayer) {
        return getPlayer(ePlayer).getTitle();
    }

    public ELocation getLocation(EPlayer ePlayer) {
        L2PcInstance player = getPlayer(ePlayer);

        return new ELocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
    }

    public boolean isDead(EPlayer ePlayer) {
        return getPlayer(ePlayer).isDead();
    }

    // ------------- ACTIONS -------------

    public void removeFromEventInstance(EPlayer ePlayer, ELocation eLocation) {
        L2PcInstance player = getPlayer(ePlayer);

        InstanceManager.getInstance().getPlayerWorld(player).removeAllowed(player.getObjectId());
        player.setInstanceId(0);
        // Remove the player from event listener (it's used to deny the manual res)
        player.removeEventListener(EventEngineListener.class);
        player.teleToLocation(getLocation(eLocation));
    }

    public void sendMessage(EPlayer ePlayer, String message) {
        getPlayer(ePlayer).sendMessage(message);
    }

    public void setTitle(EPlayer ePlayer, String title) {
        getPlayer(ePlayer).setTitle(title);
    }

    public int getTitleColor(EPlayer ePlayer) {
        return getPlayer(ePlayer).getAppearance().getTitleColor();
    }

    public void setTitleColor(EPlayer ePlayer, int color) {
        getPlayer(ePlayer).getAppearance().setTitleColor(color);
    }

    public void cancelDecay(EPlayer ePlayer) {
        DecayTaskManager.getInstance().cancel(getPlayer(ePlayer));
    }

    public void setFullHealth(EPlayer ePlayer) {
        L2PcInstance player = getPlayer(ePlayer);

        player.setCurrentCp(player.getMaxCp());
        player.setCurrentHp(player.getMaxHp());
        player.setCurrentMp(player.getMaxMp());
    }

    public void setHealth(EPlayer ePlayer, double cp, double hp, double mp) {
        L2PcInstance player = getPlayer(ePlayer);

        player.setCurrentCp(cp);
        player.setCurrentHp(hp);
        player.setCurrentMp(mp);
    }

    public void setCp(EPlayer ePlayer, double value) {
        getPlayer(ePlayer).setCurrentCp(value);
    }

    public void setHp(EPlayer ePlayer, double value) {
        getPlayer(ePlayer).setCurrentHp(value);
    }

    public void setMp(EPlayer ePlayer, double value) {
        getPlayer(ePlayer).setCurrentMp(value);
    }

    public void updateAndBroadcastStatus(EPlayer ePlayer, int value) {
        getPlayer(ePlayer).updateAndBroadcastStatus(value);
    }

    public void revive(EPlayer ePlayer) {
        getPlayer(ePlayer).doRevive();
    }

    public void teleToLocation(EPlayer ePlayer, ELocation eLocation) {
        Location location = getLocation(eLocation);
        L2PcInstance player = getPlayer(ePlayer);

        player.teleToLocation(location, false);
    }

    public void addItems(EPlayer ePlayer, Collection<EItemHolder> eItemHolders) {
        L2PcInstance player = getPlayer(ePlayer);

        for (EItemHolder eItemHolder : eItemHolders) {
            player.addItem("eventReward", eItemHolder.getId(), eItemHolder.getCount(), null, true);
        }
    }

    public void applyEffects(EPlayer ePlayer, Collection<ESkillHolder> eSkillHolders) {
        L2PcInstance player = getPlayer(ePlayer);

        for (ESkillHolder eSkillHolder : eSkillHolders) {
            SkillHolder skillHolder = getSkillHolder(eSkillHolder);
            skillHolder.getSkill().applyEffects(player, player);
        }
    }

    public void cancelAllPlayerActions(EPlayer ePlayer) {
        L2PcInstance player = getPlayer(ePlayer);

        // Cancel target
        player.setTarget(null);
        // Cancel any attack in progress
        player.breakAttack();
        // Cancel any skill in progress
        player.breakCast();
    }

    public void cancelAllEffects(EPlayer ePlayer, int maxDelaySkill) {
        L2PcInstance player = getPlayer(ePlayer);

        // Stop all effects
        player.stopAllEffects();
        // Check Transform
        if (player.isTransformed())
        {
            player.untransform();
        }
        // Check Summon's and pets
        if (player.hasSummon())
        {
            final L2Summon summon = player.getSummon();
            summon.stopAllEffectsExceptThoseThatLastThroughDeath();
            summon.abortAttack();
            summon.abortCast();
            // Remove
            summon.unSummon(player);
        }

        // Cancel all character cubics
        for (L2CubicInstance cubic : player.getCubics().values())
        {
            cubic.stopAction();
            cubic.cancelDisappear();
        }
        // Stop any cubic that has been given by other player
        player.stopCubicsByOthers();

        // Remove player from his party
        final L2Party party = player.getParty();
        if (party != null)
        {
            party.removePartyMember(player, L2Party.messageType.Expelled);
        }

        // Remove Agathion
        if (player.getAgathionId() > 0)
        {
            player.setAgathionId(0);
            player.broadcastUserInfo();
        }

        // Remove reuse delay skills
        for (Skill skill : player.getAllSkills())
        {
            if (skill.getReuseDelay() <= maxDelaySkill)
            {
                player.enableSkill(skill);
            }
        }
        // Check Skills
        player.sendSkillList();
        player.sendPacket(new SkillCoolTime(player));
    }

    // ------------- REPOSITORY -------------

    private L2PcInstance getPlayer(EPlayer ePlayer) {
        return L2World.getInstance().getPlayer(ePlayer.getId());
    }

    private SkillHolder getSkillHolder(ESkillHolder eSkillHolder) {
        return new SkillHolder(eSkillHolder.getSkillId(), eSkillHolder.getSkillLevel());
    }

    private Location getLocation(ELocation eLocation) {
        Location location = new Location(eLocation.getX(), eLocation.getY(), eLocation.getZ());
        location.setInstanceId(eLocation.getInstanceId());
        location.setHeading(eLocation.getHeading());
        return location;
    }

    public static APIPlayer getInstance() {
        if (sInstance == null) sInstance = new APIPlayer();
        return sInstance;
    }
}
