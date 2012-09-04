package com.mojang.mojam.entity.mob;


import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.ICarrySwap;
import com.mojang.mojam.entity.IUsable;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.building.CatacombTreasure;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.loot.Loot;
import com.mojang.mojam.entity.loot.LootCollector;
import com.mojang.mojam.level.tile.PlayerRailTile;
import com.mojang.mojam.level.tile.RailTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.Art;

public class RailDroid extends Mob implements IUsable, ICarrySwap, LootCollector{
	private enum Direction {
		UNKNOWN, LEFT, UP, RIGHT, DOWN;

		private Direction turnBy180DegreesRight() {
			switch (this) {
			case LEFT:
				return RIGHT;
			case UP:
				return DOWN;
			case RIGHT:
				return LEFT;
			case DOWN:
				return UP;
			case UNKNOWN:
			default:
				return UNKNOWN;
			}
		}
	}

	private Direction dir = Direction.UNKNOWN;
	private Direction lDir = Direction.DOWN;
	private int waitForTurnTime = 0;
	private int pauseTime = 0;
	public int swapTime = 0;
	public int team;
	public static boolean creative = Options.getAsBoolean(Options.CREATIVE);

	boolean isOnRailTile;
	boolean isOnPlayerRailTile;
	boolean canGoLeft;
    boolean canGoRight;
    boolean canGoUp;
    boolean canGoDown;
	
    double xOffsetToTileCenter;
    double yOffsetToTileCenter;
    
	public RailDroid(double x, double y, int team) {
		super(x, y, team);
		this.team = team;
		this.setSize(10, 8);
		deathPoints = 1;
		
		if(creative)
			isImmortal = true;
	}

    public void tick() {
        xBump = yBump = 0;

        super.tick();
        
        if (isCarrying()) {
            handleCarrying();
        }
        
        boolean hadPaused = pauseTime > 0;

        if (freezeTime > 0)
            return;

        if (decreaseTimers())
            return;

        int xTile = (int) (pos.x / Tile.WIDTH);
        int yTile = (int) (pos.y / Tile.HEIGHT);

        isOnRailTile = level.getTile(xTile, yTile) instanceof RailTile;
        if (isOnRailTile) {
        	isOnPlayerRailTile = level.getTile(xTile, yTile) instanceof PlayerRailTile;
        	if (isOnPlayerRailTile) {
        		isOnPlayerRailTile = ((PlayerRailTile) level.getTile(xTile, yTile)).isTeam(team);
        	}
        }
        canGoLeft = level.getTile(xTile - 1, yTile) instanceof RailTile;
        canGoRight = level.getTile(xTile + 1, yTile) instanceof RailTile;
        canGoUp = level.getTile(xTile, yTile - 1) instanceof RailTile;
        canGoDown = level.getTile(xTile, yTile + 1) instanceof RailTile;

        xd *= 0.4;
        yd *= 0.4;

        xOffsetToTileCenter = pos.x - (xTile * Tile.WIDTH + 16);
        yOffsetToTileCenter = pos.y - (yTile * Tile.HEIGHT + 16);

        boolean isNearlyCentered = xOffsetToTileCenter * xOffsetToTileCenter
                + yOffsetToTileCenter * yOffsetToTileCenter < 2 * 2;
        boolean isNearlyCenteredX = xOffsetToTileCenter * xOffsetToTileCenter < 2 * 2;
        boolean isNearlyCenteredY = yOffsetToTileCenter * yOffsetToTileCenter < 2 * 2;

        if (!isNearlyCenteredX) {
            canGoUp = false;
            canGoDown = false;
        }
        if (!isNearlyCenteredY) {
            canGoLeft = false;
            canGoRight = false;
        }

        determineDirection(isNearlyCentered);
        centerPosition();

        double speed = 0.7;
        if (dir != Direction.UNKNOWN)
            lDir = dir;
        switch (dir) {
        case LEFT:
            xd -= speed;
            break;
        case RIGHT:
            xd += speed;
            break;
        case UP:
            yd -= speed;
            break;
        case DOWN:
            yd += speed;
            break;
		default:
			break;
        }

        Vec2 oldPos = pos.clone();
        move(xd, yd);

        if (hasNotMoved(oldPos)) {
            if (hadPaused) {
                dir = dir.turnBy180DegreesRight();
                waitForTurnTime = 0;
            } else {
                pauseTime = 10;
                waitForTurnTime = 0;
            }
        }

        pickUpTreasure();
        increaseScoreAtBase();
    }

    private boolean hasNotMoved(Vec2 oldPos) {
        return dir != Direction.UNKNOWN && oldPos.distSqr(pos) < 0.1 * 0.1;
    }

