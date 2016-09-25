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
package com.github.u3games.eventengine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.github.u3games.eventengine.api.adapter.EventEngineAdapter;
import com.github.u3games.eventengine.ai.NpcManager;
import com.github.u3games.eventengine.config.BaseConfigLoader;
import com.github.u3games.eventengine.core.model.ELocation;
import com.github.u3games.eventengine.core.model.entity.EPlayer;
import com.github.u3games.eventengine.core.model.ETask;
import com.github.u3games.eventengine.datatables.BuffListData;
import com.github.u3games.eventengine.datatables.EventData;
import com.github.u3games.eventengine.datatables.MessageData;
import com.github.u3games.eventengine.dispatcher.events.OnLogInEvent;
import com.github.u3games.eventengine.dispatcher.events.OnLogOutEvent;
import com.github.u3games.eventengine.enums.EventEngineState;
import com.github.u3games.eventengine.events.handler.AbstractEvent;
import com.github.u3games.eventengine.manager.RegistrationManager;
import com.github.u3games.eventengine.manager.VoteManager;
import com.github.u3games.eventengine.security.DualBoxProtection;
import com.github.u3games.eventengine.task.EventEngineTask;

/**
 * @author fissban
 */
public class EventEngineManager
{
	private static final Logger LOGGER = Logger.getLogger(EventEngineManager.class.getName());
	
	/**
	 * Constructor
	 */
	public EventEngineManager()
	{
		load();
	}
	
	/**
	 * It loads all the dependencies needed by EventEngine.
	 */
	private void load()
	{
		try
		{
			// Load the adapter to L2J Core
			EventEngineAdapter.class.newInstance();
			LOGGER.info(EventEngineManager.class.getSimpleName() + ": Adapter loaded.");
			// Load event configs
			BaseConfigLoader.getInstance();
			LOGGER.info(EventEngineManager.class.getSimpleName() + ": New Configs loaded.");
			EventData.getInstance();
			LOGGER.info(EventEngineManager.class.getSimpleName() + ": Events loaded.");
			VoteManager.getInstance().initVotes();
			// Load buff list
			BuffListData.getInstance();
			LOGGER.info(EventEngineManager.class.getSimpleName() + ": Buffs loaded.");
			// Load Multi-Language System
			MessageData.getInstance();
			LOGGER.info(EventEngineManager.class.getSimpleName() + ": Multi-Language system loaded.");
			// Load Npc Manager
			NpcManager.class.newInstance();
			LOGGER.info(EventEngineManager.class.getSimpleName() + ": AI's loaded.");
			// Launch main timer
			_time = 0;
			ETask.newInstance(new EventEngineTask(), 10 * 1000, 1000);
			LOGGER.info(EventEngineManager.class.getSimpleName() + ": Timer loaded.");
		}
		catch (Exception e)
		{
			LOGGER.warning(EventEngineManager.class.getSimpleName() + ": load() " + e);
			e.printStackTrace();
		}
	}
	
	// XXX EventEngineTask ------------------------------------------------------------------------------------
	private int _time;
	
	public int getTime()
	{
		return _time;
	}
	
	public void setTime(int time)
	{
		_time = time;
	}
	
	public void decreaseTime()
	{
		_time--;
	}
	
	// XXX NEXT EVENT ---------------------------------------------------------------------------------
	private Class<? extends AbstractEvent> _nextEvent;
	
	/**
	 * Get the next event type.
	 * @return
	 */
	public Class<? extends AbstractEvent> getNextEvent()
	{
		return _nextEvent;
	}
	
	/**
	 * Set the next event type.
	 * @param event
	 */
	public void setNextEvent(Class<? extends AbstractEvent> event)
	{
		_nextEvent = event;
	}
	
	// XXX CURRENT EVENT ---------------------------------------------------------------------------------
	// Event that is running
	private AbstractEvent _currentEvent;
	
