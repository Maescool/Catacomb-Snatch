package com.mojang.mojam.entity;

import java.util.Random;

import com.mojang.mojam.*;
import com.mojang.mojam.entity.animation.SmokePuffAnimation;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.loot.*;
import com.mojang.mojam.entity.mob.*;
import com.mojang.mojam.entity.particle.Sparkle;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.level.tile.*;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.*;

public class Player extends Mob implements LootCollector {

    public static final int COST_RAIL = 10;
    public static final int COST_DROID = 50;
    public static final int COST_REMOVE_RAIL = 15;
    public static final int REGEN_INTERVAL = 60 * 3;

    public Keys keys;
    public double xAim, yAim = 1;
    public int shootDelay = 0;
    public double xd, yd;
    public int takeDelay = 0;
    public int flashTime = 0;
    public int suckRadius = 0;
    public boolean wasShooting;
    public int score;
    private int facing = 0;
    private int time = 0;
    private int walkTime = 0;
    private Entity selected = null;
    static final int RailDelayTicks = 15;
    private int lastRailTick = -999;
    private final static int INTERACT_DISTANCE = 20 * 20; // Sqr
    private int steps = 0;
    private boolean isSeeing;
    private int startX;
    private int startY;
    private int muzzleTicks = 0;
    private double muzzleX = 0;
    private double muzzleY = 0;
    private int muzzleImage = 0;

    private int nextWalkSmokeTick = 0;

    private int regenDelay = 0;

    public Player(Keys keys, int x, int y, int team) {
        super(x, y, team);
        this.keys = keys;

        startX = x;
        startY = y;

        score = 0;

    }

