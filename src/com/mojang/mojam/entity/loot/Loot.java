package com.mojang.mojam.entity.loot;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.*;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;
import com.mojang.mojam.level.HoleTile;

public class Loot extends Entity {
	public double xa, ya, za;
	public double z;
	public Entity owner;
	public int life;
	public int takeTime = 0;
	public int animTime = 0;
	private int value = 0;
	public boolean fake = false;
	private boolean isTakeable;

	public static Bitmap[][][] anims = { Art.pickupCoinBronzeSmall,
			Art.pickupCoinSilverSmall, Art.pickupCoinGoldSmall,
			Art.pickupCoinBronze, Art.pickupCoinSilver, Art.pickupCoinGold,
			Art.pickupGemEmerald, Art.pickupGemRuby, Art.pickupGemDiamond, };

	public static int[] values = { 1, 2, 5, 10, 20, 50, 100, 200, 500 };

	public Loot(double x, double y, double xa, double ya, int val) {
		pos.set(x, y);
		isTakeable = true;

		value = 0;
		while (value < 8 && values[value] < val)
			value++;

		if (TurnSynchronizer.synchedRandom.nextInt(3) == 0)
			value++;
		if (TurnSynchronizer.synchedRandom.nextInt(3) == 0)
			value++;
		if (value > 8)
			value = 8;

		// value = TurnSynchronizer.synchedRandom.nextInt(9);
		double pow = TurnSynchronizer.synchedRandom.nextDouble() * 1 + 1;
		this.xa = xa * pow;
		this.ya = ya * pow;
		this.za = TurnSynchronizer.synchedRandom.nextDouble() * 2 + 1.0;
		this.setSize(2, 2);
		physicsSlide = false;
		life = TurnSynchronizer.synchedRandom.nextInt(100) + 600;

		animTime = TurnSynchronizer.synchedRandom
				.nextInt(anims[value].length * 3);

	}

	public void makeUntakeable() {
		isTakeable = false;
		life = 100 - TurnSynchronizer.synchedRandom.nextInt(40);
	}

	public void tick() {
		if(level.getTile(pos) instanceof HoleTile)
		{
			remove();
		}
		animTime++;
		if (takeTime > 0) {
			takeTime--;
			if (takeTime == 0) {
				remove();
			}
			z += za;
			za += 0.25;
			return;
		}
		move(xa, ya);
		z += za;
		if (z < 0) {
			z = 0;
			xa *= 0.8;
			ya *= 0.8;
		} else {
			xa *= 0.98;
			ya *= 0.98;

		}
		za -= 0.2;
		if (--life < 0)
			remove();

		if (isTakeable) {
			double dist = 100;
			for (Entity e : level.getEntities(getBB().grow(dist))) {
				if (!(e instanceof LootCollector))
					continue;
				LootCollector p = (LootCollector) e;
				if (!p.canTake()) {
					double xd = e.pos.x - pos.x;
					double yd = e.pos.y - pos.y;
					double localDist = 80;
					if (xd * xd + yd * yd < localDist * localDist) {
						double dd = Math.sqrt(xd * xd + yd * yd);
						if (dd < 16) {
							onTake(p);
							return;
						}
						xd /= dd;
						yd /= dd;
						double pow = (1 - (dd / localDist)) * 0.1;
						if (z <= 0) {
							this.xa -= xd * pow;
							this.ya -= yd * pow;
						}
					}
				} else {
					double suckPow = p.getSuckPower();
					double xd = e.pos.x - pos.x;
					double yd = e.pos.y - pos.y;
					double localDist = (dist - 40) * suckPow + 40;
					if (xd * xd + yd * yd < localDist * localDist) {
						p.notifySucking();
						double dd = Math.sqrt(xd * xd + yd * yd);
						if (dd < 16) {
							onTake(p);
							return;
						}
						xd /= dd;
						yd /= dd;
						double pow = (1 - (dd / localDist)) * 1.6
								* (suckPow * 0.5 + 0.5);
						if (z <= 0) {
							this.xa += xd * pow;
							this.ya += yd * pow;
						}
					}
				}
			}
		}
	}

	public void forceTake(LootCollector taker) {
		onTake(taker);
	}

	protected void onTake(LootCollector taker) {
		remove();
		taker.flash();
		taker.take(this);

		if (value > 8) {
			MojamComponent.soundPlayer.playSound("/sound/Big Gem.wav",
					(float) pos.x, (float) pos.y);
		} else if (value > 6) {
			MojamComponent.soundPlayer.playSound("/sound/Gem.wav",
					(float) pos.x, (float) pos.y);
		} else if (value > 4) {
			MojamComponent.soundPlayer.playSound("/sound/Big Coin.wav",
					(float) pos.x, (float) pos.y);
		} else {
			MojamComponent.soundPlayer.playSound("/sound/Small Coin.wav",
					(float) pos.x, (float) pos.y);
		}
	}

	protected boolean shouldBlock(Entity e) {
		return false;
	}

	@Override
	public void handleCollision(Entity entity, double xa, double ya) {
		if (isTakeable && entity instanceof Player) {
			((Player) entity).take(this);
		}
	}

	public void render(Screen screen) {
		Bitmap[][] bm = anims[value];
		if (life > 60 * 3 || life / 2 % 2 == 0) {
			int frame = animTime / 3 % bm.length;
			Bitmap bmp = bm[frame][0];
			if (z > 0) {
				screen.blit(Art.shadow, pos.x - 2, pos.y);
			}
			screen.blit(bmp, pos.x - bmp.w / 2, pos.y - bmp.h / 2 - 2 - z);
		}
	}

	public int getScoreValue() {
		return fake ? 0 : values[value];
	}
}