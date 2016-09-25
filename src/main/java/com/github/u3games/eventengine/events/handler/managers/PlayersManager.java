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
package com.github.u3games.eventengine.events.handler.managers;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.u3games.eventengine.core.model.entity.EEntity;
import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.core.model.entity.ESummon;
import com.github.u3games.eventengine.manager.RegistrationManager;
import com.github.u3games.eventengine.repository.PlayerRepository;

/**
 * @author fissban
 */
public class PlayersManager
{
	private final Map<Integer, EPlayer> _eventPlayers = new ConcurrentHashMap<>();
	
	/**
	 * We obtain the full list of all players within an event.
	 * @return Collection<PlayerHolder>
	 */
	public Collection<EPlayer> getAllEventPlayers()
	{
		return _eventPlayers.values();
	}
	
	/**
	 * We add all the characters registered to our list of characters in the event.<br>
	 * Check if player in olympiad.<br>
	 * Check if player in duel.<br>
	 * Check if player in observer mode.
	 */
	public void createEventPlayers()
	{
		for (Integer playerId : RegistrationManager.getInstance().getAllRegisteredPlayers())
		{
			EPlayer player = PlayerRepository.getInstance().getPlayer(playerId);

			// Check if player in olympiad
			if (player.isInOlympiadMode())
			{
				player.sendMessage("You can not attend the event being in the Olympics.");
				continue;
			}
			// Check if player in duel
			if (player.isInDuel())
			{
				player.sendMessage("You can not attend the event being in the Duel.");
				continue;
			}
			// Check if player in observer mode
			if (player.isInObserverMode())
			{
				player.sendMessage("You can not attend the event being in the Observer mode.");
				continue;
			}
			_eventPlayers.put(player.getId(), player);
			//player.addEventListener(new EventEngineListener(player));
		}
		// We clean the list, no longer we need it
		RegistrationManager.getInstance().clearRegisteredPlayers();
	}
	
	/**
	 * Check if the playable is participating in any event. In the case of a summon, verify that the owner participates. For not participate in an event is returned <u>false.</u>
	 * @param entity
	 * @return boolean
	 */
	public boolean isPlayableInEvent(EEntity entity)
	{
		if (entity.isPlayer())
		{
			return _eventPlayers.containsKey(entity.getId());
		}
		
		if (entity.isSummon())
		{
			return _eventPlayers.containsKey(((ESummon) entity).getOwnerId());
		}
		return false;
	}
	
	/**
	 * Check if a player is participating in any event. In the case of dealing with a summon, verify the owner. For an event not participated returns <u>null.</u>
	 * @param entity
	 * @return PlayerHolder
	 */
	public EPlayer getEventPlayer(EEntity entity)
	{
		if (entity.isSummon())
		{
			return _eventPlayers.get(((ESummon) entity).getOwnerId());
		}
		if (entity.isPlayer())
		{
			return _eventPlayers.get(entity.getId());
		}
		return null;
	}
}