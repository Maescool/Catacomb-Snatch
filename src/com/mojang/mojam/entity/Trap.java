package com.mojang.mojam.entity;

import com.mojang.mojam.entity.animation.EnemyDieAnimation;
import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class Trap extends Bullet {

	
	public Trap(Mob e, double xa, double ya) {
		super(e, xa, ya);
		life=1;
		damage = 10;
		this.setSize(10, 10);
		isGroundOnly=true;
		
	}


	@Override
	public void tick() {
		if(life > 0) {
			move(xa, ya);
			life = 0;
		}
		
        if (hit && !removed) {
            remove();
        }
	}
	
	@Override
	public void remove() {
		super.remove();
		 level.addEntity(new EnemyDieAnimation(pos.x, pos.y));
	}
		
	 @Override
	protected boolean shouldBlock(Entity e) {
		 
		 if ((e instanceof Bat)) return false;
		 
		return super.shouldBlock(e);
	}
	    
	@Override
	public void render(Screen screen) {
		  screen.blit(Art.trap, pos.x - 8, pos.y - 10);
	}
}
