package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.IUsable;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Generic building class
 */
public class Building extends Mob implements IUsable {
	public static final int SPAWN_INTERVAL = 60;
	public static final int MIN_BUILDING_DISTANCE = 1700; // Sqr
	public static final int HEALING_INTERVAL = 15;

	public int spawnTime = 0;
	public boolean highlight = false;
	private int healingTime = HEALING_INTERVAL;
	public Mob carriedBy = null;
	public Mob lastCarrying = null;

	protected int upgradeLevel = 0;
	private int maxUpgradeLevel = 0;
	private int[] upgradeCosts = null;
	protected boolean healthRegenB = false;

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
		healthRegen = false;
		healthRegenB = true;
		freezeTime = 10;
		spawnTime = TurnSynchronizer.synchedRandom.nextInt(SPAWN_INTERVAL);
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
		renderMarker(screen);
		if(team == MojamComponent.localTeam)
			renderInfo(screen);
	}

	/**
	 * Render the marker onto the given screen
	 * 
	 * @param screen
	 *            Screen
	 */
	protected void renderMarker(Screen screen) {
		if (highlight && !isCarried()) {
			BB bb = getBB();
			bb = bb.grow((getSprite().w - (bb.x1 - bb.x0))
					/ (3 + Math.sin(System.currentTimeMillis() * .01)));
			int width = (int) (bb.x1 - bb.x0);
			int height = (int) (bb.y1 - bb.y0);
			Bitmap marker = new Bitmap(width, height);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if ((x < 2 || x > width - 3 || y < 2 || y > height - 3)
							&& (x < 5 || x > width - 6)
							&& (y < 5 || y > height - 6)) {
						int i = x + y * width;
						marker.pixels[i] = 0xffffffff;
					}
				}
			}
			screen.blit(marker, bb.x0, bb.y0 - 4);
		}
	}

	/**
	 * Render the shop info onto the given screen
	 * 
	 * @param screen
	 *            Screen
	 */
	protected void renderInfo(Screen screen) {
		// Draw iiAtlas' shop item info graphics
		if (highlight) {
		    if (this instanceof ShopItem) {
		        ShopItem s = (ShopItem)this;
		        Bitmap image = getSprite();
		        int teamYOffset = (team == 2) ? 90 : 0;
		        screen.blit(Art.tooltipBackground,
                        (int)(pos.x - image.w / 2 - 10),
                        (int)(pos.y + 20 - teamYOffset), 110, 25);
		        
		        String[] tooltip = s.getTooltip();
		        Font f = Font.FONT_GOLD;
		        for (int i=0; i<tooltip.length; i++) {
		            f.draw(screen, tooltip[i], (int)(pos.x - image.w + 8), (int)pos.y + 22 - teamYOffset + (i==0?0:1) + i*(f.getFontHeight()+1));
		            f = Font.FONT_WHITE_SMALL;
		        }
		    }
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (freezeTime > 0) {
			return;
		}
		if (hurtTime <= 0 && healthRegenB) {
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
	
	/**
	 * Called if this building is picked up
	 * 
	 * @param mob Reference to the mob object carrying this building
	 */
	public void onPickup(Mob mob) {
	    lastCarrying = carriedBy = mob;
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
	public void hurt(Bullet bullet) {
		super.hurt(bullet);
		healingTime = HEALING_INTERVAL;
	}

	@Override
	public void hurt(Entity source, float damage) {
		super.hurt(source, damage);
		healingTime = HEALING_INTERVAL;
	}

	@Override
	public Bitmap getSprite() {
		return Art.floorTiles[3][2];
	}

	@Override
	public boolean move(double xBump, double yBump) {
		return false;
	}

	@Override
	public void slideMove(double xa, double ya) {
		super.move(xa, ya);
		fallDownHole();
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
		highlight = hl;
	}

	@Override
	public boolean isAllowedToCancel() {
		return true;
	}
	
	/**
	 * Set regeneration status for this building
	 * 
	 * @param regen True if building can regenerate, false if not
	 */
	public void buildingRegen(boolean regen) {
		healthRegenB = regen;
	}
	
	/**
	 * Check if this building is able to regenerate
	 * 
	 * @return True if building can regenerate, false if not
	 */
	public boolean buildingRegenEnabled() {
		return healthRegenB;
	}
}
