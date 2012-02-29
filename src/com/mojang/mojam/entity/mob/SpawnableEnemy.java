package com.mojang.mojam.entity.mob;


/**
 * @author andy36
 * 
 * This enum is to make the addition and organization of Spawnable Enemies easier than tracking int values
 * @param type - this is the integer for the enemy type
 * @param clazz - the actual class of the enemy object. This is used for reflection.
 *
 */
public enum SpawnableEnemy {
	BAT(0, Bat.class),
	SNAKE(1, Snake.class),
	MUMMY(2, Mummy.class),
	SCARAB(3, Scarab.class);
	 
	private int type;
	private Class<? extends HostileMob> clazz;

	private SpawnableEnemy(int type, Class<? extends HostileMob> clazz){
		this.type = type;
		this.clazz = clazz;
	}

	public int getType() {
		return type;
	}

	public Class<? extends HostileMob> getClazz() {
		return clazz;
	}
	
	/*
	 * Look up an enum by the type int
	 */
	public static SpawnableEnemy getByType(int type){
		SpawnableEnemy found = null;
		for(SpawnableEnemy curEnemy : SpawnableEnemy.values()){
			if(curEnemy.getType() == type){
				found = curEnemy;
			}
		}
		return found;
	}
	
}
