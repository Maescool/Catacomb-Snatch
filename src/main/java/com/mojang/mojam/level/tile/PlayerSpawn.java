package com.mojang.mojam.level.tile;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

public class PlayerSpawn extends Tile {
	public static final int COLOR = 0xFFA5A5A5;
	protected static final String NAME = "Player Spawn";
	private AbstractBitmap[][] art;
	private int team;
	private int playerID;	

	public PlayerSpawn(int img, int team) {
		if (img > 11) {
			img = 11;
		}
		this.team = team;
		
		if(team == Team.Team1) {
			playerID = 0;
			minimapColor = 0xFFA50000;
		}
		if(team == Team.Team2) {
			playerID = 1;
			minimapColor = 0xFF0000A5;
		}
		
		this.img = img;
		
	}
	
	public void init(Level level, int x, int y) {
		super.init(level, x, y);
		level.addSpawnPoint(x * Tile.WIDTH + Tile.WIDTH / 2, y * Tile.HEIGHT + Tile.HEIGHT / 2, team);
	}

	@Override
	public void render(AbstractScreen screen) {
		//We need to determine the art here because the level is initialized before the player
		art = Art.getPlayerSpawn(getPlayerCharacter(playerID));
	    screen.blit(art[img % 4][img / 4], x * Tile.WIDTH, y * Tile.HEIGHT);
	}
	
	private GameCharacter getPlayerCharacter(int playerID){
	    Player player = MojamComponent.instance.players[playerID];
	    if (player == null) return GameCharacter.None;
	    else return player.getCharacter();
	}
	
	@Override
	public boolean canPass(Entity e) { //only allow players/loot/bullets to pass the spawn flag
		if ((e instanceof Player) || (e instanceof Loot) || (e instanceof Bullet)) {
			return true;
		}
		
		return false;
	}

	public int getColor() {
		return PlayerSpawn.COLOR;
	}

	public String getName() {
		return PlayerSpawn.NAME;
	}
	
	@Override
	public boolean isBuildable() {
		return true;
	}
	
	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return art[img % 4][img / 4];
	}
}
