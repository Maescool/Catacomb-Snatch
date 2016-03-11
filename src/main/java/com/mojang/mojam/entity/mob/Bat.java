package com.mojang.mojam.entity.mob;

import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

public class Bat extends HostileMob  {
	public static final int COLOR = 0xffff6600;
	private int tick = 0;

	public Bat(double x, double y) {
		super(x, y, Team.Neutral);
		setPos(x, y);
		dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
		minimapColor = 0xffff0000;
		yOffs = 5;
	}
	public void tick() {
		super.tick();
		if (freezeTime > 0)
			return;

		tick++;

		dir += (TurnSynchronizer.synchedRandom.nextDouble() - TurnSynchronizer.synchedRandom
				.nextDouble()) * 0.2;
		xd += Math.cos(dir) * 1;
		yd += Math.sin(dir) * 1;
		
		if (shouldBounceOffWall(xd, yd)){
			xd = -xd;
			yd = -yd;
		}
		
		if (!move(xd, yd)) {
			dir += (TurnSynchronizer.synchedRandom.nextDouble() - TurnSynchronizer.synchedRandom
					.nextDouble()) * 0.8;
		}
		xd *= 0.2;
		yd *= 0.2;
	}

	public void die() {
		super.die();
	}

	public AbstractBitmap getSprite() {
		return Art.bat[(tick / 3) & 3][0];
	}

	@Override
	public void render(AbstractScreen screen) {
		screen.alphaBlit(Art.batShadow, (int)(pos.x - Art.batShadow.getWidth() / 2), (int)(pos.y - Art.batShadow.getHeight() / 2 - yOffs + 16), 0x45);
		super.render(screen);
	}

	@Override
	public int getColor() {
		return Bat.COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return Bat.COLOR;
	}

	@Override
	public String getName() {
		return "BAT";
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.bat[0][0];
	}
}
