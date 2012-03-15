package com.mojang.mojam.level.gamemode;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.building.SpawnerEntity;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.predicates.EntityIntersectsBBAndInstanceOf;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.math.BBPredicate;

/**
 * This class can be used to check the surroundings for a {@link SpawnerEntity}.
 */
class SpawnSurroundingsChecker {

    private final Level level;
    private final double centerX;
    private final double centerY;
    private final BBPredicate<Entity> playerOrSpawnerPredicate;

    SpawnSurroundingsChecker(Level level, double centerX, double centerY) {
        this.level = level;
        this.centerX = centerX;
        this.centerY = centerY;
        playerOrSpawnerPredicate = new EntityIntersectsBBAndInstanceOf(
                Player.class, SpawnerEntity.class);
    }

    public boolean isSurroundingsClear() {
        return hasNoEntitiesInRadius(32 * 8, playerOrSpawnerPredicate)
                && hasNoEntitiesInRadius(32 * 4, Turret.class)
                && hasNoEntitiesInRadius(32, Building.class);
    }

    private boolean hasNoEntitiesInRadius(double r,
            BBPredicate<Entity> predicate) {
        return level.getEntities(
                centerX - r, centerY - r, centerX + r, centerY + r, predicate)
                .isEmpty();
    }

    private boolean hasNoEntitiesInRadius(double r, Class<? extends Entity> c) {
        return level.getEntities(
                centerX - r, centerY - r, centerX + r, centerY + r, c)
                .isEmpty();
    }

    public static boolean isClear(Level llevel, double x, double y) {
        return new SpawnSurroundingsChecker(llevel, x, y).isSurroundingsClear();
    }
}
