package com.mojang.mojam.entity.mob.pather;

import java.util.ArrayList;
import java.util.Set;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.HostileMob;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.AStar;
import com.mojang.mojam.level.Path;
import com.mojang.mojam.level.tile.*;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

/**
 * Extends HostileMob and ands 1. a* path finding 2. local object avoidance
 * Simply put the Pather uses a* to find its way to a target anywhere on the
 * Level it plots a path via the most direct route by building a list of Nodes.
 * <p>
 * While moving towards a Node it runs Local Object Avoidance, 1. collect a list
 * of Mobs and Tiles (walls) that need ot be avoided 2. store them in a generic
 * 'AvoidableObject' List 3. iterate though the list and based on its distance,
 * danger, radius and current angle rotate slightly the direction the Pather is
 * moving (think steering a car) 4. cap the maximum turn rate 5. recalculate the
 * movement vector based on the resulting direction angle.
 * <p>
 * When extending you must provide Vec2 getPathTarget() which the Pather uses to
 * get the initial a* based target.
 * 
 * @author Morgan Gilroy
 * @see HostileMob
 * @see Soldier
 */
public abstract class Pather extends HostileMob {

	public int facing;
	public double stepTime;

	/**
	 * Stores the a* object for caching
	 */
	private AStar aStar;

	/**
	 * Stores a Path that the Pather needs to walk down, generated from AStart
	 */
	private Path path;

	/**
	 * Used to stop the expensive a* path finding from running too often. when
	 * resetting the path set this to the number of ticks before it tries to
	 * find a new path
	 */
	private int pathTime;

	/**
	 * Distance at which it takes notice of Entities to avoid
	 */
	private double objectAvoidanceRadius = Tile.HEIGHT * 3;

	/**
	 * The maximum angle it can turn in a single tick
	 * 
	 * @see lastDirection
	 */
	private double maxTurnRate = Math.PI / 10;
	private double lastDirection = 0;

	/**
	 * Keeps a timer of how long the Pather has NOT been moving towards its
	 * target, if this gets too high it triggers a path reset to find another
	 * route.
	 * 
	 * @see lastGoalDistance
	 * @see goalTimeout
	 * @see maxGoalTimeout
	 */
	private double lastGoalDistance;
	private int goalTimeout;
	private int maxGoalTimeout = 100;

	/**
	 * Passed to the a* function to increase the 'cost' of waking down a tile
	 * next to a wall, used to give a more natural movement.
	 */
	private double avoidWallsModifier;
	/**
	 * Passed to the a* function to increase the 'cost' of walking over a tile
	 * randomly, it makes the path of the Pather more natural/erratic so it is
	 * less likely to take the shortest route.
	 */
	private double randomDistanceModifier;
	/**
	 * If a path node is within the radius it is a candidate for skipping
	 */
	private double pathNodeSkipRadius;

	/*
	 * // Used for debugging the local object avoidance code
	 * // stores the AvoidableObjects array so we can render pointers private
	 * ArrayList<AvoidableObject> aObjectArray;
	 * // store the vector pointer of where the Pather is trying to go
	 * private Vec2 dPosNew;
	 * // store the vector pointer of where the Pather is actually going to go
	 * private Vec2 * dPos;
	 */

