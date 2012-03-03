package com.mojang.mojam;

import com.mojang.mojam.entity.Entity;

public class EmptyEntity extends Entity
{
	Object[] vars;
	
	public EmptyEntity(double x, double y, Object... o)
	{
		super();
		setPos(x,y);
		vars = o;
	}
}
