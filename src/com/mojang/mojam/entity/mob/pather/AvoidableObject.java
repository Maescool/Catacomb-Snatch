package com.mojang.mojam.entity.mob.pather;

import com.mojang.mojam.math.Vec2;

public class AvoidableObject {
	Vec2 pos;
	double danger;
	double radius;
	double avoidDistance;
	Vec2 bounds;

	Object object;

	public AvoidableObject(Vec2 pos, double danger, Object object, double radius, double avoidDistance) {
		this.pos = pos;
		this.danger = danger;
		this.object = object;
		this.radius = radius;
		this.avoidDistance = avoidDistance;
	}

	public AvoidableObject(Vec2 pos, double danger, Object object, Vec2 bounds) {
		this.pos = pos;
		this.danger = danger;
		this.object = object;
		this.radius = bounds.length();
		this.bounds = bounds;
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
