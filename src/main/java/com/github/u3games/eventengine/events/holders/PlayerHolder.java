/*
 * Copyright (C) 2015-2016 L2J EventEngine
 *
 * This file is part of L2J EventEngine.
 *
 * L2J EventEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J EventEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.u3games.eventengine.events.holders;

import com.github.u3games.eventengine.datatables.BuffListData;
import com.github.u3games.eventengine.enums.TeamType;
import com.github.u3games.eventengine.events.listeners.EventEngineListener;
import com.github.u3games.eventengine.interfaces.ParticipantHolder;
import com.github.u3games.eventengine.model.ELocation;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2CubicInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.SkillCoolTime;
import com.l2jserver.gameserver.taskmanager.DecayTaskManager;

import java.util.Set;

/**
 * It manages player's info that participates in an event.
 * @author fissban
 */
public class PlayerHolder implements ParticipantHolder
{
	// Max delay time for reuse skill
	private static final int MAX_DELAY_TIME_SKILL = 900000;

	private final L2PcInstance _player;
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
	private Location _returnLocation;
	private int _dinamicInstanceId = 0;
	
	// Variable used to know the protection time inside events
	private long _protectionTimeEnd = 0;
	
	/**
	 * Constructor.
	 * @param player
	 */
	public PlayerHolder(L2PcInstance player)
	{
		_player = player;
	}
	
	// METODOS VARIOS -----------------------------------------------------------

	public TeamHolder getTeam()
	{
		return _team;
	}
	
	/**
	 * <b>Actions:</b>
	 * <ul>
	 * <li>Set the event team.</li>
	 * <li>Change the player color by team.</li>
	 * </ul>
	 * @param team
	 */
	public void setTeam(TeamHolder team)
	{
		// Set original title character
		_oriTitle = _player.getTitle();
		// Set original title color character
		_oriColorTitle = _player.getAppearance().getTitleColor();
		// Set team character
		_team = team;
		// Set team title character
		_player.setTitle(team.getName());
		// Set team color character
		_player.getAppearance().setTitleColor(team.getTeamType().getColor());
	}
	
	/**
	 * Get the player's team.
	 * @return
	 */
	public TeamType getTeamType()
	{
		return _team.getTeamType();
	}
	
	/**
	 * Get the event instance id.
	 * @return
	 */
	public int getDinamicInstanceId()
	{
		return _dinamicInstanceId;
	}
	
	/**
	 * Set the event instance id.
	 * @param dinamicInstanceId
	 */
	public void setDinamicInstanceId(int dinamicInstanceId)
	{
		_dinamicInstanceId = dinamicInstanceId;
	}
	
	/**
	 * Increase the kills by one.
	 */
	public void increaseKills()
	{
		_kills++;
	}
	
	/**
	 * Get the kills count.
	 * @return
	 */
	@Override
	public int getKills()
	{
		return _kills;
	}
	
	/**
	 * Increase the deaths by one.
	 */
	public void increaseDeaths()
	{
		_deaths++;
	}
	
	/**
	 * Get the deaths count.
	 * @return
	 */
	@Override
	public int getDeaths()
	{
		return _deaths;
	}
	
	/**
	 * Get the player's points.
	 * @return
	 */
	@Override
	public int getPoints()
	{
		return _kills - _deaths;
	}
	
	/**
	 * Set a player's title.
	 * @param title
	 */
	public void setNewTitle(String title)
	{
		_player.setTitle(title);
	}
	
	/**
	 * Recover the original player title.
	 */
	public void recoverOriginalTitle()
	{
		_player.setTitle(_oriTitle);
	}
	
	/**
	 * Recover the original color player title.
	 */
	public void recoverOriginalColorTitle()
	{
		_player.getAppearance().setTitleColor(_oriColorTitle);
	}
	
	/**
	 * Get the original location before teleporting to the event.
	 * @return
	 */
	public Location getReturnLoc()
	{
		return _returnLocation;
	}
	
