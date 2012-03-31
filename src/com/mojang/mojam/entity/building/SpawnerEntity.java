package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.IEditable;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.mod.ModSystem;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

/**
 * Spawner entity. A sarcophage which spawns enemies of a given type onto the
 * field.
 */
public abstract class SpawnerEntity extends Building implements IEditable
{
	/** Spawn interval in frames */
	public static final int SPAWN_INTERVAL = 60 * 4;

	public int spawnTime = 0;

	private int lastIndex = 0;

	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 */
	public SpawnerEntity(double x, double y)
	{
		super(x, y, Team.Neutral);

		setStartHealth(20);
		freezeTime = 10;
		spawnTime = TurnSynchronizer.synchedRandom.nextInt(SPAWN_INTERVAL);
		minimapIcon = 4;
		healthBarOffset = 22;
		deathPoints = 0 * 5 + 5;
		yOffs = 0;
	}

	@Override
	public void tick()
	{
		super.tick();
		if(freezeTime > 0) return;

		if(--spawnTime <= 0)
		{
			spawn();
			spawnTime = TitleMenu.difficulty.calculateSpawntime(SPAWN_INTERVAL);
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

		Mob te = getMob(x,y);
		
		if (level.countEntities(Mob.class) < level.maxMonsters
				&& level.getEntities(te.getBB().grow(8), te.getClass()).isEmpty()
				&& spawntile.canPass(te))
			level.addMob(te,xin,yin);
	}

	protected abstract Mob getMob(double x, double y);

	@Override
	public AbstractBitmap getSprite() {
		int newIndex = (int)(3 - (3 * health) / maxHealth);
		if (newIndex != lastIndex) {
			// if (newIndex > lastIndex) // means more hurt
			// level.addEntity(new SmokeAnimation(pos.x - 12, pos.y - 20,
			// Art.fxSteam24, 40));
			lastIndex = newIndex;
		}
		return Art.mobSpawner[newIndex][0];
	}

	public static Entity getRandomSpawner(double x, double y)
	{

		int nextInt = TurnSynchronizer.synchedRandom.nextInt(ModSystem.numEntitiesLoaded());

		if(nextInt == 0) return new SpawnerForBat(x, y);
		if(nextInt == 1) return new SpawnerForSnake(x, y);
		if(nextInt == 2) return new SpawnerForMummy(x, y);
		if(nextInt == 3) return new SpawnerForScarab(x, y);
		return new SpawnerEntityMod(x,y,nextInt);
		//return new SpawnerForBat(x, y); //should never reach this
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.mobSpawner[0][0];
	}

	@Override
    public void render(AbstractScreen screen) {
	    super.render(screen);
	    screen.blit(Art.mobSpawnerShadow, pos.x - Art.mobSpawnerShadow.getWidth() / 2 - 1, pos.y - Art.mobSpawnerShadow.getHeight() / 2 + 7);
    }
	
	protected AbstractBitmap blitMobOnTop(AbstractBitmap mobicon) {
		AbstractBitmap bitmap = Art.mobSpawner[0][0].copy();
		bitmap.blit(mobicon, (bitmap.getWidth()/2)-mobicon.getWidth()/2,(bitmap.getHeight()/2)-5-mobicon.getHeight()/2);
		return bitmap;
	}
	
}
