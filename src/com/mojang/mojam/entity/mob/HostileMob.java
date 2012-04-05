package com.mojang.mojam.entity.mob;

import java.util.Set;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.IEditable;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.resources.Constants;


public abstract class HostileMob extends Mob  implements IEditable {

	public HostileMob(double x, double y, int team) {
		super(x, y, team);
		deathPoints = Constants.getInt("deathPoints", this);
		strength = Constants.getInt("strength", this);
		speed = Constants.getDouble("speed", this);
		limp = Constants.getInt("limp", this);
		setStartHealth(Constants.getFloat("health", this));
	}

	@Override
	public void setStartHealth(float newHealth) {
		super.setStartHealth(TitleMenu.difficulty.calculateHealth(newHealth));
	}
	
	public int faceEntity(double x, double y, double radius, Class<? extends Entity> c, int facing){
        Entity closest = checkIfNear(radius, c);
        if (closest !=null) {
            chasing=true;
            double angle = Math.atan2((closest.pos.y - pos.y), (closest.pos.x - pos.x));
            facing = (int) Math.abs(2*(angle+(3*Math.PI/4))/Math.PI) % 4; 
        } else {
        	chasing=false;
        }
        return facing;
	}

	public Entity checkIfInFront(double radius, Class<? extends Entity> c) {
		double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
		
		switch (facing) {
        case 0:
        	x0 = pos.x - (radius/2);
        	x1 = pos.x + (radius/2);
        	y0 = pos.y - radius;
        	y1 = pos.y;
            break;
        case 1:
            x0 = pos.x;
            x1 =  pos.x + radius;
            y0 = pos.y - (radius/2);
        	y1 = pos.y + (radius/2);
            break;
        case 2:
        	x0 = pos.x - (radius/2);
         	x1 = pos.x + (radius/2);
            y0 = pos.y;
            y1 = pos.y + radius;
            break;
        case 3:
        	x0 = pos.x - radius;
            x1 = pos.x;
            y0 = pos.y - (radius/2);
        	y1 = pos.y + (radius/2);
            break;
    	}
		
		
		Set<Entity> entities = level.getEntities(x0,y0,x1,y1,c);
        Entity closest = null;
        double closestDist = 99999999.0f;
        for (Entity e : entities) {
            final double dist = e.pos.distSqr(pos);
            if (dist < closestDist) {
                closestDist = dist;
                closest = e;
            }
        }
        
        if(closest != null && !isTargetBehindWall(closest.pos.x, closest.pos.y, closest))
        	return closest;
        else 
        	return null;
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
				mob.hurt(this, TitleMenu.difficulty.calculateStrength(strength));
			}
		}
	}

	//Look for the closest Entity optionally check if its an enemy Mob to maintain comparability with checkIfNear
	public Entity checkIfEnemyNear(double radius, Class<? extends Entity> c, boolean isEnemyCheck) {
		Set<Entity> entities = level.getEntities(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius, c);
	    Entity closest = null;
	    double closestDist = 99999999.0f;
	    for (Entity e : entities) {
	        final double dist = e.pos.distSqr(pos);
	        if (dist < closestDist && !isTargetBehindWall(e.pos.x, e.pos.y, e)) {
	        	if (isEnemyCheck) {
	        		if (!(e instanceof Mob))
	        			continue;
	        		if (!((Mob)e).isNotFriendOf(this))
	        			continue;
	        	}
	            closestDist = dist;
	            closest = e;
	        }
	    }
	return closest;
	}

	public Entity checkIfNear(double radius, Class<? extends Entity> c) {
		return checkIfEnemyNear( radius, c, false);
	}

}