	/**
	 * Set the original location before teleporting to the event.
	 * @param loc
	 */
	public void setReturnLoc(Location loc)
	{
		_returnLocation = loc;
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
	
	public void sendMessage(String message)
	{
		_player.sendMessage(message);
	}

	// TODO: Change in the future
	public void teleToLocation(ELocation loc)
	{
		Location coreLoc = new Location(loc.getX(), loc.getY(), loc.getZ());
		coreLoc.setInstanceId(loc.getInstanceId());
		_player.teleToLocation(coreLoc, false);
	}

	public void updateAndBroadcastStatus(int value)
	{
		_player.updateAndBroadcastStatus(value);
	}

	public ELocation getLocation()
	{
		return new ELocation(_player.getLocation().getX(), _player.getLocation().getY(), _player.getLocation().getZ());
	}

	public String getName()
	{
		return _player.getName();
	}

	public int getObjectId()
	{
		return _player.getObjectId();
	}

	public boolean isDead()
	{
		return _player.isDead();
	}

	public void cancelDecay()
	{
		DecayTaskManager.getInstance().cancel(_player);
	}

	public void revive()
	{
		_player.doRevive();
	}

	public void setFullHealth()
	{
		setCurrentHealth(_player.getMaxCp(), _player.getMaxHp(), _player.getMaxMp());
	}

	public void setCurrentHealth(int cp, int hp, int mp)
	{
		_player.setCurrentCp(cp);
		_player.setCurrentHp(hp);
		_player.setCurrentMp(mp);
	}

	public void addItems(ItemHolder... items)
	{
		for (ItemHolder item : items)
		{
			_player.addItem("eventReward", item.getId(), item.getCount(), null, true);
		}
	}

	public void cancelAllPlayerActions()
	{
		// Cancel target
		_player.setTarget(null);
		// Cancel any attack in progress
		_player.breakAttack();
		// Cancel any skill in progress
		_player.breakCast();
	}

	public void cancelAllEffects()
	{
		// Stop all effects
		_player.stopAllEffects();
		// Check Transform
		if (_player.isTransformed())
		{
			_player.untransform();
		}
		// Check Summon's and pets
		if (_player.hasSummon())
		{
			final L2Summon summon = _player.getSummon();
			summon.stopAllEffectsExceptThoseThatLastThroughDeath();
			summon.abortAttack();
			summon.abortCast();
			// Remove
			summon.unSummon(_player);
		}

		// Cancel all character cubics
		for (L2CubicInstance cubic : _player.getCubics().values())
		{
			cubic.stopAction();
			cubic.cancelDisappear();
		}
		// Stop any cubic that has been given by other player
		_player.stopCubicsByOthers();

		// Remove player from his party
		final L2Party party = _player.getParty();
		if (party != null)
		{
			party.removePartyMember(_player, L2Party.messageType.Expelled);
		}

		// Remove Agathion
		if (_player.getAgathionId() > 0)
		{
			_player.setAgathionId(0);
			_player.broadcastUserInfo();
		}

		// Remove reuse delay skills
		for (Skill skill : _player.getAllSkills())
		{
			if (skill.getReuseDelay() <= MAX_DELAY_TIME_SKILL)
			{
				_player.enableSkill(skill);
			}
		}
		// Check Skills
		_player.sendSkillList();
		_player.sendPacket(new SkillCoolTime(_player));
	}

	public void removePlayerFromEvent()
	{
		// Recovers player's title and color
		recoverOriginalColorTitle();
		recoverOriginalTitle();
		// Remove the player from world instance
		InstanceManager.getInstance().getPlayerWorld(_player).removeAllowed(_player.getObjectId());
		_player.setInstanceId(0);
		// Remove the player from event listener (it's used to deny the manual res)
		_player.removeEventListener(EventEngineListener.class);
		_player.teleToLocation(getReturnLoc());
	}

	public void applyEffects(SkillHolder... skills)
	{
		for (SkillHolder sh : skills)
		{
			sh.getSkill().applyEffects(_player, _player);
		}
	}

	public void applyEffects(Set<SkillHolder> skills)
	{
		for (SkillHolder sh : skills)
		{
			sh.getSkill().applyEffects(_player, _player);
		}
	}
}