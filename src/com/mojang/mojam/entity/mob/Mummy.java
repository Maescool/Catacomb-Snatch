package com.mojang.mojam.entity.mob;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import java.util.Set;

public class Mummy extends HostileMob {

    public int facing;
    public int walkTime;
    public int stepTime;
    double speed = 0.5;
    private int tick = 0;
    public static double ATTACK_RADIUS = 128.0;
    public boolean chasing = false;

    public Mummy(double x, double y, int localTeam) {
        super(x, y, Team.Neutral,localTeam);
        setPos(x, y);
        setStartHealth(7);
        dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
        minimapColor = 0xffff0000;
        yOffs = 10;
        facing = TurnSynchronizer.synchedRandom.nextInt(4);

        deathPoints = 4;
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
        switch (facing) {
            case 0:
                yd -= speed;
                break;
            case 1:
                xd += speed;
                break;
            case 2:
                yd += speed;
                break;
            case 3:
                xd -= speed;
                break;
        }
        walkTime++;

        if (walkTime / 12 % 3 != 0) {
            if (shouldBounceOffWall(xd, yd)) {
                facing = facing + 2 % 4;
                xd = -xd;
                yd = -yd;
            }

            stepTime++;
            if ((!move(xd, yd) || (walkTime > 10 && TurnSynchronizer.synchedRandom.nextInt(200) == 0) && chasing==false)) {
                facing = TurnSynchronizer.synchedRandom.nextInt(4);
                walkTime = 0;
            }
        }
        xd *= 0.2;
        yd *= 0.2;
    }

    public void die() {
        super.die();
    }

    public Bitmap getSprite() {
        return Art.mummy[((stepTime / 6) & 3)][(facing + 1) & 3];
    }

    @Override
    public void collide(Entity entity, double xa, double ya) {
        super.collide(entity, xa, ya);

        if (entity instanceof Mob) {
            Mob mob = (Mob) entity;
            if (isNotFriendOf(mob)) {
                mob.hurt(this, DifficultyInformation.calculateStrength(2));
            }
        }
    }

    @Override
    public String getDeathSound() {
        return "/sound/Enemy Death 2.wav";
    }
}