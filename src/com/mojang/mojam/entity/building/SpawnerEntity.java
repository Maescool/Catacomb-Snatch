package com.mojang.mojam.entity.building;

import java.lang.reflect.Constructor;

import com.mojang.mojam.entity.mob.HostileMob;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.SpawnableEnemy;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

/**
 * Spawner entity. A sarcophage which spawns enemies of a given type onto the field.
 */
public class SpawnerEntity extends Building {
	/** Spawn interval in frames*/
	public static final int SPAWN_INTERVAL = 60 * 4;

	public int spawnTime = 0;

	private SpawnableEnemy type;

	private int lastIndex = 0;

	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param type Mob type
	 */
	public SpawnerEntity(double x, double y, SpawnableEnemy type) {
		super(x, y, Team.Neutral);

		this.type = type;
		setStartHealth(20);
		freezeTime = 10;
		spawnTime = TurnSynchronizer.synchedRandom.nextInt(SPAWN_INTERVAL);
		minimapIcon = 4;
		healthBarOffset = 15;
		deathPoints = type.getType() * 5 + 5;
	}

	@Override
	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;

		if (--spawnTime <= 0) {
			spawn();
			spawnTime = DifficultyInformation.calculateSpawntime(SPAWN_INTERVAL);
		}
	}

	/**
	 * Spawn a new enemy of the given type onto the field.
	 */
	private void spawn() {
		double x = pos.x + (TurnSynchronizer.synchedRandom.nextFloat() - 0.5)
				* 5;
		double y = pos.y + (TurnSynchronizer.synchedRandom.nextFloat() - 0.5)
				* 5;
		x=Math.max(Math.min(x, level.width*Tile.WIDTH), 0);//spawn only inside the level!
		y=Math.max(Math.min(y, level.height*Tile.HEIGHT), 0);
		int xin=(int)x/ Tile.WIDTH;
		int yin=(int)y/ Tile.HEIGHT;
		Tile spawntile = level.getTile(xin, yin);

		try {
			//This whole try/catch block is to create the new enemy of the given type using reflection. This will allow new enemies to be added without touching this 
			//code as long as they are defined in SpawnableEnemies enum and have a public constructor that takes (double, double)
			Mob te = null;
			Constructor<? extends HostileMob> con = this.type.getClazz().getConstructor(new Class[]{double.class, double.class});
			te = con.newInstance(new Object[]{x,y});
			if (level.countEntities(Mob.class) < level.maxMonsters && level.getEntities(te.getBB().grow(8), te.getClass()).size() == 0 && spawntile.canPass(te))
				level.addEntity(te);
		} catch (Exception e) {
			//Something went wrong with the reflection creation of the Enemy. Here are a few things that could've gone wrong:
			//The constructor with arguments double, double is not defined as Public
			//There is no constructor with arguments double, double
			e.printStackTrace();
		} 		
	}

	@Override
	public Bitmap getSprite() {
		int newIndex = (int)(3 - (3 * health) / maxHealth);
		if (newIndex != lastIndex) {
			// if (newIndex > lastIndex) // means more hurt
			// level.addEntity(new SmokeAnimation(pos.x - 12, pos.y - 20,
			// Art.fxSteam24, 40));
			lastIndex = newIndex;
		}
		return Art.mobSpawner[newIndex][0];
	}
}
