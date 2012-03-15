package com.mojang.mojam.entity.mob.pather;

import com.mojang.mojam.math.Vec2;

/**
 * Used a generic store for Avoidance data used by Pather
 * so we can iterate though a generic list of Entities and Tiles 
 * and maybe later Projectiles.
 * 
 * @author Morgan Gilroy
 * @see Pather
 */
public class AvoidableObject {
	Vec2 pos;
	double danger;
	double radius;
	double avoidDistance;
	Vec2 bounds;
	/*
	 * generic pointer to the Object that this Avoidance data is related to just in case
	 * other parts of the code need to gather further information.
	 * 
	 */
	Object object;

	public AvoidableObject(Vec2 pos, double danger, Object object, double radius, double avoidDistance) {
		this.pos = pos;
		this.danger = danger;
		this.object = object;
		this.radius = radius;
		this.avoidDistance = avoidDistance;
	}

	public AvoidableObject(Vec2 pos, double danger, Object object, Vec2 bounds, double avoidDistance) {
		this.pos = pos;
		this.danger = danger;
		this.object = object;
		this.radius = bounds.length();
		this.bounds = bounds;
		this.avoidDistance = avoidDistance;
	}

	public Vec2 getPos() {
		return pos;
	}

	public void setPos(Vec2 pos) {
		this.pos = pos;
	}

	public double getDanger() {
		return danger;
	}

	public void setDanger(double danger) {
		this.danger = danger;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Vec2 getBounds() {
		return bounds;
	}

	public void setBounds(Vec2 bounds) {
		this.radius = bounds.length();
		this.bounds = bounds;
	}

}
