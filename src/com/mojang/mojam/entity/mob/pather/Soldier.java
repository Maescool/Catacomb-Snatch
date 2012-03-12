package com.mojang.mojam.entity.mob.pather;

import java.util.Set;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.IUsable;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.loot.LootCollector;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.entity.particle.Sparkle;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.gui.components.Font;

import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

/**
 * @author Morgan
 * 
 */
public class Soldier extends Pather implements IUsable, LootCollector {

	static final int Mode_Patrol = 0;
	static final int Mode_Follow = 1;
	static final int Mode_ReturnHome = 2;

	private String[] modeNames = new String[] { "Patrol", "Follow",
			"Return Home" };

	private int mode;

	private Entity followEntity;

	private boolean highlight;
	private double suckPower;
	private int maxUpgradeLevel;
	private int upgradeLevel;

	private int[] upgradeCosts = new int[] { 20, 50, 100 };
	private int[] upgradeShootRadius = new int[] { 3 * Tile.WIDTH,
			5 * Tile.WIDTH, 7 * Tile.WIDTH };
	private int[] upgradeReloadTime = new int[] { 24, 21, 18 };
	private double[] upgradeSuckPower = new double[] { 0.75, 1, 1.25 };

	private int shootRadius;


	
	/**
	 * @param x
	 * @param y
	 */

	public Soldier(double x, double y, int team, Entity followEntity) {
		super(x, y, team);
		setPos(x, y);
		setStartHealth(70);
		setAvoidWallsModifier(0);
		setRandomDistanceModifier(0);
		setMoney(0);
		setMaxMoney(500);
		setDoShowMoneyBar(false);
		makeUpgradeableWithCosts(upgradeCosts);

		//damageEffectType = Mob.DamageEffectBlood;
		setFollowEntity(followEntity);
		setMode(Mode_Patrol);
		setWeapon(new Rifle(this));
		setShootRadius(4*32);
	}

	public void tick() {
		super.tick();
		tryToShoot();
	}

	protected Vec2 getPathTarget() {
		Tile tileTo = null;

		switch (mode) {

		
		case Mode_Follow:
			if (followEntity != null)
				tileTo = level.getTile(followEntity.pos);
			break;
		case Mode_ReturnHome:
					
			if ((pos.y < (8*Tile.HEIGHT) && getTeam() == Team.Team2)
					|| (pos.y > ((level.height - 9) *Tile.HEIGHT)  && getTeam() == Team.Team1)) {
				setMode(Mode_Patrol);
				health=maxHealth;
				if (followEntity instanceof Player)
					if (((Player) followEntity).canTake())
						((Player) followEntity).addScore(getMoney());
						setMoney(0);
			}
					
			if (getTeam() == Team.Team1) {
				tileTo=level.getTile(level.width/2,(level.height - 5 - 1));
			} else {
				tileTo=level.getTile(level.width/2,6);
			}
			break;
		case Mode_Patrol:
		default:
			if ( health < maxHealth/2 || getMoney() >= getMaxMoney()) {
				setMode(Mode_ReturnHome);
				return null;
			}
			int tileX;
			int tileY;

			do {
				tileX = TurnSynchronizer.synchedRandom.nextInt(level.width);
				tileY = TurnSynchronizer.synchedRandom.nextInt(level.height);

				tileTo = level.getTile(tileX, tileY);
				if (!tileTo.canPass(this))
					tileTo = null;
			} while (tileTo == null);
			break;
		}

		if (tileTo != null)
			return new Vec2(tileTo.x, tileTo.y);
		else
			return null;
	}

	/**
	 * 
	 */
	private void tryToShoot() {

		int shootRadius = getShootRadius();
		
		Entity closest = checkIfEnemyNear( shootRadius, Mob.class, true);

		if (closest != null) {
			double yDir = closest.pos.y - pos.y;
			double xDir = closest.pos.x - pos.x;
			aimVector=(closest.pos.sub(pos));
			weapon.primaryFire(xDir, yDir);
		}
	}

	public Bitmap getSprite() {
		Bitmap[][] sheet = null;
		if (followEntity instanceof Player) {
			sheet =Art.getPlayer(((Player)followEntity).getCharacter());
		} else {
			sheet =Art.getPlayer(GameCharacter.LordLard);
		}
		
		if (sheet != null)
			return sheet[(int) stepTime % 6][facing];
		return null;
	}

	public void render(Screen screen) {
		super.render(screen);
		renderMarker(screen);
	}

	@Override
	public boolean canTake() {
		if (getMoney() < getMaxMoney())
			return true;
		return false;
	}