	public Pather(double x, double y, int team) {
		super(x, y, team);
		yOffs = 10;
		facing = TurnSynchronizer.synchedRandom.nextInt(4);
		deathPoints = 4;

		physicsSlide = true;

		/*
		 * // Used for debugging the local object avoidance code 
		 * dPosNew = new Vec2(); dPos = new Vec2();
		 * aObjectArray = new ArrayList<AvoidableObject>();
		 */
		lastGoalDistance = 0;
		goalTimeout = 0;
		avoidWallsModifier = 0;
		randomDistanceModifier = 0;
		pathNodeSkipRadius = 0;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.mojang.mojam.entity.mob.Mob#tick()
	 */
	public void tick() {
		/*
		 * // Used for debugging the local object avoidance code
		 * ePosArray.clear(); aObjectArray.clear();
		 */

		super.tick();

		xBump *= 0.8;
		yBump *= 0.8;

		if (freezeTime > 0)
			return;

		Vec2 moveToPos = getMoveToPos();

		if (moveToPos != null) {
			tryToMove(moveToPos);
		}

	}

	/**
	 * Pather uses this to get the initial a* based target. from child classes.
	 */
	protected abstract Vec2 getPathTarget();

	/**
	 * Resets the a* path so it will be recalculated
	 */
	protected void resetPath() {
		path = null;
		goalTimeout = 0;
		pathTime = TurnSynchronizer.synchedRandom.nextInt(10) + 1;
	}

	/**
	 * checks if aStar is set if not creates it, then checks if path is set if
	 * not creates it. It then does some simple checks if the Next node is
	 * visible and within a set range so it can skip over the current Node. this
	 * leads to a more natural way of moving when combined with the local object
	 * avoidance.
	 * 
	 * @see aStart
	 * @see path
	 * @return Point to which the Pather should move to
	 */
	protected Vec2 getMoveToPos() {

		if (pathTime < 0)
			pathTime = 0;
		if (--pathTime > 0) {
			return null;
		}

		if (aStar == null)
			this.aStar = new AStar(level, this);

		if (aStar != null && path == null) {
			Tile tileFrom = level.getTile(pos);

			Vec2 pathTarget = getPathTarget();

			if (pathTarget == null) {
				pathTime = 100;
				return null;
			}

			this.path = this.aStar.getPathMods(
					new Vec2(tileFrom.x, tileFrom.y), pathTarget,
					avoidWallsModifier, randomDistanceModifier);
		}

		if (path == null) {
			pathTime = 100;
			return null;
		}

		if (path.isDone()) {
			resetPath();
			return null;
		}
		double dDistance = path.getCurrentWorldPos().dist(pos);

		if (dDistance < pathNodeSkipRadius) {
			if (level.checkLineOfSight(this, path.getCurrentWorldPos())) {
				Vec2 nextNodePos = path.getWorldPos(path.getIndex() + 1);

				if (nextNodePos != null
						&& level.checkLineOfSight(this, nextNodePos)) {
					path.next();
				}
			}
		}

		if (dDistance >= lastGoalDistance - 0.01) {
			goalTimeout += 2;
		} else {
			if (goalTimeout > 0)
				goalTimeout--;
		}
		lastGoalDistance = dDistance;
		if (goalTimeout > maxGoalTimeout) {
			resetPath();
			return null;
		}

		if (!level.checkLineOfSight(this, path.getWorldPos(path.getIndex()))) {
			path.previous();
			return path.getCurrentWorldPos();
		}

		if (path.getCurrentWorldPos().dist(pos) < Tile.WIDTH) {
			path.next();
			return path.getCurrentWorldPos();
		}

		return path.getCurrentWorldPos();
	}

	/**
	 * Look around the local area for Entities within objectAvoidanceRadius and
	 * add the details to an ArrayList of AvoidableObject
	 * 
	 * @see AvoidableObject
	 * @see objectAvoidanceRadius
	 * @return List of AvoidableObjects containing Entities that it needs to
	 *         avoid.
	 */
	protected ArrayList<AvoidableObject> getAvoidableEntities() {
		ArrayList<AvoidableObject> r = new ArrayList<AvoidableObject>();

		Set<Entity> entities = level.getEntities(pos.x - objectAvoidanceRadius,
				pos.y - objectAvoidanceRadius, pos.x + objectAvoidanceRadius,
				pos.y + objectAvoidanceRadius);

		double eDanger;

		for (Entity e : entities) {
			eDanger = 1;

			if (!(e instanceof Mob))
				continue;
			if (e == this)
				continue;

			if (e.pos.sub(pos).length() > objectAvoidanceRadius
					&& level.checkLineOfSight(this, e)) {
				continue;
			}

			r.add(new AvoidableObject(e.pos, eDanger, e, ((Mob) e).radius,
					objectAvoidanceRadius));
		}
		return r;
	}

	/**
	 * Look around the local area for Tiles within the surrounding 3x3 square
	 * and add the details to an ArrayList of AvoidableObject
	 * 
	 * @see AvoidableObject
	 * @see objectAvoidanceRadius
	 * @return List of AvoidableObjects containing Tiles that it needs to avoid.
	 */
	protected ArrayList<AvoidableObject> getAvoidableTiles() {
		ArrayList<AvoidableObject> r = new ArrayList<AvoidableObject>();

		Vec2 cTilePos = level.getTileFromPosition(pos);
		int x, y;
		Tile tile;
		double tDanger;

		for (x = -1; x <= 1; x++) {
			for (y = -1; y <= 1; y++) {
				tile = level.getTile((int) (x + cTilePos.x),
						(int) (y + cTilePos.y));
				if (tile != null && tile.canPass(this))
					continue;
				tDanger = 0.9;

				r.add(new AvoidableObject(level.getPositionFromTile(
						(int) (x + cTilePos.x), (int) (y + cTilePos.y)),
						tDanger, tile,
						new Vec2(Tile.WIDTH / 2, Tile.HEIGHT / 2), Tile.WIDTH));
			}
		}

		return r;
	}

	/**
	 * This is the local Object Avoidance, while moving towards a Node it runs
	 * Local Object Avoidance, 1. collect a list of Mobs and Tiles (walls) that
	 * need to be avoided 2. store them in a generic 'AvoidableObject' List 3.
	 * iterate though the list and based on its distance, danger, radius and
	 * current angle rotate slightly the direction the Pather is moving (think
	 * steering a car) 4. cap the maximum turn rate 5. recalculate the movement
	 * vector based on the resulting direction angle.
	 * 
	 * @param goal
	 *            the point to which the Pather is trying to move.
	 */
	protected void tryToMove(Vec2 goal) {
		Vec2 dPos = goal.sub(pos);
		Vec2 dPosNormal = dPos.normal();
		double dPosRads = Math.atan2(dPosNormal.x, dPosNormal.y);
		dPos.length();
		double dPosRadsNew = dPosRads;
		Vec2 dPosNew;
		Vec2 moveBy;
		double tSpeed = getSpeed();

		ArrayList<AvoidableObject> avoidableObjects;

		avoidableObjects = getAvoidableEntities();

		// only avoid walls when trying to avoid monsters!
		// if (avoidableObjects.size() > 0) {
		avoidableObjects.addAll(getAvoidableTiles());
		// }

		Vec2 ePos;
		double ePosRads;
		double eDistance;
		double eInverseDistance;
		double eInverseDistanceSquared;
		double eDanger;

		for (AvoidableObject o : avoidableObjects) {
			ePos = o.getPos().sub(pos);
			eDanger = o.getDanger();
			eDistance = ePos.length() - o.radius;

			ePosRads = Math.atan2(ePos.x, ePos.y);
			ePosRads -= dPosRads;
			ePosRads += Math.PI;
			ePosRads = Mth.normalizeAngle(ePosRads, 0.0);

			eInverseDistance = o.avoidDistance - eDistance;
			eInverseDistanceSquared = eInverseDistance * eInverseDistance;

			eDanger *= (eInverseDistanceSquared)
					/ (o.avoidDistance * o.avoidDistance);
			/*
			 * // Used for debugging the local object avoidance code
			 * this.ePosArray.add(ePos.normal().scale((32 * (eDanger + 1))));
			 * this.aObjectArray.add(o);
			 */

			dPosRadsNew += (eDanger * ePosRads);
		}

		double turnRate = Mth.normalizeAngle(dPosRadsNew, lastDirection)
				- lastDirection;

		if (turnRate > maxTurnRate) {
			dPosRadsNew = lastDirection + maxTurnRate;
		} else if (turnRate < -maxTurnRate) {
			dPosRadsNew = lastDirection - maxTurnRate;
		}
		lastDirection = dPosRadsNew;

		dPosNew = new Vec2(Math.sin(dPosRadsNew), Math.cos(dPosRadsNew));

		/*
		 * // Used for debugging the local object avoidance code this.dPosNew =
		 * dPosNew.normal().scale(32); this.dPos =
		 * dPos.normal().scale(dDistance);
		 */

		moveBy = dPosNew.scale(tSpeed);

		if (!move(moveBy.x, moveBy.y)) {
			resetPath();
		} else {
			facing = (int) ((Math.atan2(-moveBy.x, moveBy.y) * 8
					/ (Math.PI * 2) - 8.5)) & 7;

			stepTime += getSpeed() / 4;
			if (stepTime > 6)
				stepTime = 0;
		}

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.mojang.mojam.entity.mob.Mob#getDeatchSound()
	 */
	@Override
	public String getDeathSound() {
		return "/sound/Enemy Death 2.wav";
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.mojang.mojam.entity.mob.Mob#die()
	 */
	public void die() {
		super.die();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.mojang.mojam.entity.mob.Mob#getSprite()
	 */
	public abstract Bitmap getSprite();

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.mojang.mojam.entity.mob.HostileMob#collide(com.mojang.mojam.entity.Entity,
	 *      double, double)
	 */
	@Override
	public void collide(Entity entity, double xa, double ya) {
		super.collide(entity, xa, ya);

		if (TurnSynchronizer.synchedRandom.nextInt(10) > 5)
			resetPath();
	}

	public double getAvoidWallsModifier() {
		return avoidWallsModifier;
	}

	public void setAvoidWallsModifier(double avoidWallsModifier) {
		this.avoidWallsModifier = avoidWallsModifier;
	}

	public double getRandomDistanceModifier() {
		return randomDistanceModifier;
	}

	public void setRandomDistanceModifier(double randomDistanceModifier) {
		this.randomDistanceModifier = randomDistanceModifier;
	}

	public double getPathNodeSkipRadius() {
		return pathNodeSkipRadius;
	}

	public void setPathNodeSkipRadius(double pathNodeSkipRadius) {
		this.pathNodeSkipRadius = pathNodeSkipRadius;
	}
}
