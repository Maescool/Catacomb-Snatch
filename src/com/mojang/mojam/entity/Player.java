package com.mojang.mojam.entity;

import java.util.Random;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.Keys;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.animation.SmokePuffAnimation;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.building.Harvester;
import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.loot.LootCollector;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.RailDroid;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.entity.particle.Sparkle;
import com.mojang.mojam.entity.weapon.IWeapon;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.entity.weapon.WeaponInventory;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.level.tile.PlayerRailTile;
import com.mojang.mojam.level.tile.RailTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

/**
 * Implements the player entity
 */
public class Player extends Mob implements LootCollector {
    public static int COST_RAIL;
    public static int COST_DROID;
    public static int COST_REMOVE_RAIL;
    public int REGEN_INTERVAL = 60 * 3;
    public int plevel;
    public double pexp;
    public double psprint;
    public boolean isSprint = false;
    public int timeSprint = 0;
    public int maxTimeSprint;
    public Keys keys;
    public MouseButtons mouseButtons;
    public int mouseFireButton = 1;
    public int mouseUseButton = 3;

    
    private boolean mouseAiming;
   
    public int takeDelay = 0;
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
    boolean isImmortal;
    private GameCharacter character;

    public WeaponInventory weaponInventory = new WeaponInventory();
    private int weaponSlot = 0;
    private boolean isWeaponChanged = false;
    
    private boolean isSprintIgnore = false;
    
    /**
     * Constructor
     * 
     * @param keys Key bindings for this player
     * @param mouseButtons Mouse Button state
     * @param x Initial x coordinate
     * @param y Initial y coordinate
     * @param team Team number
     */
    public Player(Keys keys, MouseButtons mouseButtons, int x, int y, int team, GameCharacter character) {
        super(x, y, team);
        this.keys = keys;
        this.mouseButtons = mouseButtons;
        this.character = character;

        startX = x;
        startY = y;

        plevel = 0; // will be displayed in GUI as lev 1
        pexp = 0;
        maxHealth = 5;
        health = 5;
        psprint = 1.5;
        maxTimeSprint = 100;
        aimVector = new Vec2(0, 1);
        score = 0;
        
        weaponInventory.add(new Rifle(this));
        weapon = weaponInventory.get(weaponSlot);
        setRailPricesAndImmortality();
    }
    
    /**
     * Handle creative mode
     */
    public void setRailPricesAndImmortality(){
    	if (Options.getAsBoolean(Options.CREATIVE)){
    		COST_RAIL = 0;
    		COST_DROID = 0;
    		COST_REMOVE_RAIL = 0;
    		isImmortal = true;
    	}else{
     		COST_RAIL = 10;
    		COST_DROID = 50;
    		COST_REMOVE_RAIL = 0;
    		isImmortal = false;
    	}
    }

    /**
     * Check if the player has reached enough XP for a levelup
     */
    private void handleLevelUp() {
        if (xpSinceLastLevelUp() >= nettoXpNeededForLevel(plevel+1)) {
            this.maxHealth++;
            plevel++;
            psprint += 0.1;
            maxTimeSprint += 20;

            MojamComponent.soundPlayer.playSound("/sound/levelUp.wav", (float) pos.x, (float) pos.y, true);
        }
    }

    /**
     * 
     * @param level to calculate summed up xp value for
     *
     * @return summed up xp value
     */
    public double summedUpXpNeededForLevel(int level){
        return (level * 7) * (level * 7);
    }
    
    /**
     * 
     * @param level to calculate netto xp value for
     *
     * @return netto xp value
     */
    public double nettoXpNeededForLevel(int level){
        if (level == 0) return 0;
        return summedUpXpNeededForLevel(level) - summedUpXpNeededForLevel(level-1);
    }
    
    /**
     * 
     * @return xp gained since last level up
     */
    public double xpSinceLastLevelUp(){
        return pexp - summedUpXpNeededForLevel(plevel);
    }
    
