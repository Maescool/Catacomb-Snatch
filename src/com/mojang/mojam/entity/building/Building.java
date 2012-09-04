package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.IUsable;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

/**
 * Generic building class
 */
public abstract class Building extends Mob implements IUsable {
	public static final int SPAWN_INTERVAL = 60;
	public static final int MIN_BUILDING_DISTANCE = 1700; // Sqr

	public int REGEN_INTERVAL = 15;
	public float REGEN_AMOUNT = 1;
	public boolean REGEN_HEALTH = true;
	public int healingTime = REGEN_INTERVAL;

	public int spawnTime = 0;

	public Mob carriedBy = null;

	protected int upgradeLevel = 0;
	private int maxUpgradeLevel = 0;
	private int[] upgradeCosts = null;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            Initial X coordinate
	 * @param y
	 *            Initial Y coordinate
	 * @param team
	 *            Team number
	 */
	public Building(double x, double y, int team) {
		super(x, y, team);

		setStartHealth(20);
		freezeTime = 10;
		spawnTime = TurnSynchronizer.synchedRandom.nextInt(SPAWN_INTERVAL);
	}

	@Override
	public void render(AbstractScreen screen) {
		super.render(screen);
	}

	/**
	 * Render the marker onto the given screen
	 * 
	 * @param screen
	 *            AbstractScreen
	 */
	protected void renderMarker(AbstractScreen screen) {
		if (isCarried())
			return;
		super.renderMarker(screen);
	}

	@Override
	public void tick() {
		super.tick();
		if (carriedBy == null) {
			fallDownHole();
		}
		xd = 0.0;
		yd = 0.0;
	}
	
	public void doRegenTime() {
		if (freezeTime <= 0) {
			super.doRegenTime();
		} else {
			// DO NOTHING
		}
	}
	
	/**
	 * Called if this building is picked up
	 * 
	 * @param mob Reference to the mob object carrying this building
	 */
	public void onPickup(Mob mob) {
	    carriedBy = mob;
	}
	
	/**
	 * Called if this building is dropped by its carrier
	 */
	public void onDrop() {
	    carriedBy = null;
	}
	
	/**
	 * Check if this building is being carried
	 * 
	 * @return True if carried, false if not
	 */
	public boolean isCarried() {
	    return carriedBy != null;
	}


	@Override
	public AbstractBitmap getSprite() {
		return Art.floorTiles[3][2];
	}

	@Override
	public boolean move(double xBump, double yBump) {
		return false;
	}

	@Override
	public void slideMove(double xa, double ya) {
		super.move(xa, ya);
	}
	
	/**
	 * Called if building upgrade is complete
	 */
	protected void upgradeComplete() {
	}

	@Override
	public boolean upgrade(Player p) {
		if (upgradeLevel >= maxUpgradeLevel) {
			MojamComponent.soundPlayer.playSound("/sound/Fail.wav",
					(float) pos.x, (float) pos.y, true);
			if (this.team == MojamComponent.localTeam) {
				Notifications.getInstance().add(
						MojamComponent.texts.getStatic("upgrade.full"));
			}
			return false;
		}

		final int cost = upgradeCosts[upgradeLevel];
		if (cost > p.getScore() && !Options.getAsBoolean(Options.CREATIVE)) {
			MojamComponent.soundPlayer.playSound("/sound/Fail.wav",
					(float) pos.x, (float) pos.y, true);
			if (this.team == MojamComponent.localTeam) {
				Notifications.getInstance().add(
						MojamComponent.texts.upgradeNotEnoughMoney(cost));
			}
			return false;
		}

		MojamComponent.soundPlayer.playSound("/sound/Upgrade.wav",
				(float) pos.x, (float) pos.y, true);

		++upgradeLevel;
		p.useMoney(cost);
		upgradeComplete();

		if (this.team == MojamComponent.localTeam) {
			Notifications.getInstance().add(
					MojamComponent.texts.upgradeTo(upgradeLevel + 1));
		}
		return true;
	}

	/**
	 * Make this building upgradeable
	 * 
	 * @param costs Cost vector
	 */
	void makeUpgradeableWithCosts(int[] costs) {
		if (costs == null) {
			maxUpgradeLevel = 0;
		} else {
			upgradeCosts = costs;
			maxUpgradeLevel = costs.length - 1;
			upgradeComplete();
		}
		return;
	}

	@Override
	public void use(Entity user) {
		if (user instanceof Player) {
			((Player) user).pickup(this);
		}
	}

	@Override
	public boolean isHighlightable() {
		return true;
	}

	@Override
	public void setHighlighted(boolean hl) {
		setHighlight(hl);
		justDroppedTicks = 80;
	}

	@Override
	public boolean isAllowedToCancel() {
		return true;
	}
	
}
