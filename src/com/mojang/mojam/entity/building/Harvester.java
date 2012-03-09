package com.mojang.mojam.entity.building;

import java.awt.Color;
import java.util.Random;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.animation.SmokeAnimation;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.loot.LootCollector;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

/**
 * Harvester building. Automatically collects all coins within a given radius around itself
 */
public class Harvester extends Building implements LootCollector {
	private int capacity = 1500;

	private int time = 0;
	private int harvestingTicks = 20;
	private boolean isHarvesting = false;
	private boolean isEmptying = false;
	private Player emptyingPlayer = null;
	private int emptyingSpeed = 50;
	public int radius;
	private int[] upgradeRadius = new int[] { (int) (1.5 * Tile.WIDTH),
			2 * Tile.WIDTH, (int) (2.5 * Tile.WIDTH) };
	private int[] upgradeCapacities = new int[] { 1500, 2500, 3500 };
	
	
	private AbstractBitmap areaBitmap;
	private boolean updateAreaBitmap;
	private static final int RADIUS_COLOR = new Color(240, 210, 190).getRGB();
	
	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param team Team number
	 */
	public Harvester(double x, double y, int team) {
		super(x, y, team);
		setStartHealth(10);
		freezeTime = 10;
		yOffs = 20;
		//TODO should this upgrade cost change with Difficulty like Turret?
		makeUpgradeableWithCosts(new int[] { 500, 1000, 5000 });
		healthBarOffset = 13;
		updateAreaBitmap=true;
	}
	
	@Override
	public void notifySucking() {
		harvestingTicks = 30;
	}

	@Override
	public boolean isAllowedToCancel() {
		return !isEmptying;
	}

	@Override
	public void tick() {
		super.tick();
		if (--freezeTime > 0) {
			return;
		}

		if (isEmptying && (time % 3 == 0)) {
			if (money <= 0) {
				isEmptying = false;
			} else {
				Loot l = new Loot(pos.x, pos.y, 0, 0, 1);
				l.fake = true;
				l.life = 20;
				l.forceTake(emptyingPlayer);
				int toAdd = Math.min(emptyingSpeed, money);
				money -= toAdd;
				emptyingPlayer.addScore(toAdd);
				level.addEntity(l);
			}
		}

		++time;
		--harvestingTicks;
		isHarvesting = (harvestingTicks >= 0);

		Random random = TurnSynchronizer.synchedRandom;
		if (isHarvesting) {
			if (random.nextDouble() < 0.050f) {
				level.addEntity(new SmokeAnimation(pos.x - 6
						+ random.nextInt(8) - random.nextInt(8), pos.y - 16,
						Art.fxSteam12, 30));
			}
		} else {
			if (random.nextDouble() < 0.002f) {
				level.addEntity(new SmokeAnimation(pos.x - 6
						+ random.nextInt(8) - random.nextInt(8), pos.y - 16,
						Art.fxSteam12, 30));
			}
		}
		if (health == 0) {
			dropAllMoney();
		}
	}

	@Override
	public AbstractBitmap getSprite() {
		int frame = isHarvesting ? (4 + ((time >> 3) % 5)) : (time >> 3) % 4;
		switch (upgradeLevel) {
        case 1:
            return Art.harvester2[frame][0];
        case 2:
            return Art.harvester3[frame][0];
        default:
            return Art.harvester[frame][0];
        }
	}

	@Override
	protected void upgradeComplete() {
	    maxHealth += 10;
	    health += 10;
	    radius = upgradeRadius[upgradeLevel];
	    capacity = upgradeCapacities[upgradeLevel];
	    updateAreaBitmap = true;
	    if (upgradeLevel != 0) justDroppedTicks = 80; //show the radius for a brief time
	}

	/**
	 * Check if this collector still hascapacity for additional coins
	 * 
	 * @return True if remaining capacity is more than zero, false if not
	 */
	public boolean canTake() {
		return money < capacity;
	}

	@Override
	public void render(AbstractScreen screen) {
		
		if((justDroppedTicks-- > 0 || isHighlight()) && MojamComponent.localTeam==team) {
			drawRadius(screen);
		}
		
		super.render(screen);

		AbstractBitmap image = getSprite();
		if (capacity - money < 500) {
			screen.colorBlit(image, pos.x - image.getWidth() / 2, pos.y - image.getHeight() / 2 - yOffs, 0x77ff7200);
		}
		
		if( team == MojamComponent.localTeam && !(isCarried() && this.carriedBy instanceof Player)) {
				addMoneyBar(screen);
		}
		
	}
	
	/**
	 * Draw the money bar onto the given screen
	 * 
	 * @param screen AbstractScreen
	 */
	private void addMoneyBar(AbstractScreen screen) {
	    int start = (int) (money * 20 / capacity);
        screen.blit(Art.moneyBar[start][0], pos.x - 16, pos.y + 8);
    }
	
	@Override
	public void take(Loot loot) {
		loot.remove();
		money += loot.getScoreValue();
		if (money > capacity) {
			money = capacity;
		}
	}

	@Override
	public double getSuckPower() {
		return radius / 60.0;
	}

	@Override
	public void flash() {
		setFlashTime(5);
	}

	@Override
	public int getScore() {
		return money;
	}

	/**
	 * Drop all money, comes with a nice animation
	 */
	public void dropAllMoney() {
		while (money > 0) {
			double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI
					* 2;
			Loot loot = new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir),
					money / 2);
			level.addEntity(loot);

			money -= loot.getScoreValue();
		}
		money = 0;
	}

	@Override
	public void use(Entity user) {
		if(money > 0) {
			isEmptying = true;
			if (user instanceof Player) {
				emptyingPlayer = (Player) user;
			}
		} else {
			super.use(user);
		}
	}
	
	public void drawRadius(AbstractScreen screen) {
		if (updateAreaBitmap) {
		areaBitmap = screen.rangeBitmap(radius,RADIUS_COLOR);
		updateAreaBitmap = false;
		}
		screen.alphaBlit(areaBitmap, (int) pos.x-radius, (int) pos.y-radius - yOffs + Tile.HEIGHT/2, 0x22);	
	}
}
