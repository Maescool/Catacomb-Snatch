package com.mojang.mojam.entity.mob;

import java.util.Set;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.tile.Tile;


public abstract class HostileMob extends Mob {

	public HostileMob(double x, double y, int team) {
		super(x, y, team);
	}

	@Override
	public void setStartHealth(float newHealth) {
		super.setStartHealth(DifficultyInformation.calculateHealth(newHealth));
	}
	
	public int FaceEntity(double x, double y, double radius, Class<? extends Entity> c, int facing){
        Set<Entity> entities = level.getEntities(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius, c);
        Entity closest = null;
        double closestDist = 99999999.0f;
        for (Entity e : entities) {
            final double dist = e.pos.distSqr(pos);
            if (dist < closestDist) {
                closestDist = dist;
                closest = e;
            }
        }
        if (closest != null && !isTargetBehindWall(closest.pos.x, closest.pos.y, closest)) {
            chasing=true;
            double angle = Math.atan2((closest.pos.y - pos.y), (closest.pos.x - pos.x));
            facing = (int) Math.abs(2*(angle+(3*Math.PI/4))/Math.PI) % 4; 
        } else {
        	chasing=false;
        }
        return facing;
	}
	public void tick() {	
		super.tick();
		Tile thisTile = level.getTile((int)pos.x/Tile.WIDTH, (int)pos.y/Tile.HEIGHT);
		if (!thisTile.canPass(this)){
			remove();
		}
	}
	
	public void collide(Entity entity, double xa, double ya) {
		if (entity instanceof Mob) {
			Mob mob = (Mob) entity;
			if (isNotFriendOf(mob)) {
				mob.hurt(this, DifficultyInformation.calculateStrength(strength));
			}
		}
	}

}