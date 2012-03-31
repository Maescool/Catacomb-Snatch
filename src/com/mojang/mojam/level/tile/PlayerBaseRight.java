package com.mojang.mojam.level.tile;

import java.util.List;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

public class PlayerBaseRight extends Tile {
	public static final int COLOR = 0xFF707070;
	private static final String NAME = "PLAYER BASE RIGHT";
	private AbstractBitmap[][] art;
	private int playerID;

	public PlayerBaseRight(int img, int team) {
		if (img > 5 || img < 0) {
			img = 5;
		}
		if(team == Team.Team1) {
			playerID = 0;
			minimapColor = 0xFF770000;
		}
		if(team == Team.Team2) {
			playerID = 1;
			minimapColor = 0xFF000077;
		}
		
		this.img = img;
	}
	
	public void init(Level level, int x, int y) {
		super.init(level, x, y);
	}

	@Override
	public void render(AbstractScreen screen) {
		//We need to determine the art here because the level is initialized before the player
		art = Art.getPlayerBaseRight(getPlayerCharacter(playerID));
	    screen.blit(art[img % 2][img / 2], x * Tile.WIDTH, y * Tile.HEIGHT);
	}
	
	private GameCharacter getPlayerCharacter(int playerID){
	    Player player = MojamComponent.instance.players[playerID];
	    if (player == null) return GameCharacter.None;
	    else return player.getCharacter();
	}
	
	public boolean canPass(Entity e) {
		return false;
	}

	public int getColor() {
		return PlayerBaseRight.COLOR;
	}

	public String getName() {
		return PlayerBaseRight.NAME;
	}
	
	@Override
	public void addClipBBs(List<BB> list, Entity e) {
		list.add(new BB(this, x * Tile.WIDTH + 5, y * Tile.HEIGHT + 5, (x + 1)
				* Tile.WIDTH - 5, (y + 1) * Tile.HEIGHT - 5));
	}
	
	@Override
	public boolean isBuildable() {
		return false;
	}
	
	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return art[img % 2][img / 2];
	}
}