    public void tick() {
        time++;
        minimapIcon = time / 3 % 4;
        if (minimapIcon == 3) {
            minimapIcon = 1;
        }

        if (regenDelay > 0) {
            regenDelay--;
            if (regenDelay == 0) {
                if (health < maxHealth) health++;
                regenDelay = REGEN_INTERVAL;
            }
        }
        double xa = 0;
        double ya = 0;
        if (flashTime > 0) {
            flashTime = 0;
        }
        if (hurtTime > 0) {
            hurtTime--;
        }
        if (freezeTime > 0) {
            freezeTime--;
        }
        if (muzzleTicks > 0) {
            muzzleTicks--;
        }
        if (keys.up.isDown || keys.down.isDown || keys.left.isDown || keys.right.isDown) {
            if ((carrying == null && steps % 10 == 0) || (steps % 20 == 0)) {
                MojamComponent.soundPlayer.playSound("/sound/Step " + (TurnSynchronizer.synchedRandom.nextInt(2) + 1) + ".wav", (float) pos.x, (float) pos.y, true);
            }
            steps++;
        }
        if (keys.up.isDown) {
            ya--;
        }
        if (keys.down.isDown) {
            ya++;
        }
        if (keys.left.isDown) {
            xa--;
        }
        if (keys.right.isDown) {
            xa++;
        }

        if (!keys.fire.isDown && xa * xa + ya * ya != 0) {
            xAim *= 0.7;
            yAim *= 0.7;
            xAim += xa;
            yAim += ya;
            facing = (int) ((Math.atan2(-xAim, yAim) * 8 / (Math.PI * 2) + 8.5)) & 7;
        }

        if (xa != 0 || ya != 0) {
            int facing2 = (int) ((Math.atan2(-xa, ya) * 8 / (Math.PI * 2) + 8.5)) & 7;
            int diff = facing - facing2;
            if (diff >= 4) {
                diff -= 8;
            }
            if (diff < -4) {
                diff += 8;
            }

            if (carrying != null) {
                if (diff > 2 || diff < -4) {
                    walkTime--;
                } else {
                    walkTime++;
                }
            }
            if (diff > 2 || diff < -4) {
                walkTime--;
            } else {
                walkTime++;
            }

            Random random = TurnSynchronizer.synchedRandom;
            if (walkTime >= nextWalkSmokeTick) {
                level.addEntity(new SmokePuffAnimation(this, Art.fxDust12, 35 + random.nextInt(10)));
                nextWalkSmokeTick += (15 + random.nextInt(15));
            }
            if (random.nextDouble() < 0.02f) level.addEntity(new SmokePuffAnimation(this, Art.fxDust12, 35 + random.nextInt(10)));

            double dd = Math.sqrt(xa * xa + ya * ya);
            double speed = getSpeed() / dd;

            xa *= speed;
            ya *= speed;

            xd += xa;
            yd += ya;
        }

        if (freezeTime > 0) {
            move(xBump, yBump);
        } else {
            move(xd + xBump, yd + yBump);

        }
        xd *= 0.4;
        yd *= 0.4;
        xBump *= 0.8;
        yBump *= 0.8;
        muzzleImage = (muzzleImage + 1) & 3;

        if (carrying == null && keys.fire.isDown) {
            wasShooting = true;
            if (takeDelay > 0) {
                takeDelay--;
            }
            if (shootDelay-- <= 0) {
                double dir = Math.atan2(yAim, xAim) + (TurnSynchronizer.synchedRandom.nextFloat() - TurnSynchronizer.synchedRandom.nextFloat()) * 0.1;

                xa = Math.cos(dir);
                ya = Math.sin(dir);
                xd -= xa;
                yd -= ya;
                Entity bullet = new Bullet(this, xa, ya);
                level.addEntity(bullet);
                muzzleTicks = 3;
                muzzleX = bullet.pos.x + 7 * xa - 8;
                muzzleY = bullet.pos.y + 5 * ya - 8 + 1;
                shootDelay = 5;
                MojamComponent.soundPlayer.playSound("/sound/Shot 1.wav", (float) pos.x, (float) pos.y);
            }
        } else {
            if (wasShooting) {
                suckRadius = 60;
            }
            wasShooting = false;
            if (suckRadius > 0) {
                suckRadius--;
            }
            takeDelay = 15;
            shootDelay = 0;
        }

        int x = (int) pos.x / Tile.WIDTH;
        int y = (int) pos.y / Tile.HEIGHT;

        if (keys.build.isDown && !keys.build.wasDown) {
            if (level.getTile(x, y).isBuildable()) {
                if (score >= COST_RAIL && time - lastRailTick >= RailDelayTicks) {
                    lastRailTick = time;
                    level.placeTile(x, y, new RailTile(level.getTile(x, y)), this);
                    payCost(COST_RAIL);
                }
            } else if (level.getTile(x, y) instanceof RailTile) {
                if ((y < 8 && team == Team.Team2) || (y > level.height - 9 && team == Team.Team1)) {
                    if (score >= COST_DROID) {
                        level.addEntity(new RailDroid(pos.x, pos.y, team));
                        payCost(COST_DROID);
                    }
                } else {

                    if (score >= COST_REMOVE_RAIL && time - lastRailTick >= RailDelayTicks) {
                        lastRailTick = time;
                        if (((RailTile) level.getTile(x, y)).remove()) {
                            payCost(COST_REMOVE_RAIL);
                        }
                    }
                    MojamComponent.soundPlayer.playSound("/sound/Track Place.wav", (float) pos.x, (float) pos.y);
                }
            }
        }



        if (carrying != null) {
            carrying.setPos(pos.x, pos.y - 20);
            carrying.tick();

            if (keys.use.wasPressed()) {
                Vec2 buildPos = pos.clone();
                Tile tile = level.getTile(buildPos);
//                if (tile != null && tile.isBuildable()) {
                boolean allowed = true;
/*
 * for (Entity e : level.getEntities(x - Building.MIN_BUILDING_DISTANCE, y -
 * Building.MIN_BUILDING_DISTANCE, x + Building.MIN_BUILDING_DISTANCE, y +
 * Building.MIN_BUILDING_DISTANCE, Building.class)) { if
 * (e.pos.distSqr(buildPos) < Building.MIN_BUILDING_DISTANCE) { allowed = false;
 * break; } }
 */
                if (allowed && (!(carrying instanceof IUsable) || (carrying instanceof IUsable && ((IUsable) carrying).isAllowedToCancel()))) {
                    carrying.removed = false;
                    carrying.xSlide = xAim * 3;
                    carrying.ySlide = yAim * 3;
                    carrying.freezeTime = 10;
                    carrying.setPos(buildPos.x, buildPos.y);
                    level.addEntity(carrying);
                    carrying = null;
                }
//                }
            }
        } else {
            Entity closest = null;
            double closestDist = Double.MAX_VALUE;
            for (Entity e : level.getEntitiesSlower(pos.x - INTERACT_DISTANCE, pos.y - INTERACT_DISTANCE, pos.x + INTERACT_DISTANCE, pos.y + INTERACT_DISTANCE, Building.class)) {
                double dist = e.pos.distSqr(getInteractPosition());
                if (dist <= INTERACT_DISTANCE && dist < closestDist) {
                    closestDist = dist;
                    closest = e;
                }
            }
            if (selected != null) {
                ((IUsable) selected).setHighlighted(false);
            }
            if (closest != null && ((IUsable) closest).isHighlightable()) {
                selected = closest;
                ((IUsable) selected).setHighlighted(true);
            }

            if (selected != null) {
                if (selected.pos.distSqr(getInteractPosition()) > INTERACT_DISTANCE) {
                    ((IUsable) selected).setHighlighted(false);
                    selected = null;
                } else if (selected instanceof IUsable && keys.use.wasPressed()) {
                    ((IUsable) selected).use(this);
                } else if (selected instanceof IUsable && keys.upgrade.wasPressed()){
                	((IUsable) selected).upgrade(this);
                }
            }

        }

        if (isSeeing) {
            level.reveal(x, y, 5);
        }
    }

