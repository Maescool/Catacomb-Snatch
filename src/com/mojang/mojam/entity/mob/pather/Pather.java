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
 * @author Morgan
 * 
 */
public abstract class Pather extends HostileMob {
	public int facing;
	public int walkTime;
	public double stepTime;

	private AStar aStar;
	private Path path;

	private int pathTime;

	private double objectAvoidanceRadius = Tile.HEIGHT * 3;
	private double maxTurnRate = Math.PI / 10;
	private double lastDirection = 0;

	// private ArrayList<AvoidableObject> aObjectArray;

	// private Vec2 dPosNew;
	// private Vec2 dPos;

	private double lastGoalDistance;
	private int goalTimeout;
	private int maxGoalTimeout = 100;

	private double avoidWallsModifier;
	private double randomDistanceModifier;
	private double pathNodeSkipRadius;
	

	/**
	 * @param x
	 * @param y
	 */

	public Pather(double x, double y, int team) {
		super(x, y, team);
		yOffs = 10;
		facing = TurnSynchronizer.synchedRandom.nextInt(4);
		deathPoints = 4;

		physicsSlide = true;

		// dPosNew = new Vec2();
		// dPos = new Vec2();
		// ePosArray = new ArrayList<Vec2>();
		// aObjectArray = new ArrayList<AvoidableObject>();

		lastGoalDistance = 0;
		goalTimeout = 0;
		avoidWallsModifier=0;
		randomDistanceModifier=0;
		pathNodeSkipRadius=0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mojang.mojam.entity.mob.Mob#tick()
	 */

	public void tick() {

		// ePosArray.clear();
		// aObjectArray.clear();

		super.tick();
		xBump*=0.8;
		yBump*=0.8;
		if (freezeTime > 0)
			return;


		Vec2 moveToPos = getMoveToPos();

		if (moveToPos != null) {
			tryToMove(moveToPos);
		}

	}

	protected abstract Vec2 getPathTarget();

	/**
	 * 
	 */
	protected void resetPath() {
		path = null;
		goalTimeout = 0;
		pathTime = TurnSynchronizer.synchedRandom.nextInt(10) + 1;
	}

	/**
	 * @return
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
			
			Vec2 pathTarget=getPathTarget();
			
			if (pathTarget == null) {
				pathTime = 100;
				return null;
			}
			
			this.path = this.aStar.getPathMods(
					new Vec2(tileFrom.x, tileFrom.y), pathTarget, avoidWallsModifier, randomDistanceModifier);
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
		// System.out.println("dDistance: " + dDistance + " lastGoalDistance: "
		// + lastGoalDistance + " goalTimeout: " + goalTimeout);

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

			r.add(new AvoidableObject(e.pos, eDanger, e, ((Mob) e).radius));
		}
		return r;
	}

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
				if (tile.canPass(this))
					continue;
				tDanger = 0.9;

				r.add(new AvoidableObject(level.getPositionFromTile(
						(int) (x + cTilePos.x), (int) (y + cTilePos.y)),
						tDanger, tile, new Vec2(Tile.WIDTH, Tile.HEIGHT)));
			}
		}

		return r;
	}

	/**
	 * @param goal
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
			eDistance = ePos.length();

			if (eDistance > objectAvoidanceRadius) {
				continue;
			}

			ePosRads = Math.atan2(ePos.x, ePos.y);
			ePosRads -= dPosRads;
			ePosRads += Math.PI;
			ePosRads = Mth.normalizeAngle(ePosRads, 0.0);

			eInverseDistance = objectAvoidanceRadius - eDistance;
			eInverseDistanceSquared = eInverseDistance * eInverseDistance;

			eDanger *= (eInverseDistanceSquared)
					/ (objectAvoidanceRadius * objectAvoidanceRadius);
			// this.ePosArray.add(ePos.normal().scale((32 * (eDanger + 1))));
			// this.aObjectArray.add(o);
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

		// this.dPosNew = dPosNew.normal().scale(32);
		// this.dPos = dPos.normal().scale(dDistance);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mojang.mojam.entity.mob.Mob#getDeatchSound()
	 */
	@Override
	public String getDeathSound() {
		return "/sound/Enemy Death 2.wav";
	}

	public void die() {
		super.die();
	}

	public abstract Bitmap getSprite();

	@Override
	public void collide(Entity entity, double xa, double ya) {
		super.collide(entity, xa, ya);

		if (entity instanceof Mob) {
			Mob mob = (Mob) entity;
			if (isNotFriendOf(mob)) {
				mob.hurt(this, 2);
			}
		}
		freezeTime = TurnSynchronizer.synchedRandom.nextInt(5) + 5;
		xBump = xa;
		yBump = ya;

		if (TurnSynchronizer.synchedRandom.nextInt(10) > 5)
			resetPath();
	}

/*	public void render(Screen screen) {
		super.render(screen);
		
		 * for (AvoidableObject aO : aObjectArray) { screen.line((int) pos.x,
		 * (int) pos.y, (int) (aO.getPos().x), (int) (aO.getPos().y),
		 * 0x80FF0000); } screen.line((int) pos.x, (int) pos.y, (int) (dPosNew.x
		 * + pos.x), (int) (dPosNew.y + pos.y), 0x8000FF00); screen.line((int)
		 * pos.x, (int) pos.y, (int) (dPos.x + pos.x), (int) (dPos.y + pos.y),
		 * 0x800000FF);
		 
		// render the path ahead of the pather...

		if (path != null) {

			int pathOriginalIndex = path.getIndex();
			int pathMaxIndex = path.size();
			Vec2 previousNodePos = pos.clone();
			Vec2 thisNodePos = new Vec2();
			int i;

			for (i = pathOriginalIndex; i < (pathOriginalIndex + 5)
					&& (i < pathMaxIndex); i++) {
				thisNodePos = path.getWorldPos(i);
				screen.line((int) previousNodePos.x, (int) previousNodePos.y,
						(int) (thisNodePos.x), (int) (thisNodePos.y),
						0x800000FF);
				previousNodePos = thisNodePos.clone();
			}
		}
		
	}*/


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
