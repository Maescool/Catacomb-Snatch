package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class TurretTeamTwo extends Turret {

	public static final int COLOR = 0xff990033;

	public TurretTeamTwo(double x, double y) {
		super(x, y, Team.Team2);
	}

	@Override
	public int getColor() {
		return TurretTeamTwo.COLOR;
	}

	@Override
	public String getName() {
		return "TURR.T2";
	}

	@Override
	public int getMiniMapColor() {
		return TurretTeamTwo.COLOR;
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.turret[0][0];
	}
	
	
}
