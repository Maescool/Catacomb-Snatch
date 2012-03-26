package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.weapon.VenomShooter;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;

public class Snake extends HostileMob {

	private int tick = 0;
	public static final int COLOR = 0xffff9900;
	public static double ATTACK_RADIUS = 128.0;
	
	public Snake(double x, double y) {
		super(x, y, Team.Neutral);
		setPos(x, y);
		dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
		minimapColor = 0xffff0000;
		yOffs = 10;
		facing = TurnSynchronizer.synchedRandom.nextInt(4);
		weapon = new VenomShooter(this);
	}

	public void tick() {
        super.tick();
        if (freezeTime > 0) {
            return;
        }
        tick++;
        if (tick >= 20) {
            tick = 0;

	        if(TurnSynchronizer.synchedRandom.nextInt(5) == 0 && checkIfInFront(ATTACK_RADIUS, Player.class) != null) {
	            aimVector.set(xd, yd);
	            aimVector.normalizeSelf();
	            weapon.primaryFire(xd, yd);
	        }
        }
        walk();
	}

	public void die() {
		super.die();
	}

	public AbstractBitmap getSprite() {
		return Art.snake[((stepTime / 6) & 3)][(facing + 1) & 3];
	}

	@Override
	public String getDeathSound() {
		return "/sound/Enemy Death 2.wav";
	}

	@Override
	public int getColor() {
		return COLOR;
	}

	@Override
	public int getMiniMapColor() {
		return COLOR;
	}

	@Override
	public String getName() {
		return "SNAKE";
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.snake[0][0];
	}
}
