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

    public Mummy(double x, double y) {
        super(x, y, Team.Neutral);
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
            Set<Entity> entities = level.getEntities(pos.x - ATTACK_RADIUS, pos.y - ATTACK_RADIUS, pos.x + ATTACK_RADIUS, pos.y + ATTACK_RADIUS, Player.class);
            Entity closest = null;
            double closestDist = 99999999.0f;
            for (Entity e : entities) {
                final double dist = e.pos.distSqr(pos);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = e;
                }
            }
            if (closest != null) {
                if (!this.isTargetBehindWall(closest.pos.x, closest.pos.y, closest)) {
                    double angle = Math.atan2((closest.pos.y - pos.y), (closest.pos.x - pos.x));
                    facing = (int) (((Math.toDegrees(angle) + 360) / 90) - 2);
                }
            }
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
            if (!move(xd, yd) || (walkTime > 10 && TurnSynchronizer.synchedRandom.nextInt(200) == 0)) {
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

    private boolean isTargetBehindWall(double dx2, double dy2, Entity e) {
        int x1 = (int) pos.x / Tile.WIDTH;
        int y1 = (int) pos.y / Tile.HEIGHT;
        int x2 = (int) dx2 / Tile.WIDTH;
        int y2 = (int) dy2 / Tile.HEIGHT;

        int dx, dy, inx, iny, a;
        Tile temp;

        dx = x2 - x1;
        dy = y2 - y1;
        inx = dx > 0 ? 1 : -1;
        iny = dy > 0 ? 1 : -1;

        dx = java.lang.Math.abs(dx);
        dy = java.lang.Math.abs(dy);

        if (dx >= dy) {
            dy <<= 1;
            a = dy - dx;
            dx <<= 1;
            while (x1 != x2) {
                temp = level.getTile(x1, y1);
                if (!temp.canPass(e)) {
                    return true;
                }
                if (a >= 0) {
                    y1 += iny;
                    a -= dx;
                }
                a += dy;
                x1 += inx;
            }
        } else {
            dx <<= 1;
            a = dx - dy;
            dy <<= 1;
            while (y1 != y2) {
                temp = level.getTile(x1, y1);
                if (!temp.canPass(e)) {
                    return true;
                }
                if (a >= 0) {
                    x1 += inx;
                    a -= dy;
                }
                a += dx;
                y1 += iny;
            }
        }
        temp = level.getTile(x1, y1);
        if (!temp.canPass(e)) {
            return true;
        }
        return false;
    }
}