    @Override
    public void tick() {
        // If the mouse is used, update player orientation before level tick
        if (!mouseButtons.mouseHidden) {
            // Update player mouse, in world pixels relative to player
            setAimByMouse(
                    (mouseButtons.getX() - (MojamComponent.screen.getWidth() / 2)),
                    ((mouseButtons.getY() + 24) - (MojamComponent.screen.getHeight() / 2)));
        } else {
            setAimByKeyboard();
        }

        time++;
		
		this.doRegenTime();
			
        handleLevelUp();
        flashMiniMapIcon();
        countdownTimers();
        playStepSound();

        double xa = 0;
        double ya = 0;
        double xaShot = 0;
        double yaShot = 0;

        // Handle keys
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
            if (keys.right.isDown) {
                xa++;
            }
            if (keys.fireUp.isDown) {
                yaShot--;
            }
            if (keys.fireDown.isDown) {
                yaShot++;
            }
            if (keys.fireLeft.isDown) {
                xaShot--;
            }
            if (keys.fireRight.isDown) {
                xaShot++;
            }
        }

        // Handle mouse aiming
        if (!mouseAiming && !keys.fire.isDown && !mouseButtons.isDown(mouseFireButton) && xa * xa + ya * ya != 0) {
            aimVector.set(xa, ya);
            aimVector.normalizeSelf();
            updateFacing();
        }
        
        
        if (!mouseAiming && fireKeyIsDown() && xaShot * xaShot + yaShot * yaShot != 0) {
            aimVector.set(xaShot, yaShot);
            aimVector.normalizeSelf();
            updateFacing();
        }

        // Move player if it is not standing still
        if (xa != 0 || ya != 0) {
            handleMovement(xa, ya);
        } else {
        	restoreTimeSprint();
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
        handleWeaponSelection();

        int x = (int) pos.x / Tile.WIDTH;
        int y = (int) pos.y / Tile.HEIGHT;

        if (!dead && fallDownHole()) {
        	dead = true;
        	carrying = null;
        	deadDelay = 60;
        }

        if (dead && deadDelay <= 0) {
            dead = false;
            revive();
        }

        if (keys.build.isDown && !keys.build.wasDown) {
            handleRailBuilding(x, y);
        }
        
        handleCarrying();
        handleEntityInteraction();
        
        if (isSeeing) {
            level.reveal(x, y, 5);
        }
    }

    /**
     * Update the display cycle of the player indicator on the minimap
     */
    private void flashMiniMapIcon() {
        minimapIcon = time / 3 % 4;
        if (minimapIcon == 3) {
            minimapIcon = 1;
        }
    }

    /**
     * Count down the internal timers
     */
    protected void countdownTimers() {
    	super.countdownTimers();
    	
        if (muzzleTicks > 0) {
            muzzleTicks--;
        }
        if (deadDelay > 0) {
            deadDelay--;
        }
    }

    /**
     * Play step sounds synchronized to player movement and carrying status
     */
    private void playStepSound() {
        if (keys.up.isDown || keys.down.isDown || keys.left.isDown || keys.right.isDown) {
            int stepCount = 25;
            
            if (carrying == null) {
                stepCount = 15;
            }
            
            if (isSprint) {
                stepCount *= 0.6;
            }
            
            if (steps % stepCount == 0) {
                MojamComponent.soundPlayer.playSound("/sound/Step " + (TurnSynchronizer.synchedRandom.nextInt(2) + 1) + ".wav", (float) pos.x, (float) pos.y, true);
            }
            steps++;
        }
    }

