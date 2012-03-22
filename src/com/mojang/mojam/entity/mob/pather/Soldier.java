package com.mojang.mojam.entity.mob.pather;

import java.util.Random;
import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.HostileMob;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.IUsable;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.animation.SmokePuffAnimation;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.loot.LootCollector;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.entity.particle.Sparkle;
import com.mojang.mojam.entity.weapon.IWeapon;
import com.mojang.mojam.entity.weapon.Raygun;
import com.mojang.mojam.entity.weapon.Shotgun;
import com.mojang.mojam.entity.weapon.SoldierRifle;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

/**
 * - Added AI controlled Soldiers that can be bought at the players base.
 * - Players can have as many Soldiers as they have levels, if a soldier dies or the
 * player gains a level the Player can buy another soldier.
 * - Soldiers shoot any enemy Mobs, Players, Spawners or Soldiers.
 * - Soldiers collect any Coins close by, they have a carry limit of 500.
 * - Soldiers do NOT regenerate health (see below).
 * - Soldiers have a small communications device on their helmet.
 * - While moving around soldiers actively try to avoid Mobs by turning away from them.
 * - Soldiers have 3 modes
 *      1. Patrol – they move around the level randomly, killing and collecting, 
 *      when The Soldier is full of Coins or heavily damaged they switch to mode
 *      2. Return Home – in this mode the soldier uses the fastest route to get to the 
 *      Players base. Once at 'Home' they drop off their Coins (its added directly to player score)
 *      and they are instantly healed.
 *      3. Follow – in this mode they roughly follow the player around shooting Mobs
 *      and collecting Coins, they will NOT automatically Return Home,
 *      the player will have to order them to do so.
 *     
 * - When a player is close and looking at them they stop moving and display their status, 
 * Health, Coin capacity, Mod, and Upgrade costs.
 * - The Player can select a Soldiers mode by walking up to them and pressing the Use key (e).
 * - The Player can upgrade a Soldier by walking up to them and pressing the Upgrade key (f).
 * - Soldiers have 3 levels
 *      1. initially they use a downgraded version of the players Rifle.
 *      2. their health, collection radius are upgraded and they are given a Shotgun.
 *      3. their health, collection radius are upgraded to Max and they are given a Raygun.
 *
 * - Soldier Shops are added at the Players base, they look just like Soldiers!
 * - When the Player is unable to buy a Soldier due to level limits the Shop is grayed out.
 * 
 * @author Morgan Gilroy
 */

public class Soldier extends Pather implements IUsable, LootCollector {

	/**
	 * Modes used by the Soldier see class description.
	 * should this be an enum?
	 */
	static final int Mode_Patrol = 0;
	static final int Mode_Follow = 1;
	static final int Mode_ReturnHome = 2;

	/**
	 * String names used for display.
	 * should move to translations
	 */
	private String[] modeNames = new String[] { "Patrol", "Follow",
			"Return Home" };
	/**
	 * The current mode the Soldier is in
	 */
	private int mode;

	/**
	 * When in follow mode it uses this entity as its target
	 */
	private Entity followEntity;

	private double suckPower;
	private double reloadTime;
	private int maxUpgradeLevel;
	private int upgradeLevel;

	private int[] upgradeCosts = new int[] { 20, 50, 100 };
	private int[] upgradeShootRadius = new int[] { 3 * Tile.WIDTH,
			5 * Tile.WIDTH, 7 * Tile.WIDTH };
	private int[] upgradeReloadTime = new int[] { 24, 21, 18 };
	private double[] upgradeSuckPower = new double[] { 0.75, 1, 1.25 };
	private IWeapon[] upgradeWeapon = new IWeapon[] { new SoldierRifle(this),
			new Shotgun(this), new Raygun(this) };
	private double[] upgradeMaxHealth = new double[] { 20, 30, 40 };

	private int shootRadius;
	private int nextWalkSmokeTick = 0;
	
	private Entity target = null;

