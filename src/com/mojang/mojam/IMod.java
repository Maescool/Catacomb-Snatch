package com.mojang.mojam;

import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.gamemode.RandomSpawner;
import com.mojang.mojam.network.Packet;

public interface IMod
{

	/**
	 * Called at the start of every synchronised tick in game, it
	 * can be used for almost anything.
	 * 
	 */
	public void OnTick();

	/**
	 * Called at the end of every synchronised tick, this is used in
	 * rare circumstances, such as measuring latency or tick times.
	 * 
	 */
	public void AfterTick();

	/**
	 * Called at the start of every render tick, this is used in rare
	 * circumstances, such as measuring render times. Anything drawn
	 * here will likely be drawn over later.
	 * 
	 */
	public void OnStartRender();

	/**
	 * Called at the end of every render tick, this is the main method
	 * used to add a custom render item to the game, since this will be
	 * on top of everything else.
	 * 
	 */
	public void OnRender();

	/**
	 * Called once before the main {@link MojamComponent} game loop. Used
	 * for initialising scripts, since mods can use their constructors.
	 */
	public void RunOnce();

	/**
	 * Called when the window is closed, used for shutting down scripts.
	 * 
	 */
	public void OnClose();

	/**
	 * Called whenever a packet is sent to another player. Can not be
	 * intercepted, but can be recorded.
	 * 
	 * @param packet
	 *            The {@link Packet} being sent over the network
	 * @see Packet
	 */
	public void OnSendPacket(Packet packet);

	/**
	 * Called upon the victory of one player or team, so that fanfare
	 * can be given.
	 * 
	 * @param team
	 *            The winning {@link Team}
	 * @see Team
	 * @see com.mojang.mojam.level.gamemode.IVictoryConditions
	 */
	public void OnVictory(int team);

	/**
	 * Called whenever the level itself ticks, such as respawning
	 * spawners or spawning mobs. This can be used to manipulate
	 * the world in all sorts of ways.
	 * 
	 * @param level
	 *            The {@link Level} being ticked
	 * @see Level
	 */
	public void OnLevelTick(Level level);

	/**
	 * Called whenever a packet is received from another player.
	 * Can (possibly) be intercepted.
	 * 
	 * @param packet
	 *            The {@link Packet} being received over the network
	 * @see Packet
	 */
	public void OnReceivePacket(Packet packet);
	/**
	 * Called whenever a packet is sent or received. Can not be
	 * intercepted, but can be recorded.
	 * 
	 * @param packet
	 *            The {@link Packet} being sent over the network
	 * @see Packet
	 */
	public void HandlePacket(Packet packet);

	/**
	 * Called upon the level being created. This can change fields
	 * such as gametype, map size and layout.
	 * 
	 * @param level
	 *            The {@link Level} being created
	 * @see Level
	 */
	public void CreateLevel(Level level);

	/**
	 * Called whenever an entity is spawned. It cycles through
	 * loaded mods, and until it is returned an entity. Use it
	 * in conjunction with the i value and registering entities
	 * so that <br>
	 * {@code if(i==myEntityId)return new MyEntity(i,x,y);}
	 * 
	 * 
	 * @param i
	 *            The id of the Entity to spawn
	 * @param x
	 *            The x location of the new Entity to Spawn
	 * @param y
	 *            the y location of the new Entity to Spawn
	 * @return A new instance of your registered entity
	 * @see SpawnerEntity
	 * @see RandomSpawner
	 * @see Entity
	 */
	public Entity getEntityInstanceById(int i, double x, double y);

	/**
	 * Called on every tick that the key is down
	 * 
	 * @param key
	 *            The key that is down
	 */
	public void IfKeyDown(Key key);

	/**
	 * Called on every tick that the key is up
	 * 
	 * @param key
	 *            The key that is up
	 */
	public void IfKeyUp(Key key);

	/**
	 * To be called when displaying an error
	 * 
	 * @return The mod's version
	 * @see Snatch
	 */
	public String getVersion();

	/**
	 * Called upon a console command
	 * 
	 * @param command The command given
	 * @param params The rest of the console line
	 * @return 1 if it has been responded to, 0 otherwise
	 */
	public int OnConsole(String command, String params);
}
