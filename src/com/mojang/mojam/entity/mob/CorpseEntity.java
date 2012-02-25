package com.mojang.mojam.entity.mob;

import com.mojang.mojam.screen.*;

public class CorpseEntity extends Mob {
	public CorpseEntity(double x, double y,int localTeam) {
		super(x, y, Team.Neutral,localTeam);
		setStartHealth(2);
		freezeTime = 10;
	}

	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;

		xd *= 0.2;
		yd *= 0.2;
	}

	public Bitmap getSprite() {
		return Art.floorTiles[3][1];
	}
}
