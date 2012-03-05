package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class Mummy extends HostileMob {

    private int tick = 0;
    public static double ATTACK_RADIUS = 128.0;

	public static final int COLOR = 0xffffCC00;
	
    public Mummy(double x, double y) {
        super(x, y, Team.Neutral);
        setPos(x, y);
        setStartHealth(7);
        dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
        minimapColor = 0xffff0000;
        yOffs = 10;
        facing = TurnSynchronizer.synchedRandom.nextInt(4);
        deathPoints = 4;
        strength = 2;
        speed = 0.5;
        limp = 3;
    }

    public void tick() {
        super.tick();
        if (freezeTime > 0) {
            return;
        }
        tick++;
        if (tick >= 20) {
            tick = 0;
            facing = faceEntity(pos.x, pos.y, ATTACK_RADIUS, Player.class, facing);
        }
        walk();
    }

    public void die() {
        super.die();
    }

    public Bitmap getSprite() {
        return Art.mummy[((stepTime / 6) & 3)][(facing + 1) & 3];
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
		return "MUMMY";
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.mummy[0][0];
	}
}