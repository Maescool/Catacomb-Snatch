package com.mojang.mojam.entity.building;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.entity.mob.pather.Soldier;
import com.mojang.mojam.screen.Art;

public class ShopItemSoldier extends ShopItem {

	public ShopItemSoldier(double x, double y, int team) {
		super("soldier", x, y, team, 1000, 8);

		int facing = (team == 2) ? 0 : 4;

		if (team == Team.Team1) {
			setSprite(Art.getPlayer(GameCharacter.LordLard)[facing][0]);
		} else {
			setSprite(Art.getPlayer(GameCharacter.HerrVonSpeck)[facing][0]);
		}
	}

	public void useAction(Player player) {
		if (player.getTeam() == team) {
			level.addEntity(new Soldier(player.pos.x, player.pos.y, team,
					player));
		}
	}
}