    /**
     * Handler player movement
     * 
     * @param xa Position change on the x axis
     * @param ya Position change on the y axis
     */
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
            level.addEntity(new SmokePuffAnimation(this, Art.fxDust12, 35 + random.nextInt(10)));
            nextWalkSmokeTick += (15 + random.nextInt(15));
        }
        if (random.nextDouble() < 0.02f) {
            level.addEntity(new SmokePuffAnimation(this, Art.fxDust12, 35 + random.nextInt(10)));
        }

        double dd = Math.sqrt(xa * xa + ya * ya);
        double speed = getSpeed() / dd;

        if (this.keys.sprint.isDown) {
        	if (!isSprintIgnore) {
	            if (timeSprint < maxTimeSprint) {
	                isSprint = true;
	                if (carrying == null) {
	                    speed = getSpeed() / dd * psprint;
	                } else {
	                    speed = getSpeed() / dd * (psprint - 0.5);
	                }
	                timeSprint++;
	            } else {
	            	restoreTimeSprint();
	                isSprintIgnore = true;
	            }
        	} else {
        		restoreTimeSprint();
                isSprintIgnore = true;
        	}
        } else {
        	restoreTimeSprint();          
        }
        
        if (this.keys.sprint.wasReleased()) {
        	isSprintIgnore = false;
        }

        xa *= speed;
        ya *= speed;

        xd += xa;
        yd += ya;
    }
    
    private void restoreTimeSprint() {
    	if (timeSprint > 0) {
            timeSprint--;
        } 
    	isSprint = false;
    }

    /**
     * Handle weapon fire
     * 
     * @param xa Position change on the x axis
     * @param ya Position change on the y axis
     */
    private void handleWeaponFire(double xa, double ya) {
        weapon.weapontick();
        
        if (!dead
                && (carrying == null && fireKeyIsDown()
                || carrying == null && mouseButtons.isDown(mouseFireButton))) {
            wasShooting = true;
            if (takeDelay > 0) {
                takeDelay--;
            }
            weapon.primaryFire(xa, ya);
        } else {
            if (wasShooting) {
                suckRadius = 0;
            } else {
            	suckRadius = 60;
            }
            wasShooting = false;
            takeDelay = 15;
        }
    }
    
    private void handleWeaponSelection() {
        //Weapon selection
    	int prevWeaponSlot = weaponSlot;
        if (keys.weaponSlot1.wasPressed()) {
        	weaponSlot = 0;
        }
        if (keys.weaponSlot2.wasPressed()) {
        	weaponSlot = 1;
        }
        if (keys.weaponSlot3.wasPressed()) {
        	weaponSlot = 2;
        }
        if (keys.cycleLeft.wasPressed()) {
        	weaponInventory.cycleLeft();
        	isWeaponChanged = true;
        }
        if (keys.cycleRight.wasPressed()) {
        	weaponInventory.cycleRight();
        	isWeaponChanged = true;
        }
        if (prevWeaponSlot != weaponSlot) {
        	isWeaponChanged = true;
        }
    	if (isWeaponChanged) {
    		IWeapon weapon = weaponInventory.get(weaponSlot);
    		if(weapon != null) {
    			this.weapon = weapon;
    		}
    		else weaponSlot = prevWeaponSlot;
    		isWeaponChanged = false;
    	}
    }
    
    /**
     * Returns true if one of the keyboard fire buttons is down
     * @return
     */
    private boolean fireKeyIsDown() {
        return keys.fire.isDown || keys.fireUp.isDown || keys.fireDown.isDown || keys.fireRight.isDown || keys.fireLeft.isDown;
    }

    /**
     * Handle rail building
     * 
     * @param x current position's X coordinate
     * @param y current position's Y coordinate
     */
    private void handleRailBuilding(int x, int y) {
    	Tile tile = level.getTile(x, y);
    	
        if (tile.isBuildable()) {
            if (score >= COST_RAIL && time - lastRailTick >= RailDelayTicks) {
                lastRailTick = time;
                level.placeTile(x, y, new RailTile(), this);
                payCost(COST_RAIL);
            } else if (score < COST_RAIL && this.team == MojamComponent.localTeam) {
            	Notifications.getInstance().add(MojamComponent.texts.buildRail(COST_RAIL));
            }            
        } else if (tile instanceof RailTile) {
            if (tile instanceof PlayerRailTile) {
            	if ( ((PlayerRailTile) tile).isTeam(team)) {
	                if (score >= COST_DROID) {
	                    level.addEntity(new RailDroid(pos.x, pos.y, team));
	                    payCost(COST_DROID);
	                } else {
	                	if(this.team == MojamComponent.localTeam) {
	                		Notifications.getInstance().add(MojamComponent.texts.buildDroid(COST_DROID));
	                	}
	                }
            	}
            } else {
                if (score >= COST_REMOVE_RAIL && time - lastRailTick >= RailDelayTicks) {
                    lastRailTick = time;
                    if (((RailTile) tile).remove()) {
                        payCost(COST_REMOVE_RAIL);
                    }
                } else if (score < COST_REMOVE_RAIL) {
                	if(this.team == MojamComponent.localTeam) {
                		Notifications.getInstance().add(MojamComponent.texts.removeRail(COST_REMOVE_RAIL));
                	}
                }
                MojamComponent.soundPlayer.playSound("/sound/Track Place.wav", (float) pos.x, (float) pos.y);
            }
        }
    }

    /**
     * Handle object carrying
     */
    private void handleCarrying() {

        if (keys.use.wasPressed() || mouseButtons.isDown(mouseUseButton)) {
            mouseButtons.setNextState(mouseUseButton, false);

            if(selected != null) {
            	if (selected instanceof ICarrySwap) {
            		carrying=((ICarrySwap)selected).tryToSwap(carrying);
            		if (carrying != null) {
            			carrying.onPickup(this);
            		}
            	}
            } else {
            	if (!isCarrying())
            		return;
            	
            	if (((IUsable) carrying).isAllowedToCancel()) {
            		drop();
            	}
            }
        }
        
    	if (!isCarrying())
    		return;
    	
        carrying.setPos(pos.x, pos.y - 20);
        carrying.tick();
    }

    /**
     * Handle interaction with entities
     */
    private void handleEntityInteraction() {
        // Unhighlight previously selected building
    	if (selected != null && selected instanceof IUsable) {
    		((IUsable)selected).setHighlighted(false);
            selected = null;
        }

        // Find the closest Entity within interaction
        // distance
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : level.getEntitiesSlower(pos.x - INTERACT_DISTANCE, pos.y - INTERACT_DISTANCE, pos.x + INTERACT_DISTANCE, pos.y + INTERACT_DISTANCE, Mob.class)) {
            double dist = e.pos.distSqr(getInteractPosition());
            if (dist <= INTERACT_DISTANCE && dist < closestDist && e instanceof IUsable) {
                closestDist = dist;
                closest = e;
            }
        }

        // If we found a entity close enough to interact with...
        if (closest != null) {
            // Perform any allowed interactions if the correct
            // keys have been pressed
            if (keys.use.wasPressed() || mouseButtons.isDown(mouseUseButton)) {

                if (canUseEntity(closest)) {
                	((IUsable)closest).use(this);
                    mouseButtons.setNextState(mouseUseButton, false);
                }
            } else if (keys.upgrade.wasPressed()) {
                
                if (canUpgradeEntity(closest)) {
                	((IUsable)closest).upgrade(this);
                }
            }

			/**
			 * If it is a building we should highlight on this game
			 * client, then highlight the building (also, remember the
			 * highlighted building, so we can unhighlight it again later)
			 */
            if (shouldHighlightEntity(closest)) {
                selected = closest;
                ((IUsable)selected).setHighlighted(true);
            }
        }
    }
  
    /**
     *  Whether this Player should highlight the entity in question, Multi player safe.
     * @param entity the entity this player is trying to highlight
     * @return true if this player can highlight the given entity
     */
    private boolean shouldHighlightEntity(Entity entity) {
    	if (!(entity instanceof IUsable))
    		return false;
    	return ((IUsable)entity).isHighlightable() && canInteractWithEntity(entity);// && this.team == entity.team; 
    }
    
    // Whether this Player should see the Entity in question
    // highlighted on their game client - this indicates that
    // they can interact with the Entity
    private boolean shouldHighlightEntityOnThisGameClient(Entity entity) {
    	if (!(entity instanceof IUsable))
    		return false;
    	return ((IUsable)entity).isHighlightable() && canInteractWithEntity(entity) && this.team == MojamComponent.localTeam; 
    }
    
    // Whether this Player is allowed to use the Entity in 
    // question
    private boolean canUseEntity(Entity entity) {
        //return building.team == this.team || building.team == Team.Neutral; // Players can only use their own and neutral buildings
    	return !(entity instanceof ShopItem && entity.team != this.team); // Players can only use their own shops, but can use any other building regardless of ownership
    }
    
    // Whether this Player is allowed to upgrade the Entity
    // in question
    private boolean canUpgradeEntity(Entity entity) {
    	return entity.team == this.team; // Players can only upgrade their own Entities
    }
    
    // Whether this Player is allowed to interact with the Entity in 
    // question
    private boolean canInteractWithEntity(Entity entity) {
    	return canUseEntity(entity) || canUpgradeEntity(entity);
    }

    /**
     * Pay for an item
     * 
     * @param cost Item cost
     */
    public void payCost(int cost) {
        score -= cost;

        while (cost > 0) {
            double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
            Loot loot = new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir), cost / 2);
            loot.makeUntakeable();
            level.addEntity(loot);

            cost -= loot.getScoreValue();
        }
    }

    /**
     * Add score points
     * 
     * @param points Points
     */
    public void addScore(int points) {
        if (points > 0) {
            score += points;
        }
    }

    /**
     * Drop all money. Animated, loot items will fall on the floor.
     */
    public void dropAllMoney() {
        score /= 2;
        
        while (score > 0) {
            double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
            Loot loot = new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir), score / 2);
            level.addEntity(loot);

            score -= loot.getScoreValue();
        }
        score = 0;
    }

    @Override
    public void render(AbstractScreen screen) {
		AbstractBitmap[][] sheet = Art.getPlayer(getCharacter());
    	
		if(sheet == null){
			return;
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
            screen.colorBlit(sheet[frame][facing], pos.x - Tile.WIDTH / 2, pos.y - Tile.HEIGHT / 2 - 8, 0x80ff0000);
        } else if (getFlashTime() > 0) {
            screen.colorBlit(sheet[frame][facing], pos.x - Tile.WIDTH / 2, pos.y - Tile.HEIGHT / 2 - 8, 0x80ffff80);
        } else {
            screen.blit(sheet[frame][facing], pos.x - Tile.WIDTH / 2, pos.y - Tile.HEIGHT / 2 - 8);
        }

        if (muzzleTicks > 0 && !behind) {
            screen.blit(Art.muzzle[muzzleImage][0], xmuzzle, ymuzzle);
        }
        
        addSprintBar(screen);
	}
    
    private void addSprintBar(AbstractScreen screen) {
    	if (this.timeSprint <= 0) { 
    		return; 
    	}
    	int start = (int) (this.timeSprint * 20 / this.maxTimeSprint);
        screen.blit(Art.sprintBar[start][0], pos.x - 16, pos.y + 8);
    }
    
    public void setCharacter(GameCharacter character) {
		this.character = character;
	}
    
    public GameCharacter getCharacter(){
    	return character;
    }

	@Override
	public void renderTop(AbstractScreen screen) {
		int frame = (walkTime / 4 % 6 + 6) % 6;
		renderCarrying(screen, (frame == 0 || frame == 3) ? -1 : 0);
	}
    
    @Override
    protected void renderCarrying(AbstractScreen screen, int yOffs) {
    	if(carrying != null && carrying.team == MojamComponent.localTeam ) {
			if(carrying instanceof Turret) {
				Turret turret = (Turret)carrying;
				turret.drawRadius(screen);	
			} else if(carrying instanceof Harvester) {
				Harvester harvester = (Harvester)carrying;
				harvester.drawRadius(screen);	
			}//TODO make an interface to clean this up
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
        setFlashTime(5);
    }

    @Override
    public int getScore() {
        return score;
    }

    public int getActiveWeaponSlot() {
    	return weaponSlot;
    }
    
    @Override
    public AbstractBitmap getSprite() {
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
        return pos.add(new Vec2(Math.cos((facing) * (Math.PI) / 4 + Math.PI / 2), Math.sin((facing) * (Math.PI) / 4 + Math.PI / 2)).scale(30));
    }


    public void drop() {
        carrying.xSlide = aimVector.x * 5;
        carrying.ySlide = aimVector.y * 5;
        super.drop();
    }

    /**
     * Set player orientation
     * 
     * @param facing New Orientation
     */
    public void setFacing(int facing) {
        this.facing = facing;
    }

    @Override
    protected boolean shouldBlock(Entity e) {
        if (carrying != null && e instanceof Bullet && ((Bullet) e).owner == carrying) {
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
            
            if (health <= 0) {
                revive();
            } else {

                double dist = source.pos.dist(pos);
                xBump = (pos.x - source.pos.x) / dist * 10;
                yBump = (pos.y - source.pos.y) / dist * 10;

                MojamComponent.soundPlayer.playSound("/sound/hit2.wav", (float) pos.x, (float) pos.y, true);
            }
        }
    }

    /**
     * Revive the player. Carried items are lost, as is all the money.
     */
    private void revive() {
        Notifications.getInstance().add(MojamComponent.texts.hasDiedCharacter(getCharacter()));
        carrying = null;
        dropAllMoney();
        Vec2 randomSpawnPoint = level.getRandomSpawnPoint(team);
        pos.set(randomSpawnPoint.x, randomSpawnPoint.y);
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
     * Orientate the player in the direction of the given mouse coordinates
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setAimByMouse(int x, int y) {
    	/* Only update aim if vector has direction */
    	if (x != 0 || y != 0) {
	        mouseAiming = true;
	        aimVector.set(x, y);
	        aimVector.normalizeSelf();
	        updateFacing();
    	}
    }

    /**
     * Disable mouse aiming and activate keyboard aiming
     */
    public void setAimByKeyboard() {
        mouseAiming = false;
    }

    /**
     * Update player orientation for rendering
     */
    public void updateFacing() {
        facing = (int) ((Math.atan2(-aimVector.x, aimVector.y) * 8 / (Math.PI * 2) + 8.5)) & 7;
    }



}