	@Override
	public void take(Loot loot) {
		if (!canTake())
			return;

		loot.remove();

		level.addEntity(new Sparkle(pos.x, pos.y, -1, 0));
		level.addEntity(new Sparkle(pos.x, pos.y, +1, 0));
		addMoney(loot.getScoreValue());
	}

	@Override
	public double getSuckPower() {
		// TODO Auto-generated method stub
		return suckPower;
	}

	@Override
	public void notifySucking() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getScore() {
		return getMoney();
	}

	@Override
	public void flash() {
		setFlashTime(5);
	}

	@Override
	public void use(Entity user) {
		rotateMode();

		setFollowEntity(user);
	}

	@Override
	public void setHighlighted(boolean hl) {
		if (this.highlight && !hl) {
			setDoShowMoneyBar(false);
			resetPath();
		}
		this.highlight = hl;
		this.freezeTime = 10;
		
		if(team == MojamComponent.localTeam && hl)
			setDoShowMoneyBar(true);
	}

	@Override
	public boolean isHighlightable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAllowedToCancel() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void renderMarker(Screen screen) {
		if (highlight) {
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
			//renderFillBar(screen, 32, 32);
			//if (upgradeLevel < maxUpgradeLevel) {
			//	Font.drawCenter(screen, String.format("Upgrade: %d",
			//			upgradeCosts[upgradeLevel]), (int) (pos.x),
			//			(int) (pos.y - 24 - yOffs));
			//}
			Font.FONT_WHITE_SMALL.draw(screen, getCurrentModeName(), (int) (pos.x), (int) (pos.y - yOffs - 16), Font.Align.CENTERED);
			//drawCenter(screen, getCurrentModeName(), (int) (pos.x),
			//		(int) (pos.y - 24 - yOffs - 8));
		}
	}

	public boolean upgrade(Player p) {
		if (upgradeLevel >= maxUpgradeLevel)
			return false;

		final int cost = upgradeCosts[upgradeLevel];
		if (cost > p.getScore()) {
			MojamComponent.soundPlayer.playSound("/sound/Enemy Death 2.wav",
					(float) pos.x, (float) pos.y);
			return false;
		}

		++upgradeLevel;
		p.payCost(cost);
		upgradeComplete(upgradeLevel);
		return true;
	}

	void makeUpgradeableWithCosts(int[] costs) {
		maxUpgradeLevel = 0;
		if (costs == null)
			return;

		upgradeCosts = costs;
		maxUpgradeLevel = costs.length - 1;
		upgradeComplete(0);
	}

	private void upgradeComplete(int i) {
		//setReloadTime(getUpgradeReloadTime(upgradeLevel));
		//setShootRadius(getUpgradeShootRadius(upgradeLevel));
		//setSuckPower(getUpgradeSuckPower(upgradeLevel));
	}

	private int getUpgradeShootRadius(int upgradeLevel) {
		return upgradeShootRadius[upgradeLevel];
	}

	private int getUpgradeReloadTime(int upgradeLevel) {
		return upgradeReloadTime[upgradeLevel];
	}

	private double getUpgradeSuckPower(int upgradeLevel) {
		return upgradeSuckPower[upgradeLevel];
	}

	public void setSuckPower(double suckPower) {
		this.suckPower = suckPower;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		switch (mode) {

		case Mode_Patrol:
			setAvoidWallsModifier(1.5);
			setRandomDistanceModifier(1);
			setPathNodeSkipRadius(5 * Tile.WIDTH);
			break;
		case Mode_Follow:
			setAvoidWallsModifier(0);
			setRandomDistanceModifier(10);
			setPathNodeSkipRadius(2 * Tile.WIDTH);
			break;
		case Mode_ReturnHome:
			setAvoidWallsModifier(0);
			setRandomDistanceModifier(0);
			setPathNodeSkipRadius(5 * Tile.WIDTH);
			break;

		default:
			setAvoidWallsModifier(0);
			setRandomDistanceModifier(0);
			setPathNodeSkipRadius(0);
			break;
		}

		this.mode = mode;
	}

	public int rotateMode() {
		int tMode = mode;
		tMode++;
		if (tMode >= modeNames.length) {
			tMode = 0;
		}
		setMode(tMode);

		return mode;
	}

	public String getCurrentModeName() {
		return modeNames[mode];
	}

	public Entity getFollowEntity() {
		return followEntity;
	}

	public void setFollowEntity(Entity followEntity) {
		this.followEntity = followEntity;
	}

	@Override
	public int getColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMiniMapColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bitmap getBitMapForEditor() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getShootRadius() {
		return shootRadius;
	}

	public void setShootRadius(int shootRadius) {
		this.shootRadius = shootRadius;

	}
}
