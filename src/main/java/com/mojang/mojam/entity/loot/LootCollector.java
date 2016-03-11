package com.mojang.mojam.entity.loot;

public interface LootCollector {
	public boolean canTake();

	public void take(Loot loot);

	public double getSuckPower();

	public void notifySucking();

	public int getScore();

	public void flash();
}
