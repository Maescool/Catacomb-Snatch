package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.level.DifficultyInformation;

public class SpikeTrap extends Mob {
    public static final int COLOR = 0x0000ff;
	private int spike = 0;
    private boolean spikeGoUp = true;

    public SpikeTrap(double x, double y) {
	super(x, y, Team.Neutral);
	setStartHealth(20);
	isImmortal = true;
	this.isBlocking = false;
    }

    public void tick() {
	super.tick();
	if (freezeTime > 0)
	    return;

	if (spikeGoUp) {
	    if (spike == 0) {
		spike = 3;
	    } else {
		if (spike == 2) {
		    spikeGoUp = false;
		}
		spike--;
	    }
	} else {
	    if (spike == 3) {
		spike = 0;
		spikeGoUp = true;
	    } else {
		spike++;
	    }
	}
	if (spike > 0 && spike < 3) {
	    this.isBlocking = true;
	}else{
	    this.isBlocking = false;
	}
	freezeTime = 30;
	return;
    }
    
    public void render(Screen screen) {
	screen.blit(Art.spikes[spike][0], pos.x, pos.y);
    }
    
    @Override
    public void collide(Entity entity, double xa, double ya) {
	if (isSpiking()){
	    if (entity instanceof Player){
		((Player) entity).dropAllMoney();
		((Player) entity).hurt(this, DifficultyInformation.calculateStrength(1));
	    }
	}
    }

    @Override
    public Bitmap getSprite() {
	return Art.floorTiles[4][2];
    }

    public boolean isSpiking() {
	if (spike > 0 && spike < 3) {
	    return true;
	}
	return false;
    }

    public boolean isBuildable() {
	return false;
    }

    public boolean isHighlightable() {
	return false;
    }

}
