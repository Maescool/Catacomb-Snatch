package com.mojang.mojam.mod;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.mob.Mummy;
import com.mojang.mojam.entity.mob.Pharao;
import com.mojang.mojam.entity.mob.Scarab;
import com.mojang.mojam.entity.mob.Snake;

public class SnatchContent extends Mod {
    int[] ids = new int[5];

    public SnatchContent() {
	ids[0] = ModSystem.addEntity(Bat.class);
	ids[1] = ModSystem.addEntity(Snake.class);
	ids[2] = ModSystem.addEntity(Mummy.class);
	ids[3] = ModSystem.addEntity(Scarab.class);
	ids[4] = ModSystem.addEntity(Pharao.class);
    }

    @Override
    public Entity getEntityInstanceById(int type, double x, double y) {
	Mob te = null;
	if (type == ids[0])
	    te = new Bat(x, y);
	if (type == ids[1])
	    te = new Snake(x, y);
	if (type == ids[2])
	    te = new Mummy(x, y);
	if (type == ids[3])
	    te = new Scarab(x, y);
	if (type == ids[4])
	    te = new Mummy(x, y);// Prevent Pharaoh Spawners
	return te;
    }

    @Override
    public String getVersion() {
	return "";
    }

}
