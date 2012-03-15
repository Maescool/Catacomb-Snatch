package com.mojang.mojam.entity.mob;


import com.mojang.mojam.entity.Bullet;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.level.tile.*;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

public class RailDroid extends Mob {
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

        boolean hadPaused = pauseTime > 0;

        if (freezeTime > 0)
            return;

        if (decreaseTimers())
            return;

        int xTile = (int) (pos.x / Tile.WIDTH);
        int yTile = (int) (pos.y / Tile.HEIGHT);

        isOnRailTile = level.getTile(xTile, yTile) instanceof RailTile;
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
        if (!carrying && swapTime == 0) {
            if (level.getEntities(getBB().grow(32), TreasurePile.class).size() > 0) {
                swapTime = 30;
                carrying = true;
            }
        }
    }

    private void increaseScoreAtBase() {
        if (carrying && swapTime == 0) {
            if (pos.y < 8 * Tile.HEIGHT) {
                carrying = false;
                level.player2Score += 2;
            }
            if (pos.y > (level.height - 7 - 1) * Tile.HEIGHT) {
                carrying = false;
                level.player1Score += 2;
            }
        }
    }
	
	@Override
	public Bitmap getSprite() {
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
			if (other.carrying != carrying && carrying) {
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

					boolean tmp = other.carrying;
					other.carrying = carrying;
					carrying = tmp;
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

	public void render(Screen screen) {
		super.render(screen);
		if (carrying) {
			screen.blit(Art.bullets[0][0], pos.x - 8, pos.y - 20 - yOffs);
		} else {
			screen.blit(Art.bullets[1][1], pos.x - 8, pos.y - 20 - yOffs);
		}
	}

}
