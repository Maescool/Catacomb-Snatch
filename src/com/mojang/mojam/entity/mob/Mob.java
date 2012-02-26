package com.mojang.mojam.entity.mob;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.animation.EnemyDieAnimation;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.HoleTile;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public abstract class Mob extends Entity {

	public final static double CARRYSPEEDMOD = 1.2;
	public final static int MoveControlFlag = 1;

	// private double speed = 0.82;
	public double speed = 1.0;
	public int team;
	protected boolean doShowHealthBar = true;
    protected int healthBarOffset = 10;
	double dir = 0;
	public int hurtTime = 0;
	public int freezeTime = 0;
	public int bounceWallTime = 0;
	public float maxHealth = 10;
	public float health = maxHealth;
	public boolean isImmortal = false;
	public double xBump, yBump;
	public Building carrying = null;
	public int yOffs = 8;
	public double xSlide;
	public double ySlide;
	public int deathPoints = 0;
	public boolean chasing=false;
	public int justDroppedTicks = 0;
	public int localTeam;
	public int strength = 0;
	public int healingInterval;
	public int healingTime;
	public boolean healthRegen = true;
    public int facing;
    public int walkTime;
    public int stepTime;
    public int limp;
	
	public Mob(double x, double y, int team, int localTeam) {
		super();
		setPos(x, y);
		this.team = team;
		this.localTeam = localTeam;
		DifficultyInformation difficulty = TitleMenu.difficulty;
		healingInterval = (difficulty != null && difficulty.difficultyID == 3) ? 15 : 25;
		healingTime = healingInterval;
	}

	public void init() {
		super.init();
	}

	public void setStartHealth(float newHealth) {
		maxHealth = health = newHealth;
	}

	public double getSpeed() {
		return carrying != null ? speed * CARRYSPEEDMOD : speed;
	}

	public void deltaMove(Vec2 v) {
		super.move(v.x, v.y);
	}

	public int getTeam() {
		return team;
	}

	public boolean isEnemyOf(Mob m) {
		if (team == Team.Neutral || m.team == Team.Neutral)
			return false;
		return team != m.team;
	}

	public boolean isNotFriendOf(Mob m) {
		return team != m.team;
	}

	public void tick() {
		if (TitleMenu.difficulty.difficultyID >= 1 && healthRegen) {
	  	if (hurtTime <= 0) {
			  if (health < maxHealth) {
			  	if (--healingTime <= 0) {
			  		health++;
			  		healingTime = healingInterval;
			  	}
			  }
			}
		}
		if (hurtTime > 0) {
			hurtTime--;
		}
		if (bounceWallTime > 0) {
			bounceWallTime--;
		}

		if (freezeTime > 0) {
			slideMove(xSlide, ySlide);
			xSlide *= 0.8;
			ySlide *= 0.8;

			if (xBump != 0 || yBump != 0) {
				move(xBump, yBump);
			}
			freezeTime--;
			return;
		} else {
			xSlide = ySlide = 0;
			if (health <= 0) {
				die();
				remove();
				return;
			}
		}
	}

	public void slideMove(double xa, double ya) {
		move(xa, ya);
	}

	public void die() {
		int particles = 8;
		//
		// for (int i = 0; i < particles; i++) {
		// double dir = i * Math.PI * 2 / particles;
		// level.addEntity(new Particle(pos.x, pos.y, Math.cos(dir),
		// Math.sin(dir)));
		// }

		if (getDeathPoints() > 0) {
			int loots = 4;
			for (int i = 0; i < loots; i++) {
				double dir = i * Math.PI * 2 / particles;

				level.addEntity(new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir), getDeathPoints()));
			}
		}

		level.addEntity(new EnemyDieAnimation(pos.x, pos.y));

		MojamComponent.soundPlayer.playSound(getDeathSound(), (float) pos.x, (float) pos.y);
	}

	public String getDeathSound() {
		return "/sound/Explosion.wav";
	}

	public boolean shouldBounceOffWall(double xd, double yd) {
		if (bounceWallTime > 0)
			return false;
		Tile nextTile = level.getTile((int) (pos.x / Tile.WIDTH + Math.signum(xd)), (int) (pos.y / Tile.HEIGHT + Math.signum(yd)));
		boolean re = (nextTile != null && !nextTile.canPass(this));
		if (re)
			bounceWallTime = 10;
		return re;
	}

	public void render(Screen screen) {
		Bitmap image = getSprite();
		if (hurtTime > 0) {
			if (hurtTime > 40 - 6 && hurtTime / 2 % 2 == 0) {
				screen.colorBlit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs, 0xa0ffffff);
			} else {
				if (health < 0)
					health = 0;
				int col = (int) (180 - health * 180 / maxHealth);
				if (hurtTime < 10)
					col = col * hurtTime / 10;
				screen.colorBlit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs, (col << 24) + 255 * 65536);
			}
		} else {
					
			screen.blit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs);
		}

		if (doShowHealthBar && health < maxHealth) {
            addHealthBar(screen);
        }
	}

	protected void addHealthBar(Screen screen) {
        
        int start = (int) (health * 21 / maxHealth);
        
        screen.blit(Art.healthBar[start][0], pos.x - 16, pos.y + healthBarOffset);
    }

	protected void renderCarrying(Screen screen, int yOffs) {
		if (carrying == null)
			return;

		carrying.yOffs -= yOffs;
		carrying.render(screen);
		carrying.yOffs += yOffs;
	}

	public abstract Bitmap getSprite();

	public void hurt(Entity source, float damage) {
		if (isImmortal)
			return;
		
		healingTime = healingInterval;

		if (freezeTime <= 0) {
			
			if (source instanceof Bullet && !(this instanceof SpawnerEntity) && !(this instanceof RailDroid)) {
				Bullet bullet = (Bullet) source;
				if (bullet.owner instanceof Player) {
					Player pl = (Player) bullet.owner;
					pl.pexp++;
				}
			}
			
			hurtTime = 40;
			freezeTime = 5;
			health -= damage;
			if (health < 0) {
				health = 0;
			}

			double dist = source.pos.dist(pos);
			xBump = (pos.x - source.pos.x) / dist * 2;
			yBump = (pos.y - source.pos.y) / dist * 2;
		}
	}

	@Override
	public void collide(Entity entity, double xa, double ya) {
		xd += xa * 0.4;
		yd += ya * 0.4;
	}

	public int getDeathPoints() {
		return deathPoints;
	}

	public void pickup(Building b) {
        if (b.health > 0) {
            level.removeEntity(b);
            carrying = b;
            carrying.onPickup(this);
        }
	}
	
	public void drop() {
        carrying.removed = false;
        carrying.freezeTime = 10;
        carrying.justDroppedTicks=80;
        carrying.setPos(pos);
        level.addEntity(carrying);
        carrying.onDrop();
        carrying = null;
	}

	public boolean isCarrying() {
		return (this.carrying != null);
	}
    
    public boolean isTargetBehindWall(double dx2, double dy2, Entity e) {
        int x1 = (int) pos.x / Tile.WIDTH;
        int y1 = (int) pos.y / Tile.HEIGHT;
        int x2 = (int) dx2 / Tile.WIDTH;
        int y2 = (int) dy2 / Tile.HEIGHT;

        int dx, dy, inx, iny, a;
        Tile temp;
        Tile dTile1;
        Tile dTile2;
        dx = x2 - x1;
        dy = y2 - y1;
        inx = dx > 0 ? 1 : -1;
        iny = dy > 0 ? 1 : -1;

        dx = java.lang.Math.abs(dx);
        dy = java.lang.Math.abs(dy);

        if (dx >= dy) {
            dy <<= 1;
            a = dy - dx;
            dx <<= 1;
            while (x1 != x2) {
                temp = level.getTile(x1, y1);
                if (!temp.canPass(e)) {
                    return true;
                }
                if (a >= 0) {
                	dTile1=level.getTile(x1+inx,y1);
                	dTile2=level.getTile(x1,y1+iny);
                	if (!(dTile1.canPass(e)||dTile2.canPass(e))){
                		return true;
                	}
                    y1 += iny;
                    a -= dx;
                }
                a += dy;
                x1 += inx;
            }
        } else {
            dx <<= 1;
            a = dx - dy;
            dy <<= 1;
            while (y1 != y2) {
                temp = level.getTile(x1, y1);
                if (!temp.canPass(e)) {
                    return true;
                }
                if (a >= 0) {
                	dTile1=level.getTile(x1+inx,y1);
                	dTile2=level.getTile(x1,y1+iny);
                	if (!(dTile1.canPass(e)||dTile2.canPass(e))){
                		return true;
                	}
                	x1 += inx;
                    a -= dy;
                }
                a += dx;
                y1 += iny;
            }
        }
        temp = level.getTile(x1, y1);
        if (!temp.canPass(e)) {
            return true;
        }
        return false;
    }
    
    public boolean fallDownHole() {
    	int x=(int) pos.x/Tile.WIDTH;
    	int y=(int) pos.y/Tile.HEIGHT;
        if (level.getTile(x, y) instanceof HoleTile) {
        	level.addEntity(new EnemyDieAnimation(pos.x, pos.y));
        	MojamComponent.soundPlayer.playSound("/sound/Fall.wav", (float) pos.x, (float) pos.y);
        	if (!(this instanceof Player)){
        		remove();
        	}
        	return true;
        }
        return false;
    }
    
    public void walk(){
    	switch (facing) {
        case 0:
            yd -= speed;
            break;
        case 1:
            xd += speed;
            break;
        case 2:
            yd += speed;
            break;
        case 3:
            xd -= speed;
            break;
    	}
    	walkTime++;

    	if (walkTime / 12 % limp != 0) {
    		if (shouldBounceOffWall(xd, yd)) {
    			facing = (facing + 2) % 4;
    			xd = -xd;
    			yd = -yd;
    		}

    		stepTime++;
    		if ((!move(xd, yd) || (walkTime > 10 && TurnSynchronizer.synchedRandom.nextInt(200) == 0) && chasing==false)) {
    			facing = TurnSynchronizer.synchedRandom.nextInt(4);
    			walkTime = 0;
    		}
    	}
    	xd *= 0.2;
    	yd *= 0.2;
    }
}
