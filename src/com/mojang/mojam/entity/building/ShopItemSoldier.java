package com.mojang.mojam.entity.building;

import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.IRemoveEntityNotify;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.entity.mob.pather.AvoidableObject;
import com.mojang.mojam.entity.mob.pather.Soldier;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class ShopItemSoldier extends ShopItem implements IRemoveEntityNotify{

	Bitmap baseSprite;
	List<Soldier> soldiers=new ArrayList<Soldier>();
	
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
			level.addEntity(soldier);
		}
	}
	
	public void tick() {
		//check if player is the right level to buy stuff!
		//checkPlayerLevel();
		checkSoldierList();
		
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
	
	protected void checkSoldierList() {
		//OMG this is nasty.. but its just POC
		//really should just add a counter not a list and
		//decrease on the soldiers death.
		//a push instead of a poll
		//int i=0;
		//int ii=soldiers.size();
		//for (i=0;i<ii;i++) {
		//	if(soldiers.get(i).removed) {
		//		soldiers.remove(i);
		//		i--;
		//		ii--;
		//	}
		//}
	}

	@Override
	public void removeEntityNotice(Entity e) {
		soldiers.remove(e);
		
	}
}