	/**
	 * @param x 			Stating x position
	 * @param y				Stating y position
	 * @param team			Stating team
	 * @param followEntity	Who bought this soldier/who to follow when in Mone_Follow
	 */
	public Soldier(double x, double y, int team, Entity followEntity) {
		super(x, y, team);
		setPos(x, y);

		setAvoidWallsModifier(0);
		setRandomDistanceModifier(0);
		setMoney(0);
		setMaxMoney(500);
		setDoShowMoneyBar(false);
		makeUpgradeableWithCosts(upgradeCosts);

		setFollowEntity(followEntity);
		setMode(Mode_Patrol);
		setShootRadius(4 * 32);

		//stop Soldier from regenerating health
		this.REGEN_INTERVAL = 0;
		this.REGEN_HEALTH = false;
	}

	
	/* (non-Javadoc)
	 * @see com.mojang.mojam.entity.mob.pather.Pather#tick()
	 */
	public void tick() {
		super.tick();
		aquireTarget();
		tryToShoot();

		// Show a small smoke puff when they walk around
		Random random = TurnSynchronizer.synchedRandom;
		if (stepTime >= nextWalkSmokeTick) {
			level.addEntity(new SmokePuffAnimation(pos.x, pos.y, 0, -1,
					Art.fxDust12, 35 + random.nextInt(10)));
			nextWalkSmokeTick += (15 + random.nextInt(15));
		}
		if (random.nextDouble() < 0.02f) {
			level.addEntity(new SmokePuffAnimation(pos.x, pos.y, 0, -0.5,
					Art.fxDust12, 35 + random.nextInt(10)));
		}
	}

	/**
	 * On collision with Friendly Player Bump a small amount.
	 * On all collisions randomly choose to reset the path ~ 50/50.
	 * 
	 * @see com.mojang.mojam.entity.mob.pather.Pather#collide(com.mojang.mojam.entity.Entity, double, double)
	 */
	public void collide(Entity entity, double xa, double ya) {
		super.collide(entity, xa, ya);

		if (entity instanceof Player) {
			if (!((Player) entity).isNotFriendOf(this)) {
				double dist = entity.pos.dist(pos);
				xBump = (pos.x - entity.pos.x) / dist * 2;
				yBump = (pos.y - entity.pos.y) / dist * 2;
			}
		}
		if (TurnSynchronizer.synchedRandom.nextInt(10) > 5)
			resetPath();
	}

