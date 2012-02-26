package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.IUsable;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Building extends Mob implements IUsable {
	public static final int SPAWN_INTERVAL = 60;
	public static final int MIN_BUILDING_DISTANCE = 1700; // Sqr
	public static final int HEALING_INTERVAL = 15;
	
	public int spawnTime = 0;
	public boolean highlight = false;
	private int healingTime = HEALING_INTERVAL;

	public Building(double x, double y, int team, int localTeam) {
		super(x, y, team, localTeam);

		setStartHealth(20);
		freezeTime = 10;
		spawnTime = TurnSynchronizer.synchedRandom.nextInt(SPAWN_INTERVAL);
	}


	
	@Override
	public void render(Screen screen) {
		super.render(screen);
		renderMarker(screen);
	}

	protected void renderMarker(Screen screen) {
		if (highlight) {
			BB bb = getBB();
			bb = bb.grow((getSprite().w - (bb.x1 - bb.x0)) / (3 + Math.sin(System.currentTimeMillis() * .01)));
			int width = (int) (bb.x1 - bb.x0);
			int height = (int) (bb.y1 - bb.y0);
			Bitmap marker = new Bitmap(width, height);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if ((x < 2 || x > width - 3 || y < 2 || y > height - 3) && (x < 5 || x > width - 6) && (y < 5 || y > height - 6)) {
						int i = x + y * width;
						marker.pixels[i] = 0xffffffff;
					}
				}
			}
			screen.blit(marker, bb.x0, bb.y0 - 4);
		}
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0) {
			return;
		}
		if (hurtTime <= 0) {
			if (health < maxHealth) {
				if (--healingTime <= 0) {
					++health;
					healingTime = HEALING_INTERVAL;
				}
			}
		}

		xd = 0.0;
		yd = 0.0;
	}

	@Override
	public void hurt(Bullet bullet) {
		super.hurt(bullet);
		healingTime = HEALING_INTERVAL;
	}

	@Override
	public void hurt(Entity source, float damage) {
		super.hurt(source, damage);
		healingTime = HEALING_INTERVAL;
	}

	public Bitmap getSprite() {
		return Art.floorTiles[3][2];
	}

	public boolean move(double xBump, double yBump) {
		return false;
	}

	public void slideMove(double xa, double ya) {
		super.move(xa, ya);
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
		fallDownHole();
=======
		checkForHoleTiles((int) pos.x/Tile.WIDTH, (int) pos.y/Tile.HEIGHT);
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
		checkForHoleTiles((int) pos.x/Tile.WIDTH, (int) pos.y/Tile.HEIGHT);
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
		checkForHoleTiles((int) pos.x/Tile.WIDTH, (int) pos.y/Tile.HEIGHT);
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
		checkForHoleTiles((int) pos.x/Tile.WIDTH, (int) pos.y/Tile.HEIGHT);
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
	}

	//
	// Upgrade
	//
	protected void upgradeComplete() {

	}

	protected int upgradeLevel = 0;
	private int maxUpgradeLevel = 0;
	private int[] upgradeCosts = null;

	public boolean upgrade(Player p) {
		if (upgradeLevel >= maxUpgradeLevel) {
			MojamComponent.soundPlayer.playSound("/sound/Fail.wav", (float) pos.x, (float) pos.y, true);
			if(this.team == this.localTeam) {
				Notifications.getInstance().add(MojamComponent.texts.getStatic("upgrade.full"));
			}
			return false;
		}

		final int cost = upgradeCosts[upgradeLevel];
		if (cost > p.getScore()) {
			MojamComponent.soundPlayer.playSound("/sound/Fail.wav", (float) pos.x, (float) pos.y, true);
			if(this.team == this.localTeam) {
				Notifications.getInstance().add(MojamComponent.texts.upgradeNotEnoughMoney(cost));
			}
			return false;
		}

		MojamComponent.soundPlayer.playSound("/sound/Upgrade.wav", (float) pos.x, (float) pos.y, true);

		++upgradeLevel;
		p.useMoney(cost);
		upgradeComplete();
		
		if(this.team == this.localTeam) {
			Notifications.getInstance().add(MojamComponent.texts.upgradeTo(upgradeLevel+1));
		}
		return true;
	}

	void makeUpgradeableWithCosts(int[] costs) {
		maxUpgradeLevel = 0;
		if (costs == null)
			return;

		upgradeCosts = costs;
		maxUpgradeLevel = costs.length - 1;
		upgradeComplete();
	}

	public void use(Entity user) {
		if (user instanceof Player) {
			((Player) user).pickup(this);
		}
	}
  
	public boolean isHighlightable() {
		return true;
	}

	public void setHighlighted(boolean hl) {
		highlight = hl;
	}

	public boolean isAllowedToCancel() {
		return true;
	}
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
	
    public void checkForHoleTiles(int x, int y) {
        if (level.getTile(x, y) instanceof HoleTile) {
            if (!removed) {
                remove();
                level.addEntity(new EnemyDieAnimation(pos.x, pos.y));
                MojamComponent.soundPlayer.playSound("/sound/Fall.wav", (float) pos.x, (float) pos.y);
            }
        }
    }
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
=======
>>>>>>> parent of cd61150... Cleanups, JavaDoc updates and some minor refactoring
}
