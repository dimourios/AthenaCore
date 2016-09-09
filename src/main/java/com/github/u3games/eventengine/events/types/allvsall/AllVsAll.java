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
package com.github.u3games.eventengine.events.types.allvsall;

import java.util.List;

import com.github.u3games.eventengine.builders.TeamsBuilder;
import com.github.u3games.eventengine.config.BaseConfigLoader;
import com.github.u3games.eventengine.datatables.MessageData;
import com.github.u3games.eventengine.dispatcher.events.OnDeathEvent;
import com.github.u3games.eventengine.dispatcher.events.OnKillEvent;
import com.github.u3games.eventengine.enums.CollectionTarget;
import com.github.u3games.eventengine.enums.ListenerType;
import com.github.u3games.eventengine.enums.ScoreType;
import com.github.u3games.eventengine.events.handler.AbstractEvent;
import com.github.u3games.eventengine.events.holders.PlayerHolder;
import com.github.u3games.eventengine.util.EventUtil;
import com.github.u3games.eventengine.util.SortUtils;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * @author fissban
 */
public class AllVsAll extends AbstractEvent
{
	// Time for resurrection
	private static final int TIME_RES_PLAYER = 10;
	
	public AllVsAll()
	{
		super(getConfig().getInstanceFile());
	}

	private static AllVsAllEventConfig getConfig()
	{
		return BaseConfigLoader.getInstance().getAllVsAllConfig();
	}
	
	@Override
	protected TeamsBuilder onCreateTeams()
	{
		return new TeamsBuilder()
				.addTeam(getConfig().getCoordinates())
				.setPlayers(getPlayerEventManager().getAllEventPlayers());
	}
	
	@Override
	protected void onEventStart()
	{
		addSuscription(ListenerType.ON_KILL);
		addSuscription(ListenerType.ON_DEATH);

		for (PlayerHolder ph : getPlayerEventManager().getAllEventPlayers())
		{
			updateTitle(ph);
		}
	}
	
	@Override
	protected void onEventFight()
	{
		// Nothing
	}
	
	@Override
	protected void onEventEnd()
	{
		giveRewardsTeams();
	}
	
	// LISTENERS -----------------------------------------------------------------------
	@Override
	public void onKill(OnKillEvent event)
	{
		PlayerHolder ph = getPlayerEventManager().getEventPlayer(event.getAttacker());
		L2Character target = event.getTarget();

		// Increase the amount of one character kills
		ph.increaseKills();
		updateTitle(ph);
		
		// Reward for kills
		if (getConfig().isRewardKillEnabled())
		{
			giveItems(ph, getConfig().getRewardKill());
		}
		// Reward PvP for kills
		if (getConfig().isRewardPvPKillEnabled())
		{
			ph.getPcInstance().setPvpKills(ph.getPcInstance().getPvpKills() + getConfig().getRewardPvPKill());
			EventUtil.sendEventMessage(ph, MessageData.getInstance().getMsgByLang(ph.getPcInstance(), "reward_text_pvp", true).replace("%count%", getConfig().getRewardPvPKill() + ""));
		}
		// Reward fame for kills
		if (getConfig().isRewardFameKillEnabled())
		{
			ph.getPcInstance().setFame(ph.getPcInstance().getFame() + getConfig().getRewardFameKill());
			EventUtil.sendEventMessage(ph, MessageData.getInstance().getMsgByLang(ph.getPcInstance(), "reward_text_fame", true).replace("%count%", getConfig().getRewardFameKill() + ""));
		}
		// Message Kill
		if (BaseConfigLoader.getInstance().getMainConfig().isKillerMessageEnabled())
		{
			EventUtil.messageKill(ph, target);
		}
	}
	
	@Override
	public void onDeath(OnDeathEvent event)
	{
		PlayerHolder ph = getPlayerEventManager().getEventPlayer(event.getTarget());

		// We generated a task to revive the player
		giveResurrectPlayer(ph, TIME_RES_PLAYER, _radius);
		// Increase the amount of one character deaths.
		ph.increaseDeaths();
		// We update the title character
		updateTitle(ph);
	}
	
	// VARIOUS METHODS ------------------------------------------------------------------
	/**
	 * Update the title of a character depending on the number of deaths or kills have.
	 * @param ph
	 */
	
	private void updateTitle(PlayerHolder ph)
	{
		// Adjust the title character
		ph.setNewTitle("Kills " + ph.getKills() + " | " + ph.getDeaths() + " Death");
		// We update the status of the character
		ph.getPcInstance().updateAndBroadcastStatus(2);
	}
	
	/**
	 * Give rewards.
	 */
	public void giveRewardsTeams()
	{
		if (getPlayerEventManager().getAllEventPlayers().isEmpty())
		{
			return;
		}
		
		List<PlayerHolder> listOrdered = SortUtils.getOrdered(getPlayerEventManager().getAllEventPlayers(), ScoreType.KILL).get(0);
		String winners = "";
		// Get the players with more kills and less deaths
		for (PlayerHolder ph : SortUtils.getOrdered(listOrdered, ScoreType.DEATH, SortUtils.Order.ASCENDENT).get(0))
		{
			winners += ph.getPcInstance().getName();
			giveItems(ph, getConfig().getReward());
		}
		EventUtil.announceTo(Say2.CRITICAL_ANNOUNCE, "ava_first_place", "%holder%", winners, CollectionTarget.ALL_PLAYERS_IN_EVENT);
	}
}