	/**
	 * this is a basic state machine that returns a single Vec2 which is the position it is aiming for.
	 * the target is chosen based on its current Mode and position, it may also switch between modes.
	 * @return Vec2 the world position the Soldier should move towards using a*
	 */
	protected Vec2 getPathTarget() {

		Tile tileTo = null;
		switch (mode) {
		case Mode_Follow:
			if (followEntity != null)
				tileTo = level.getTile(followEntity.pos);
			break;
		case Mode_ReturnHome:
			if ((pos.y < (8 * Tile.HEIGHT) && getTeam() == Team.Team2)
					|| (pos.y > ((level.height - 9) * Tile.HEIGHT) && getTeam() == Team.Team1)) {
				setMode(Mode_Patrol);
				health = maxHealth;
				if (followEntity instanceof Player)
					if (((Player) followEntity).canTake())
						((Player) followEntity).addScore(getMoney());
				setMoney(0);
			}

			if (getTeam() == Team.Team1) {
				tileTo = level.getTile(level.width / 2, (level.height - 5 - 1));
			} else {
				tileTo = level.getTile(level.width / 2, 6);
			}
			break;
		case Mode_Patrol:
		default:
			if (health < maxHealth / 2 || getMoney() >= getMaxMoney()) {
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
	 * decide on a target to shoot at
	 */
	private void aquireTarget() {

		Vec2 posDiff = null;

		if (target != null) {
			posDiff = (target.pos.sub(pos));

			if (posDiff.length() > getShootRadius() || target.removed) {
				target = null;
			}
		}

		Entity closest = checkIfEnemyNear(getShootRadius(), Mob.class, true);

		if (closest != null) {
			
			if (target == null) {
				target = closest;
				return;
			}
			
			if (target instanceof Building && closest instanceof HostileMob) {
				target = closest;
				return;
			}
			
			posDiff = (closest.pos.sub(pos));
			if (posDiff.length() < ( radius.length() + closest.radius.length() + 16 ) ) {
				target=closest;
				return;
			}
		}
	}
	
	/**
	 * Find the closest enemy and fire its gun. also change facing so it looks
	 * at it.
	 */
	private void tryToShoot() {

		Vec2 posDiff;

		if (target != null && (!(target.removed)) ) {
			posDiff = (target.pos.sub(pos));

			if (posDiff.length() < getShootRadius()) {
				aimVector = posDiff.normal();
				weapon.primaryFire(posDiff.x, posDiff.y);
				facing = (int) ((Math.atan2(-aimVector.x, aimVector.y) * 8
						/ (Mth.PI2) - 8.5)) & 7;
			}
		}

	}

	/**
	 * Get the sprite from followEntity so looks the same as the Player
	 */
	public Bitmap getSprite() {
		Bitmap[][] sheet = null;
		if (followEntity instanceof Player) {
			sheet = Art.getPlayer(((Player) followEntity).getCharacter());
		} else {
			sheet = Art.getPlayer(GameCharacter.LordLard);
		}

		if (sheet != null) {
			return sheet[(int) stepTime % 6][facing];
		}
		return null;
	}

	/**
	 * Default render + add a little dish on top of its head
	 * bounces up and down as it moves.
	 * 
	 * @param Screen screen the screen to render to
	 */
	public void render(Screen screen) {
		super.render(screen);
		int yy=0;
		switch( (int)(stepTime % 6) ) {
			case 0:
			case 3:
				yy=1;
		}
		screen.blit(Art.dish[ (int)( (System.currentTimeMillis()*0.02) % 6 ) ][0], pos.x-3, (pos.y - 32 - yy ));
	}

	/**
	 * Draw the mode and upgrade costs above the Soldier
	 * 
	 * @param Screen screen the screen to render to
	 */
	protected void renderMarker(Screen screen) {
		super.renderMarker(screen);

		if (this.highlight) {
			Font.FONT_WHITE_SMALL.draw(screen, getCurrentModeName(),
					(int) (pos.x), (int) (pos.y - yOffs - 14),
					Font.Align.CENTERED);

			if (upgradeLevel < maxUpgradeLevel) {
				Font.FONT_WHITE_SMALL.draw(screen, String.format("Upgrade: %d",
						upgradeCosts[upgradeLevel]), (int) (pos.x),
						(int) (pos.y - yOffs - 24), Font.Align.CENTERED);
			}
		}
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
		return suckPower;
	}

	@Override
	public void notifySucking() {
	}

	@Override
	public int getScore() {
		return getMoney();
	}

	@Override
	public void flash() {
		setFlashTime(5);
	}

	/**
	 * When used by the player rotate mode and reset followEntity
	 */
	@Override
	public void use(Entity user) {
		rotateMode();
		setFollowEntity(user);
	}

	/**
	 * when highlightet set freezTime to stop it moving
	 */
	@Override
	public void setHighlighted(boolean hl) {
		if (this.highlight && !hl) {
			setDoShowMoneyBar(false);
			resetPath();
		}
		this.highlight = hl;
		this.freezeTime = 10;

		if (team == MojamComponent.localTeam && hl)
			setDoShowMoneyBar(true);
	}

	@Override
	public boolean isHighlightable() {
		return true;
	}

	@Override
	public boolean isAllowedToCancel() {
		return false;
	}

	/**
	 * Attempt to upgrade the Soldier
	 * 
	 * @param Player player The player trying to iupgrade the Soldier
	 */
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
		setReloadTime(getUpgradeReloadTime(upgradeLevel));
		setShootRadius(getUpgradeShootRadius(upgradeLevel));
		setSuckPower(getUpgradeSuckPower(upgradeLevel));
		setStartHealth((float) getUpgradeMaxHealth(upgradeLevel));
		setWeapon(getUpgradeWeapon(upgradeLevel));
	}

	private double getUpgradeMaxHealth(int upgradeLevel) {
		return upgradeMaxHealth[upgradeLevel];
	}

	private IWeapon getUpgradeWeapon(int upgradeLevel) {
		return upgradeWeapon[upgradeLevel];
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

	/**
	 * sets the mode to the requested mode and also sets PathNodeSkipRadius, AvoidWallsModifier and RandomDistanceModifier
	 * so it behaves differently depending on mode ie on Patrol move more randomly around but when returning home go via
	 * a more direct route.
	 * 
	 * @param int mode One of the Modes to use
	 */
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

	/**
	 * Rotates the Mode through all that are available
	 * 
	 * @return int mode the mode it has rotated to
	 */
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
		return 0;
	}

	@Override
	public int getMiniMapColor() {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return null;
	}

	public int getShootRadius() {
		return shootRadius;
	}

	public void setShootRadius(int shootRadius) {
		this.shootRadius = shootRadius;
	}

	public double getReloadTime() {
		return reloadTime;
	}

	public void setReloadTime(double reloadTime) {
		this.reloadTime = reloadTime;
	}


	public Entity getTarget() {
		return target;
	}


	public void setTarget(Entity target) {
		this.target = target;
	}
}
