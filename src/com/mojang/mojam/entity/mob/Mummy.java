package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Mummy extends HostileMob {

    private int tick = 0;
    public static double ATTACK_RADIUS = 128.0;

    private int spriteIdx;
    private int facingIdx;
    private Bitmap shadow;
    
    public Mummy(double x, double y) {
        super(x, y, Team.Neutral);
        setPos(x, y);
        setStartHealth(7);
        dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
        minimapColor = 0xffff0000;
        yOffs = 10;
        facing = TurnSynchronizer.synchedRandom.nextInt(4);
        deathPoints = 4;
        strength = 2;
        speed = 0.5;
        limp = 3;
    }

    public void tick() {
        super.tick();
        if (freezeTime > 0) {
            return;
        }
        tick++;
        if (tick >= 20) {
            tick = 0;
            facing = FaceEntity(pos.x, pos.y, ATTACK_RADIUS, Player.class, facing);
        }
        walk();
    }

    @Override
    public void die() {
        super.die();
    }

    @Override
    public Bitmap getSprite() {
        spriteIdx = ((stepTime / 6) & 3);
        facingIdx = (facing + 1) & 3;
        return Art.mummy[spriteIdx][facingIdx];
    }

    @Override
    public String getDeathSound() {
        return "/sound/Enemy Death 2.wav";
    }
    
    @Override
	public void render(Screen screen) {
		Bitmap image = getSprite();
        
        // Render shadow
        shadow = Art.mummyShadow[spriteIdx][facingIdx];
        screen.opacityBlit(shadow, (int)(pos.x - shadow.w / 2), (int)(pos.y - shadow.h / 2 - 10), 0x99);
        
		if (hurtTime > 0) {
			if (hurtTime > 40 - 6 && hurtTime / 2 % 2 == 0) {
				screen.colorBlit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs, 0xa0ffffff);
			} else {
				if (health < 0)
					health = 0;
				int col = (int) (180 - health * 180 / maxHealth);
				if (hurtTime < 10)
					col = col * hurtTime / 10;
				screen.colorBlit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs, (col << 24) + 255 * 65536);
			}
		} else {
			screen.blit(image, pos.x - image.w / 2, pos.y - image.h / 2 - yOffs);
		}

		if (doShowHealthBar && health < maxHealth) {
            addHealthBar(screen);
        }
	}    
}