	/**
	 * Get the event currently running.
	 * @return
	 */
	public AbstractEvent getCurrentEvent()
	{
		return _currentEvent;
	}
	
	/**
	 * Define the event that shall begin to run.
	 * @param event
	 */
	public void setCurrentEvent(AbstractEvent event)
	{
		_currentEvent = event;
	}
	
	// XXX LISTENERS -------------------------------------------------------------------------------------
	/**
	 * Listener when the player logout.
	 * @param event
	 */
	public void listenerOnLogout(OnLogOutEvent event)
	{
		if (_currentEvent == null)
		{
			if ((_state == EventEngineState.REGISTER) || (_state == EventEngineState.VOTING))
			{
				EPlayer player = event.getPlayer();
				//DualBoxProtection.getInstance().removeConnection(player.getClient());
				VoteManager.getInstance().removeVote(player);
				RegistrationManager.getInstance().unRegisterPlayer(player);
			}
		}
	}
	
	/**
	 * @param event
	 */
	public void listenerOnLogin(OnLogInEvent event)
	{
		EPlayer player = event.getPlayer();
		returnPlayerDisconnected(player);

		player.sendPartyRoomCommander("event_login_participate", true);
		player.sendPartyRoomCommander("event_login_vote", true);
	}

	// XXX EVENT STATE -----------------------------------------------------------------------------------
	// Variable charge of controlling at what moment will be able to register users to events
	private EventEngineState _state = EventEngineState.WAITING;

	/**
	 * Check what is the state that have the engine.
	 * @return EventState
	 */
	public EventEngineState getEventEngineState()
	{
		return _state;
	}

	/**
	 * Define the state in which the event is.<br>
	 * <u>Observations:</u>
	 * <li>REGISTER Indicate that it is.</li><br>
	 * @param state
	 */
	public void setEventEngineState(EventEngineState state)
	{
		_state = state;
	}

	/**
	 * Get if the EventEngine is waiting to start a register or voting time.
	 * @return boolean
	 */
	public boolean isWaiting()
	{
		return _state == EventEngineState.WAITING;
	}

	/**
	 * Get if the EventEngine is running an event.
	 * @return boolean
	 */
	public boolean isRunning()
	{
		return (_state == EventEngineState.RUNNING_EVENT) || (_state == EventEngineState.RUN_EVENT);
	}

	/**
	 * Check whether you can continue registering more users to events.
	 * @return boolean
	 */
	public boolean isOpenRegister()
	{
		return _state == EventEngineState.REGISTER;
	}

	/**
	 * Check whether you can continue registering more users to events.
	 * @return boolean
	 */
	public boolean isOpenVote()
	{
		return _state == EventEngineState.VOTING;
	}
	
	// XXX MISC ---------------------------------------------------------------------------------------
	
	private final Map<Integer, ELocation> _playersDisconnected = new ConcurrentHashMap<>();
	
	/**
	 * When the player is disconnected inside event. It adds him to a list saving the original location.
	 * @param player
	 */
	public void addPlayerDisconnected(EPlayer player)
	{
		_playersDisconnected.put(player.getId(), player.getReturnLoc());
		
	}
	
	/**
	 * When the player relogs. It teleports him to the original location if he disconnected inside event.
	 * @param player
	 */
	public void returnPlayerDisconnected(EPlayer player)
	{
		ELocation returnLoc = _playersDisconnected.get(player.getId());
		if (returnLoc != null)
		{
			player.teleToLocation(returnLoc);
		}
	}
	
	/**
	 * Cleanup variables to the next event.
	 */
	public void cleanUp()
	{
		DualBoxProtection.getInstance().clearAllConnections();
		setCurrentEvent(null);
		VoteManager.getInstance().clearVotes();
		RegistrationManager.getInstance().clearRegisteredPlayers();
	}
	
	public static EventEngineManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final EventEngineManager _instance = new EventEngineManager();
	}
}