    /**
     * Handle object carrying
     */
    private void handleCarrying() {
        carrying.setPos(pos.x, pos.y - 20);
        carrying.tick();
    }
    
    private boolean decreaseTimers() {
        boolean shouldReturnFromTick = false;
        if (swapTime > 0) {
            swapTime--;
        }
        if (pauseTime > 0) {
            pauseTime--;
            if (pauseTime > 0)
                shouldReturnFromTick = true;
        }
        if (waitForTurnTime > 0) {
            waitForTurnTime--;
        }
        return shouldReturnFromTick;
    }

    private void determineDirection(boolean isAlmostCentered) {
        int leftWeight = 0;
        int upWeight = 0;
        int rightWeight = 0;
        int downWeight = 0;

        // no idea what magic happens here, maybe someone can refactor this to be more clear
        if (waitForTurnTime == 0
                && (!isOnRailTile || dir == Direction.UNKNOWN || isAlmostCentered)) {
            waitForTurnTime = 4;
            if (dir == Direction.LEFT && canGoUp)
                upWeight += 16;
            if (dir == Direction.UP && canGoRight)
                rightWeight += 16;
            if (dir == Direction.RIGHT && canGoDown)
                downWeight += 16;
            if (dir == Direction.DOWN && canGoLeft)
                leftWeight += 16;

            if (leftWeight + upWeight + rightWeight + downWeight == 0) {
                if (dir == Direction.LEFT && canGoLeft)
                    leftWeight += 16;
                if (dir == Direction.UP && canGoUp)
                    upWeight += 16;
                if (dir == Direction.RIGHT && canGoRight)
                    rightWeight += 16;
                if (dir == Direction.DOWN && canGoDown)
                    downWeight += 16;
            }

            if (leftWeight + upWeight + rightWeight + downWeight == 0) {
                if ((dir == Direction.LEFT || dir == Direction.RIGHT)) {
                    if (canGoUp)
                        upWeight += 4;
                    if (canGoDown)
                        downWeight += 4;
                }
                if ((dir == Direction.UP || dir == Direction.DOWN)) {
                    if (canGoLeft)
                        leftWeight += 4;
                    if (canGoRight)
                        rightWeight += 4;
                }
            }
            if (leftWeight + upWeight + rightWeight + downWeight == 0) {
                if (canGoLeft)
                    leftWeight += 1;
                if (canGoUp)
                    upWeight += 1;
                if (canGoRight)
                    rightWeight += 1;
                if (canGoDown)
                    downWeight += 1;
            }

            if (dir == Direction.LEFT)
                rightWeight = 0;
            if (dir == Direction.UP)
                downWeight = 0;
            if (dir == Direction.RIGHT)
                leftWeight = 0;
            if (dir == Direction.DOWN)
                upWeight = 0;

            int totalWeight = leftWeight + upWeight + rightWeight + downWeight;
            if (totalWeight == 0) {
                dir = Direction.UNKNOWN;
            } else {
                int res = TurnSynchronizer.synchedRandom.nextInt(totalWeight);
                dir = dir.turnBy180DegreesRight();

                upWeight += leftWeight;
                rightWeight += upWeight;
                downWeight += rightWeight;

                if (res < leftWeight) {
                    dir = Direction.LEFT;
                } else if (res < upWeight) {
                    dir = Direction.UP;
                } else if (res < rightWeight) {
                    dir = Direction.RIGHT;
                } else if (res < downWeight) {
                    dir = Direction.DOWN;
                }
            }
        }
    }

    private void centerPosition() {
        if (isOnRailTile) {
            double r = 1;
            if (!(dir == Direction.LEFT || dir == Direction.RIGHT)) {
                if (xOffsetToTileCenter < -r)
                    xd += 0.3;
                if (xOffsetToTileCenter > +r)
                    xd -= 0.3;
            }

            if (!(dir == Direction.UP || dir == Direction.DOWN)) {
                if (yOffsetToTileCenter < -r)
                    yd += 0.3;
                if (yOffsetToTileCenter > +r)
                    yd -= 0.3;
            }
        }
    }

    private void pickUpTreasure() {
    	
    	  if ( (carrying == null) && (swapTime == 0) ) {
            if (level.getEntities(getBB().grow(32), TreasurePile.class).size() > 0) {
                swapTime = 30;
                CatacombTreasure treasure = new CatacombTreasure(pos.x,pos.y);
                level.addEntity(treasure);
                pickup(treasure);
            }
        }
    	  
    }

    private void increaseScoreAtBase() {
    	if ( (carrying != null) && (carrying instanceof CatacombTreasure ) && (isOnPlayerRailTile) && (swapTime == 0) ) {
	    	carrying.die();
	        carrying = null;
            if (team == Team.Team2) {
                level.player2Score += 2;
            } else if (team == Team.Team1) {
                level.player1Score += 2;
            }
        }
    }
	
