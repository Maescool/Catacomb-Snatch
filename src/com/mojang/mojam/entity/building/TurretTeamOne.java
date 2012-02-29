package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class TurretTeamOne extends Turret {

	public static final int COLOR = 0xff990099;
	
	public TurretTeamOne(double x, double y) {
		super(x, y, Team.Team1);
	}
	
	@Override
	public int getColor() {
		return TurretTeamOne.COLOR;
	}
	
	@Override
	public String getName() {
		return "TURR.T1";
	}

	@Override
	public int getMiniMapColor() {
		return TurretTeamOne.COLOR;
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.turret[0][0];
	}
	
}
