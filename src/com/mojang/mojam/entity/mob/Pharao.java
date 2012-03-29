package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;

public class Pharao extends HostileMob {

    private int tick = 0;
    public static double ATTACK_RADIUS = 5 * Tile.WIDTH;

	public static final int COLOR = 0xffffdd00;
	
    public Pharao(double x, double y) {
        super(x, y, Team.Neutral);
        setPos(x, y);
        dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
        yOffs = 10;
        facing = TurnSynchronizer.synchedRandom.nextInt(4);
        REGEN_INTERVAL = 15;
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

    public AbstractBitmap getSprite() {
        return Art.pharao[((stepTime / 6) & 3)][(facing + 1) & 3];
    }

    @Override
    public String getDeathSound() {
        return "/sound/pharao_dies.wav";
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
		return "PHARAO";
	}

	@Override
	public AbstractBitmap getBitMapForEditor() {
		return Art.pharao[0][0];
	}
}