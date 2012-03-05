package com.mojang.mojam.entity.mob;

import java.lang.reflect.Constructor;

import com.mojang.mojam.network.TurnSynchronizer;


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
	
	public static Mob spawnRandomHostileMob(int x, int y) {
		Mob te = null;
		try {
			SpawnableEnemy randomEnemyClass = SpawnableEnemy.getByType(TurnSynchronizer.synchedRandom.nextInt(SpawnableEnemy.values().length))  ;
			
			//This whole try/catch block is to create the new enemy of the given type using reflection. This will allow new enemies to be added without touching this 
			//code as long as they are defined in SpawnableEnemies enum and have a public constructor that takes (double, double)
			
			Constructor<? extends HostileMob> con = randomEnemyClass.getClazz().getConstructor(new Class[]{double.class, double.class});
			te = con.newInstance(new Object[]{x,y});
		
		} catch (Exception e) {
			//Something went wrong with the reflection creation of the Enemy. Here are a few things that could've gone wrong:
			//The constructor with arguments double, double is not defined as Public
			//There is no constructor with arguments double, double
			e.printStackTrace();
		} 		
		return te;
	}
	
}
