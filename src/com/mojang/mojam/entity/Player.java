package com.mojang.mojam.entity;

import java.util.Random;

import com.mojang.mojam.Keys;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.animation.EnemyDieAnimation;
import com.mojang.mojam.entity.animation.SmokePuffAnimation;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.building.Harvester;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.loot.LootCollector;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.RailDroid;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.entity.particle.Sparkle;
import com.mojang.mojam.entity.weapon.IWeapon;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.gui.OptionsMenu;
import com.mojang.mojam.level.HoleTile;
import com.mojang.mojam.level.tile.RailTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Player extends Mob implements LootCollector {

	public static int COST_RAIL;
	public static int COST_DROID;
	public static int COST_REMOVE_RAIL;
	public static final int REGEN_INTERVAL = 60 * 3;
	public int plevel;
	public int pnextlevel;
	public double pexp;
	public double psprint;
	public boolean isSprint = false;
	public int timeSprint = 0;
	public int maxTimeSprint;
	public Keys keys;
	public MouseButtons mouseButtons;
	public int mouseFireButton = 1;
	public int mouseUseButton = 3;
	public Vec2 aimVector;
	public IWeapon weapon;
	private boolean mouseAiming;
	public double xd, yd;
	public int takeDelay = 0;
	public int flashTime = 0;
	public int suckRadius = 0;
	public boolean wasShooting;
	public int score = 0;
	private int facing = 0;
	private int time = 0;
	private int walkTime = 0;
	private Entity selected = null;
	static final int RailDelayTicks = 15;
	private int lastRailTick = -999;
	private final static int INTERACT_DISTANCE = 20 * 20; // Sqr
	private int steps = 0;
	private boolean isSeeing;
	private int startX;
	private int startY;
	public int muzzleTicks = 0;
	public double muzzleX = 0;
	public double muzzleY = 0;
	private int muzzleImage = 0;
	private boolean dead = false;
	private int deadDelay = 0;
	private int nextWalkSmokeTick = 0;
	private int regenDelay = 0;
	boolean isImmortal;
	//* this is crap
	public static boolean creative = Options.getAsBoolean(Options.CREATIVE,
			Options.VALUE_FALSE);
	//*
	private boolean alternative = Options.getAsBoolean(Options.ALTERNATIVE,
			Options.VALUE_FALSE);

	public void setRailPricesandImmortality() {
		if (creative == true) {
			COST_RAIL = 0;
			COST_DROID = 0;
			COST_REMOVE_RAIL = 0;
			isImmortal = true;
		} else {
			COST_RAIL = 10;
			COST_DROID = 50;
			COST_REMOVE_RAIL = 15;
			isImmortal = false;
		}
	}

	public Player(Keys keys, MouseButtons mouseButtons, int x, int y, int team,
			int localTeam) {
		super(x, y, team, localTeam);
		this.keys = keys;
		this.mouseButtons = mouseButtons;

		startX = x;
		startY = y;

		plevel = 1;
		pexp = 0;
		maxHealth = 5;
		health = 5;
		psprint = 1.5;
		maxTimeSprint = 100;

		aimVector = new Vec2(0, 1);

		score = 0;
		weapon = new Rifle(this);
		setRailPricesandImmortality();
	}

	private void checkForLevelUp() {
		if (pexp >= nextLevel()) {
			levelUp();
		}
	}

	private double nextLevel() {
		double next = (plevel * 7) * (plevel * 7);
		pnextlevel = (int) next;
		return next;
	}

	public double getNextLevel() {
		double next = nextLevel() - pexp;
		return next;
	}

	private void levelUp() {
		this.maxHealth++;
		this.regenDelay = 2;
		plevel++;
		psprint += 0.1;
		maxTimeSprint += 20;

		MojamComponent.soundPlayer.playSound("/sound/levelUp.wav",
				(float) pos.x, (float) pos.y, true);
	}

	@Override
	public void tick() {

		// if mouse is in use, update player orientation before level tick
		if (!mouseButtons.mouseHidden) {

			// update player mouse, in world pixels relative to
			// player
			setAimByMouse(
					((mouseButtons.getX() / MojamComponent.SCALE) - (MojamComponent.screen.w / 2)),
					(((mouseButtons.getY() / MojamComponent.SCALE) + 24) - (MojamComponent.screen.h / 2)));
		} else {
			setAimByKeyboard();
		}

		time++;

		checkForLevelUp();
		flashMiniMapIcon();
		regeneratePlayer();
		countdownTimers();
		playStepSound();

		double xa = 0;
		double ya = 0;

		if (!dead) {
			if (keys.up.isDown) {
				ya--;
			}
			if (keys.down.isDown) {
				ya++;
			}
			if (keys.left.isDown) {
				xa--;
			}
			if (keys.right.isDown) {
				xa++;
			}
		}

		if (!mouseAiming && !keys.fire.isDown
				&& !mouseButtons.isDown(mouseFireButton)
				&& xa * xa + ya * ya != 0) {
			aimVector.set(xa, ya);
			aimVector.normalizeSelf();
			updateFacing();
		}

		if (xa != 0 || ya != 0) {
			handleMovement(xa, ya);
		}

		if (freezeTime > 0) {
			move(xBump, yBump);
		} else {
			move(xd + xBump, yd + yBump);

		}
		xd *= 0.4;
		yd *= 0.4;
		xBump *= 0.8;
		yBump *= 0.8;
		muzzleImage = (muzzleImage + 1) & 3;

		handleWeaponFire(xa, ya);

		int x = (int) pos.x / Tile.WIDTH;
		int y = (int) pos.y / Tile.HEIGHT;

		checkForHoleTiles(x, y);

		if (dead && deadDelay <= 0) {
			dead = false;
			revive();
		}

		if (keys.build.isDown && !keys.build.wasDown) {
			handleRailBuilding(x, y);
		}

		if (carrying != null) {
			handleCarrying();
		} else {
			handleEntityInteraction();
		}

		if (isSeeing) {
			level.reveal(x, y, 5);
		}
	}

	private void flashMiniMapIcon() {

		minimapIcon = time / 3 % 4;
		if (minimapIcon == 3) {
			minimapIcon = 1;
		}
	}

	private void regeneratePlayer() {

		if (regenDelay > 0) {
			regenDelay--;
			if (regenDelay == 0) {
				if (health + 1 < maxHealth) {
					health++;
				} else if (health != maxHealth) {
					health = maxHealth;
				}
				regenDelay = REGEN_INTERVAL;
			}
		}
	}

	private void countdownTimers() {

		if (flashTime > 0) {
			flashTime = 0;
		}
		if (hurtTime > 0) {
			hurtTime--;
		}
		if (freezeTime > 0) {
			freezeTime--;
		}
		if (muzzleTicks > 0) {
			muzzleTicks--;
		}
		if (deadDelay > 0) {
			deadDelay--;
		}
	}

	private void playStepSound() {

		if (keys.up.isDown || keys.down.isDown || keys.left.isDown
				|| keys.right.isDown) {
			int stepCount = 25;
			if (carrying == null) {
				stepCount = 15;
			}
			if (isSprint) {
				stepCount *= 0.6;
			}
			if (steps % stepCount == 0) {
				MojamComponent.soundPlayer.playSound("/sound/Step "
						+ (TurnSynchronizer.synchedRandom.nextInt(2) + 1)
						+ ".wav", (float) pos.x, (float) pos.y, true);
			}
			steps++;
		}
	}

	private void handleMovement(double xa, double ya) {

		int facing2 = (int) ((Math.atan2(-xa, ya) * 8 / (Math.PI * 2) + 8.5)) & 7;
		int diff = facing - facing2;
		if (diff >= 4) {
			diff -= 8;
		}
		if (diff < -4) {
			diff += 8;
		}

		if (carrying != null) {
			if (diff > 2 || diff < -4) {
				walkTime--;
			} else {
				walkTime++;
			}
		}
		if (diff > 2 || diff < -4) {
			walkTime--;
		} else {
			walkTime++;
		}

		Random random = TurnSynchronizer.synchedRandom;
		if (walkTime >= nextWalkSmokeTick) {
			level.addEntity(new SmokePuffAnimation(this, Art.fxDust12,
					35 + random.nextInt(10)));
			nextWalkSmokeTick += (15 + random.nextInt(15));
		}
		if (random.nextDouble() < 0.02f) {
			level.addEntity(new SmokePuffAnimation(this, Art.fxDust12,
					35 + random.nextInt(10)));
		}

		double dd = Math.sqrt(xa * xa + ya * ya);
		double speed = getSpeed() / dd;

		if (this.keys.sprint.isDown) {
			if (timeSprint < maxTimeSprint) {
				isSprint = true;
				if (carrying == null) {
					speed = getSpeed() / dd * psprint;
				} else {
					speed = getSpeed() / dd * (psprint - 0.5);
				}
				timeSprint++;
			} else {
				isSprint = false;
			}
		} else {
			if (timeSprint >= 0) {
				timeSprint--;
			}
			isSprint = false;
		}

		xa *= speed;
		ya *= speed;

		xd += xa;
		yd += ya;
	}

	private void handleWeaponFire(double xa, double ya) {

		weapon.weapontick();
		if (!dead
				&& (carrying == null && keys.fire.isDown || carrying == null
						&& mouseButtons.isDown(mouseFireButton))) {
			primaryFire(xa, ya);
		} else {
			if (wasShooting) {
				suckRadius = 0;
			}
			wasShooting = false;
			if (suckRadius < 60) {
				suckRadius++;
			}
			takeDelay = 15;
		}
	}

	private void primaryFire(double xa, double ya) {
		wasShooting = true;
		if (takeDelay > 0) {
			takeDelay--;
		}
		weapon.primaryFire(xa, ya);
	}

	private void checkForHoleTiles(int x, int y) {

		if (level.getTile(x, y) instanceof HoleTile) {
			if (!dead) {
				dead = true;
				carrying = null;
				level.addEntity(new EnemyDieAnimation(pos.x, pos.y));
				MojamComponent.soundPlayer.playSound("/sound/Fall.wav",
						(float) pos.x, (float) pos.y);
				deadDelay = 50;
			}
		}
	}

	private void handleRailBuilding(int x, int y) {

		if (level.getTile(x, y).isBuildable()) {
			if (score >= COST_RAIL && time - lastRailTick >= RailDelayTicks) {
				lastRailTick = time;
				level.placeTile(x, y, new RailTile(level.getTile(x, y)), this);
				payCost(COST_RAIL);
			} else if (score < COST_RAIL) {
				if (this.team == this.localTeam) {
					Notifications.getInstance().add(
							MojamComponent.texts.buildRail(COST_RAIL));
				}

			}
		} else if (level.getTile(x, y) instanceof RailTile) {
			if ((y < 8 && team == Team.Team2)
					|| (y > level.height - 9 && team == Team.Team1)) {
				if (score >= COST_DROID) {
					level.addEntity(new RailDroid(pos.x, pos.y, team, localTeam));
					payCost(COST_DROID);
				} else {
					if (this.team == this.localTeam) {
						Notifications.getInstance().add(
								MojamComponent.texts.buildDroid(COST_DROID));
					}
				}
			} else {

				if (score >= COST_REMOVE_RAIL
						&& time - lastRailTick >= RailDelayTicks) {
					lastRailTick = time;
					if (((RailTile) level.getTile(x, y)).remove()) {
						payCost(COST_REMOVE_RAIL);
					}
				} else if (score < COST_REMOVE_RAIL) {
					if (this.team == this.localTeam) {
						Notifications.getInstance().add(
								MojamComponent.texts
										.removeRail(COST_REMOVE_RAIL));
					}
				}
				MojamComponent.soundPlayer.playSound("/sound/Track Place.wav",
						(float) pos.x, (float) pos.y);
			}
		}
	}

	private void handleCarrying() {

		carrying.setPos(pos.x, pos.y - 20);
		if (!(carrying instanceof Turret)) {
			carrying.tick();
		}
		if (keys.use.wasPressed() || mouseButtons.isDown(mouseUseButton)) {
			boolean allowed = true;
			mouseButtons.setNextState(mouseUseButton, false);

			if (allowed
					&& (!(carrying instanceof IUsable) || (carrying instanceof IUsable && ((IUsable) carrying)
							.isAllowedToCancel()))) {
				dropCarrying();
			}
		}
	}

	private void dropCarrying() {

		carrying.removed = false;
		carrying.xSlide = aimVector.x * 5;
		carrying.ySlide = aimVector.y * 5;
		carrying.freezeTime = 10;
		carrying.justDroppedTicks = 80;
		carrying.setPos(pos);
		level.addEntity(carrying);
		carrying = null;
	}

	private void handleEntityInteraction() {

		Entity closest = null;
		double closestDist = Double.MAX_VALUE;
		for (Entity e : level.getEntitiesSlower(pos.x - INTERACT_DISTANCE,
				pos.y - INTERACT_DISTANCE, pos.x + INTERACT_DISTANCE, pos.y
						+ INTERACT_DISTANCE, Building.class)) {
			double dist = e.pos.distSqr(getInteractPosition());
			if (dist <= INTERACT_DISTANCE && dist < closestDist) {
				closestDist = dist;
				closest = e;
			}
		}
		if (selected != null) {
			((IUsable) selected).setHighlighted(false);
		}
		if (closest != null && ((IUsable) closest).isHighlightable()) {
			selected = closest;
			((IUsable) selected).setHighlighted(true);
		}

		if (selected != null) {
			if (selected.pos.distSqr(getInteractPosition()) > INTERACT_DISTANCE) {
				((IUsable) selected).setHighlighted(false);
				selected = null;
			} else if (selected instanceof IUsable
					&& (keys.use.wasPressed() || mouseButtons
							.isDown(mouseUseButton))) {
				((IUsable) selected).use(this);
				mouseButtons.setNextState(mouseUseButton, false);
			} else if (selected instanceof IUsable && keys.upgrade.wasPressed()) {
				((IUsable) selected).upgrade(this);
			}
		}
	}

	public void payCost(int cost) {
		score -= cost;

		while (cost > 0) {
			double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI
					* 2;
			Loot loot = new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir),
					cost / 2);
			loot.makeUntakeable();
			level.addEntity(loot);

			cost -= loot.getScoreValue();
		}
	}

	public void addScore(int s) {
		if (s > 0) {
			score += s;
		}
	}

	public void dropAllMoney() {

		score /= 2;
		while (score > 0) {
			double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI
					* 2;
			Loot loot = new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir),
					score / 2);
			level.addEntity(loot);

			score -= loot.getScoreValue();
		}
		score = 0;
	}

	@Override
	public void render(Screen screen) {
		Bitmap[][] sheet = Art.lordLard;
		if(alternative) {
			sheet = Art.duchessDonut;
		}
		if(team == Team.Team2) {
			sheet = Art.herrSpeck;
		}

		if (dead) {
			// don't draw anything if we are dead (in a hole)
			return;
		}

		int frame = (walkTime / 4 % 6 + 6) % 6;

		int facing = this.facing + (carrying != null ? 8 : 0);
		double xmuzzle = muzzleX + ((facing == 0) ? 4 : 0);
		double ymuzzle = muzzleY - ((facing == 0) ? 4 : 0);

		boolean behind = (facing >= 3 && facing <= 5);

		if (muzzleTicks > 0 && behind) {
			screen.blit(Art.muzzle[muzzleImage][0], xmuzzle, ymuzzle);
		}

		if (hurtTime % 2 != 0) {
			screen.colorBlit(sheet[frame][facing], pos.x - Tile.WIDTH / 2,
					pos.y - Tile.HEIGHT / 2 - 8, 0x80ff0000);
		} else if (flashTime > 0) {
			screen.colorBlit(sheet[frame][facing], pos.x - Tile.WIDTH / 2,
					pos.y - Tile.HEIGHT / 2 - 8, 0x80ffff80);
		} else {
			screen.blit(sheet[frame][facing], pos.x - Tile.WIDTH / 2, pos.y
					- Tile.HEIGHT / 2 - 8);
		}

		if (muzzleTicks > 0 && !behind) {
			screen.blit(Art.muzzle[muzzleImage][0], xmuzzle, ymuzzle);
		}

		renderCarrying(screen, (frame == 0 || frame == 3) ? -1 : 0);
	}

	@Override
	protected void renderCarrying(Screen screen, int yOffs) {
		if (carrying != null && carrying.team == this.localTeam) {
			if (carrying instanceof Turret) {
				Turret turret = (Turret) carrying;
				screen.blit(turret.areaBitmap, pos.x - turret.areaBitmap.w / 2,
						pos.y - turret.areaBitmap.h / 2 - yOffs);
			} else if (carrying instanceof Harvester) {
				Harvester harvester = (Harvester) carrying;
				screen.blit(harvester.areaBitmap, pos.x
						- harvester.areaBitmap.w / 2, pos.y
						- harvester.areaBitmap.h / 2 - yOffs);
			}// TODO make an interface to clean this up
		}

		super.renderCarrying(screen, yOffs);
	}

	@Override
	public void collide(Entity entity, double xa, double ya) {
		xd += xa * 0.4;
		yd += ya * 0.4;
	}

	@Override
	public void take(Loot loot) {
		loot.remove();
		level.addEntity(new Sparkle(pos.x, pos.y, -1, 0));
		level.addEntity(new Sparkle(pos.x, pos.y, +1, 0));
		score += loot.getScoreValue();
	}

	@Override
	public double getSuckPower() {
		return suckRadius / 60.0;
	}

	@Override
	public boolean canTake() {
		return takeDelay > 0;
	}

	@Override
	public void flash() {
		flashTime = 20;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public Bitmap getSprite() {
		return null;
	}

	public boolean useMoney(int cost) {
		if (cost > score) {
			return false;
		}

		score -= cost;
		return true;
	}

	private Vec2 getInteractPosition() {
		return pos.add(new Vec2(Math
				.cos((facing) * (Math.PI) / 4 + Math.PI / 2), Math.sin((facing)
				* (Math.PI) / 4 + Math.PI / 2)).scale(30));
	}

	public void pickup(Building b) {
		if (b.team != this.team && b.team != Team.Neutral) {

			if (this.team == localTeam) {
				Notifications.getInstance().add(
						MojamComponent.texts.getStatic("gameplay.cantPickup"));
			}
			return;
		}

		level.removeEntity(b);
		carrying = b;
		carrying.onPickup();
	}

	public void setFacing(int facing) {
		this.facing = facing;
	}

	@Override
	protected boolean shouldBlock(Entity e) {
		if (carrying != null && e instanceof Bullet
				&& ((Bullet) e).owner == carrying) {
			return false;
		}
		return true;
	}

	public void setCanSee(boolean b) {
		this.isSeeing = b;
	}

	@Override
	public void notifySucking() {
	}

	@Override
	public void hurt(Entity source, float damage) {
		if (isImmortal) {
			return;
		}

		if (hurtTime == 0) {
			hurtTime = 25;
			freezeTime = 15;
			health -= damage;
			regenDelay = REGEN_INTERVAL;

			if (health <= 0) {
				revive();
			} else {

				double dist = source.pos.dist(pos);
				xBump = (pos.x - source.pos.x) / dist * 10;
				yBump = (pos.y - source.pos.y) / dist * 10;

				MojamComponent.soundPlayer.playSound("/sound/hit2.wav",
						(float) pos.x, (float) pos.y, true);
			}
		}
	}

	private void revive() {
		Notifications.getInstance().add(MojamComponent.texts.hasDied(team));
		carrying = null;
		dropAllMoney();
		pos.set(startX, startY);
		health = maxHealth;
	}

	@Override
	public void hurt(Bullet bullet) {
		hurt(bullet, 1);
	}

	@Override
	public String getDeathSound() {
		return "/sound/Death.wav";
	}

	/**
	 * used to update player orientation, values relative to player.
	 */
	public void setAimByMouse(int x, int y) {
		mouseAiming = true;
		aimVector.set(x, y);
		aimVector.normalizeSelf();
		updateFacing();
	}

	public void setAimByKeyboard() {
		mouseAiming = false;
	}

	/**
	 * Update facing for rendering
	 */
	public void updateFacing() {
		facing = (int) ((Math.atan2(-aimVector.x, aimVector.y) * 8
				/ (Math.PI * 2) + 8.5)) & 7;
	}

	public Vec2 getPosition() {
		return pos;
	}

	public void setLocalTeam(int i) {
		this.localTeam = i;
	}
}
