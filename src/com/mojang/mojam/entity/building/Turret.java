package com.mojang.mojam.entity.building;

import java.awt.Color;
import java.util.Set;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.RailDroid;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.tile.RailTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Defense turret. Automatically aims and shoots at the nearest monster.
 */
public class Turret extends Building {
	
	private static final float BULLET_DAMAGE = .75f;
	
	private int delayTicks = 0;
	private int delay;
	public int team;
	public int radius;
	public int radiusSqr;

	private int[] upgradeRadius = new int[] { 3 * Tile.WIDTH, 5 * Tile.WIDTH, 7 * Tile.WIDTH };
	private int[] upgradeDelay = new int[] { 24, 21, 18 };

	private int facing = 0;
	
	public static boolean creative = Options.getAsBoolean(Options.CREATIVE);
	private boolean doWarn = true;

	private Bitmap areaBitmap;
	private static final int RADIUS_COLOR = new Color(240, 210, 190).getRGB();

	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param team Team number
	 */
	public Turret(double x, double y, int team) {
		super(x, y, team);
		this.team = team;
		setStartHealth(10);
		freezeTime = 10;
		areaBitmap = Bitmap.rangeBitmap(radius,RADIUS_COLOR);
	}
	
	public Turret(double x, double y, int team, int upgradeLevel) {
		super(x, y, team);
		this.team = team;
		this.upgradeLevel = upgradeLevel;
		setStartHealth(10);
		freezeTime = 10;
		areaBitmap = Bitmap.rectangleBitmap(0,0,radius*2,radius*2,Color.YELLOW.getRGB());
	}

	@Override
	public void init() {
		makeUpgradeableWithCosts(new int[] { DifficultyInformation.calculateCosts(500), 
				DifficultyInformation.calculateCosts(1000), 
				DifficultyInformation.calculateCosts(5000)});
	}

	@Override
	public void tick() {
		if (carriedBy == null && level.getTile((int) pos.x/Tile.WIDTH, (int) pos.y/Tile.HEIGHT) 
				instanceof RailTile) railify(); else doWarn = true;
		super.tick();
		if (--freezeTime > 0)
			return;
		if (--delayTicks > 0)
			return;

		if (!isCarried()) {
		    // find target
    		Set<Entity> entities = level.getEntities(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius);
    
    		Entity closest = null;
    		double closestDist = 99999999.0f;
    		for (Entity e : entities) {
    			if (!(e instanceof Mob) || (e instanceof RailDroid && e.team == this.team) || e instanceof Bomb)
    				continue;
    			if (!((Mob) e).isNotFriendOf(this))
    				continue;
    			final double dist = e.pos.distSqr(pos);
    			Bullet bullet = new Bullet(this, pos.x, pos.y, 0);
    			if (dist < radiusSqr && dist < closestDist && !isTargetBehindWall(e.pos.x, e.pos.y, bullet)) {
    				closestDist = dist;
    				closest = e;
    			}
    		}
    		if (closest != null) {
    		    // shoot
        		double invDist = 1.0 / Math.sqrt(closestDist);
        		double yd = closest.pos.y - pos.y;
        		double xd = closest.pos.x - pos.x;
        		double angle = (Math.atan2(yd, xd) + Math.PI * 1.625);
        		facing = (8 + (int) (angle / Math.PI * 4)) & 7;
        		Bullet bullet = new Bullet(this, xd * invDist, yd * invDist, BULLET_DAMAGE * ((upgradeLevel + 1) / 2.f));
        		bullet.pos.y -= 10;
        		level.addEntity(bullet);
        
        		if (upgradeLevel > 0) {
        			Bullet second_bullet = new Bullet(this, xd * invDist, yd * invDist, BULLET_DAMAGE * ((upgradeLevel + 1) / 2.f));
        			level.addEntity(second_bullet);
        			if (facing == 0 || facing == 4) {
        				bullet.pos.x -= 5;
        				second_bullet.pos.x += 5;
        			}
        		}
        
        		delayTicks = delay;
    		}
		}
	}

	@Override
	public void render(Screen screen) {
		
		if((justDroppedTicks-- > 0 || highlight) && MojamComponent.localTeam==team) {
				drawRadius(screen);
		}
		
		super.render(screen);
	}

	@Override
	public Bitmap getSprite() {
		switch (upgradeLevel) {
		case 1:
			return Art.turret2[facing][0];
		case 2:
			return Art.turret3[facing][0];
		default:
			return Art.turret[facing][0];
		}
	}

	@Override
	protected void upgradeComplete() {
		maxHealth += 10;
		health = maxHealth;
		delay = upgradeDelay[upgradeLevel];
		radius = upgradeRadius[upgradeLevel];
		radiusSqr = radius * radius;
		areaBitmap = Bitmap.rangeBitmap(radius,RADIUS_COLOR);
		if (upgradeLevel != 0) justDroppedTicks = 80; //show the radius for a brief time
	}
	
	public void railify() {
		System.out.println("Railify "+this.getClass().getName());
		if (((Player)lastCarrying).getScore() > RailTurret.cost || creative) {
			if (!creative) ((Player)lastCarrying).payCost(RailTurret.cost);
		  this.remove();
		  level.removeEntity(this);
		  level.removeFromEntityMap(this);
		  level.addEntity(new RailTurret(pos.x, pos.y, team, upgradeLevel, ((Player)lastCarrying)));
		} else {
		  if (doWarn) Notifications.getInstance().add(
					MojamComponent.texts.upgradeNotEnoughMoney(RailTurret.cost));
		  doWarn = false;
		}
		}
	public void drawRadius(Screen screen) {
		screen.opacityBlit(areaBitmap, (int) pos.x-radius, (int) pos.y-radius - yOffs, 0xDD);	
	}
}