    public void payCost(int cost) {
        score -= cost;

        while (cost > 0) {
            double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
            Loot loot = new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir), cost / 2);
            loot.makeUntakeable();
            level.addEntity(loot);

            cost -= loot.getScoreValue();
        }
    }

    public void addScore(int s) {
        if (s > 0) score += s;
    }

    public void dropAllMoney() {

        score /= 2;
        while (score > 0) {
            double dir = TurnSynchronizer.synchedRandom.nextDouble() * Math.PI * 2;
            Loot loot = new Loot(pos.x, pos.y, Math.cos(dir), Math.sin(dir), score / 2);
            level.addEntity(loot);

            score -= loot.getScoreValue();
        }
        score = 0;
    }

    public void render(Screen screen) {
        Bitmap[][] sheet = Art.lordLard;
        if (team == Team.Team2) {
            sheet = Art.herrSpeck;
        }

        int frame = (walkTime / 4 % 6 + 6) % 6;

        int facing = this.facing + (carrying != null ? 8 : 0);
        double xmuzzle = muzzleX + ((facing == 0) ? 4 : 0);
        double ymuzzle = muzzleY - ((facing == 0) ? 4 : 0);

        boolean behind = (facing >= 3 && facing <= 5);

        if (muzzleTicks > 0 && behind) {
            screen.blit(Art.muzzle[muzzleImage][0], xmuzzle, ymuzzle);
        }

        if (hurtTime % 2 != 0) {
            screen.colorBlit(sheet[frame][facing], pos.x - Tile.WIDTH / 2, pos.y - Tile.HEIGHT / 2 - 8, 0x80ff0000);
        } else if (flashTime > 0) {
            screen.colorBlit(sheet[frame][facing], pos.x - Tile.WIDTH / 2, pos.y - Tile.HEIGHT / 2 - 8, 0x80ffff80);
        } else {
            screen.blit(sheet[frame][facing], pos.x - Tile.WIDTH / 2, pos.y - Tile.HEIGHT / 2 - 8);
        }

        if (muzzleTicks > 0 && !behind) {
            screen.blit(Art.muzzle[muzzleImage][0], xmuzzle, ymuzzle);
        }

        renderCarrying(screen, (frame == 0 || frame == 3) ? -1 : 0);
    }

    public void collide(Entity entity, double xa, double ya) {
        xd += xa * 0.4;
        yd += ya * 0.4;
    }

    public void take(Loot loot) {
        loot.remove();
        level.addEntity(new Sparkle(pos.x, pos.y, -1, 0));
        level.addEntity(new Sparkle(pos.x, pos.y, +1, 0));
        score += loot.getScoreValue();
    }

    public double getSuckPower() {
        return suckRadius / 60.0;
    }

    public boolean canTake() {
        return takeDelay > 0;
    }

    public void flash() {
        flashTime = 20;
    }

    public int getScore() {
        return score;
    }

    public Bitmap getSprite() {
        return null;
    }

    public boolean useMoney(int cost) {
        if (cost > score) {
            return false;
        }

        score -= cost;
        return true;
    }

    private Vec2 getInteractPosition() {
        return pos.add(new Vec2(Math.cos((facing) * (Math.PI) / 4 + Math.PI / 2), Math.sin((facing) * (Math.PI) / 4 + Math.PI / 2)).scale(30));
    }

    public void pickup(Building b) {
        level.removeEntity(b);
        carrying = b;
        carrying.onPickup();
        // level.addEntity( new SmokePuffAnimation(b, Art.fxDust12, 40));
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    @Override
    protected boolean shouldBlock(Entity e) {
        if (carrying != null && e instanceof Bullet && ((Bullet) e).owner == carrying) {
            return false;
        }
        return true;
    }

    public void setCanSee(boolean b) {
        this.isSeeing = b;
    }

    public void notifySucking() {
    }

    @Override
    public void hurt(Entity source, int damage) {
        if (isImmortal) {
            return;
        }

        if (hurtTime == 0) {
            hurtTime = 25;
            freezeTime = 15;
            health -= damage;
            regenDelay = REGEN_INTERVAL;

            if (health <= 0) {
                Notifications.getInstance().add((team == Team.Team1 ? "Lord Lard" : "Herr Von Speck") + " has died!");
                carrying = null;
                dropAllMoney();
                pos.set(startX, startY);
                health = maxHealth;
            } else {

                double dist = source.pos.dist(pos);
                xBump = (pos.x - source.pos.x) / dist * 10;
                yBump = (pos.y - source.pos.y) / dist * 10;
            }
        }
    }

    @Override
    public void hurt(Bullet bullet) {
        hurt(bullet, 1);
    }

    public String getDeatchSound() {
        return "/sound/Death.wav";
    }
}