	@Override
	public AbstractBitmap getSprite() {
		if (lDir == Direction.LEFT)
			return Art.raildroid[1][1];
		if (lDir == Direction.UP)
			return Art.raildroid[0][1];
		if (lDir == Direction.RIGHT)
			return Art.raildroid[1][0];
		if (lDir == Direction.DOWN)
			return Art.raildroid[0][0];
		return Art.raildroid[0][0];
	}

	public void handleCollision(Entity entity, double xa, double ya) {
		super.handleCollision(entity, xa, ya);
		if (entity instanceof RailDroid) {
			RailDroid other = (RailDroid) entity;
			if (isCarrying()) {
				if (lDir == Direction.LEFT && other.pos.x > pos.x - 4)
					return;
				if (lDir == Direction.UP && other.pos.y > pos.y - 4)
					return;
				if (lDir == Direction.RIGHT && other.pos.x < pos.x + 4)
					return;
				if (lDir == Direction.DOWN && other.pos.y < pos.y + 4)
					return;

				if (other.lDir == Direction.LEFT && pos.x > other.pos.x - 4)
					return;
				if (other.lDir == Direction.UP && pos.y > other.pos.y - 4)
					return;
				if (other.lDir == Direction.RIGHT && pos.x < other.pos.x + 4)
					return;
				if (other.lDir == Direction.DOWN && pos.y < other.pos.y + 4)
					return;

				if (other.swapTime == 0 && swapTime == 0) {
					other.swapTime = swapTime = 15;

					if (other instanceof ICarrySwap) {
						carrying=((ICarrySwap)other).tryToSwap(carrying);
	            		if (carrying != null) {
	            			carrying.onPickup(this);
	            		}
					}
				}
			}
		}
	}

	@Override
	protected boolean shouldBlock(Entity e) {
		// if (e instanceof Player && ((Player) e).team == team) return false;
		if(e instanceof Bullet && ((Bullet) e).owner instanceof Turret && ((Bullet) e).owner.team == team) return false;
		return super.shouldBlock(e);
	}

	public void render(AbstractScreen screen) {
		super.render(screen);
		renderCarrying(screen, 0 );
	}

	@Override
	public void use(Entity user) {
	}

	@Override
	public boolean upgrade(Player player) {
		return false;
	}

	@Override
	public void setHighlighted(boolean hl) {
		this.setHighlight(hl);
		this.freezeTime = 10;
	}

	@Override
	public boolean isHighlightable() {
		return true;
	}

	@Override
	public boolean isAllowedToCancel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canCarry(Building b) {
		return true;
	}

	@Override
	public boolean canPickup(Building b) {
		if (!isCarrying())	
			return true;
		return false;
	}

	@Override
	public Building getCarrying() {
		return carrying; 
	}

	@Override
	public Building tryToSwap(Building b) {	
		Building tmpBuilding = null;
		if ( canCarry(b) ) {
			tmpBuilding = carrying;
			carrying=b;
			if (carrying != null) {
				carrying.onPickup(this);
			}
		}
		return tmpBuilding;
	}

	/**
	 * Proxy all LootCollector methods to carrying so Harvesters work!
	 */
	@Override
	public boolean canTake() {
		if (carrying != null && carrying instanceof LootCollector) {
			return ((LootCollector)carrying).canTake();
		}
		return false;
	}

	/**
	 * Proxy all LootCollector methods to carrying so Harvesters work!
	 */
	@Override
	public void take(Loot loot) {
		if (carrying != null && carrying instanceof LootCollector) {
			((LootCollector)carrying).take(loot);
		}
	}

	/**
	 * Proxy all LootCollector methods to carrying so Harvesters work!
	 */
	@Override
	public double getSuckPower() {
		if (carrying != null && carrying instanceof LootCollector) {
			return ((LootCollector)carrying).getSuckPower();
		}
		return 0;
	}

	/**
	 * Proxy all LootCollector methods to carrying so Harvesters work!
	 */
	@Override
	public void notifySucking() {
		if (carrying != null && carrying instanceof LootCollector) {
			((LootCollector)carrying).notifySucking();
		}
	}

	/**
	 * Proxy all LootCollector methods to carrying so Harvesters work!
	 */
	@Override
	public int getScore() {
		if (carrying != null && carrying instanceof LootCollector) {
			return ((LootCollector)carrying).getScore();
		}
		return 0;
	}

	/**
	 * Proxy all LootCollector methods to carrying so Harvesters work!
	 */
	@Override
	public void flash() {
		if (carrying != null && carrying instanceof LootCollector) {
			((LootCollector)carrying).flash();
		}
	}

}
