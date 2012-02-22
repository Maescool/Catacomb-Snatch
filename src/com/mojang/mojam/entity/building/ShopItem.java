package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;

import com.mojang.mojam.entity.*;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.screen.*;

public abstract class ShopItem extends Building {

	protected int facing = 0;
	private int effectiveCost;

	public ShopItem(double x, double y, int team) {
		super(x, y, team);
		isImmortal = true;
		if (team == Team.Team1) {
			facing = 4;
		}
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
		// Bitmap image = getSprite();
		Font.drawCentered(screen, MojamComponent.texts.cost(effectiveCost), (int) (pos.x), (int) (pos.y + 10));
	}

	public void init() {
		effectiveCost = DifficultyInformation.calculateCosts(getCost());
	}


	public void tick() {
		super.tick();
	}


	@Override
	public void use(Entity user) {
		if (user instanceof Player && ((Player) user).getTeam() == team) {
			Player player = (Player) user;
			if (player.carrying == null && player.getScore() >= effectiveCost) {
				if(tryUse(player)) {
					player.payCost(effectiveCost);
					itemUsed(player);
				}
			} else if ( player.getScore() < effectiveCost ){
				Notifications.getInstance().add("You dont have enough money");
			}
		}
	}

	protected abstract int getCost();
	
	public abstract Bitmap getSprite();
	
	protected abstract void itemUsed(Player player);

	// Can be overriden to decline usage (i.e. player level too low)
	protected boolean tryUse(Player player) {
		return true;
	}
}
