package com.mojang.mojam.math;

import java.util.Random;

import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.network.TurnSynchronizer;

public class RandomPos {
	public static Vec2 getPos(Mob mob, double radius) {
		return generateRandomPos(mob, radius, null);
	}

	public static Vec2 getPosTowards(Mob mob, double radius, Vec2 towardsPos) {
		return generateRandomPos(mob, radius, towardsPos.sub(mob.pos));
	}

	public static Vec2 getPosAvoid(Mob mob, double radius, Vec2 avoidPos) {
		return generateRandomPos(mob, radius, mob.pos.sub(avoidPos));
	}

	private static Vec2 generateRandomPos(Mob mob, double radius, Vec2 dir) {
		// boolean hasBest = false;
		// int xBest = 0, yBest = 0, zBest = 0;
		// double best = -99999;

		Random random = TurnSynchronizer.synchedRandom;

		Vec2 pos = new Vec2();
		for (int i = 0; i < 10; i++) {
			// in a square for now...
			pos.set(random.nextDouble() * 2 * radius - radius,
					random.nextDouble() * 2 * radius - radius);
			if (dir != null && pos.dot(dir) < 0)
				continue;
			pos.addSelf(mob.pos);
			Tile tile = mob.level.getTile(mob.pos);
			if (tile == null || !tile.canPass(mob))
				continue;
			return pos;
		}
		return null;
	}
}
