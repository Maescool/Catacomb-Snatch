package com.mojang.mojam.entity.building;

import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.IRemoveEntityNotify;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.entity.mob.pather.Soldier;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

/**
 * Used to buy Soldiers
 * 
 * @see Soldier
 * @author Morgan Gilroy
 */
public class ShopItemSoldier extends ShopItem implements IRemoveEntityNotify{

	Bitmap baseSprite; //cache the sprite locally  
	List<Soldier> soldiers=new ArrayList<Soldier>(); //keep a list of soldiers so we can count them 
	
	public ShopItemSoldier(double x, double y, int team) {
		super("soldier", x, y, team, 1000, 8);

		int facing = (team == 2) ? 0 : 4;

		if (team == Team.Team1) {
			baseSprite=Art.getPlayer(GameCharacter.LordLard)[facing][0].copy();
		} else {
			baseSprite=Art.getPlayer(GameCharacter.HerrVonSpeck)[facing][0].copy();
		}
		setSprite(baseSprite);
	}

	public void useAction(Player player) {
		if (player.getTeam() == team && checkPlayerLevel(player)) {
			Soldier soldier = new Soldier(player.pos.x, player.pos.y, team,	player);
			soldiers.add(soldier);
			soldier.setSpawnSource(this); //set the spawnSource to this so when the soldier dies it can call back here
			level.addEntity(soldier);
		}
	}
	
	/**
	 * Check if the Player can buy from this shop if not Gray it out with a fluctuating gray 
	 */
	public void tick() {
		if (!( this.team == MojamComponent.localTeam && checkPlayerLevel(MojamComponent.localPlayer))) {
			Bitmap sprite=new Bitmap(baseSprite.w,baseSprite.h);
			int col=64+(int)(64*(Math.sin(System.currentTimeMillis() * .001)+1.5));
			sprite.colorBlit(baseSprite, 0, 0, (0xAA<<24)|(col)|(col<<8)|(col<<16));
			setSprite(sprite);
		} else {
			setSprite(baseSprite);
		}
	}
	
	protected boolean checkPlayerLevel(Player player) {
		if ( soldiers.size() > player.getPlevel())
			return false;
		return true;
	}
	
	/**
	 * Very basic observer setup, when the Soldier dies
	 * it calls back to the here so we can remove it from the list
	 */
	@Override
	public void removeEntityNotice(Entity e) {
		soldiers.remove(e);
		
	}
}
