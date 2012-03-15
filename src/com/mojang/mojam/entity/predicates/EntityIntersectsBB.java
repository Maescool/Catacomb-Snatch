package com.mojang.mojam.entity.predicates;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.math.BBPredicate;

/**
 * <p>
 * This predicate applies for an {@link Entity} if it intersects with the given
 * bounding box. The only exception is when the item is already removed.
 * </p>
 * <p>
 * The entity and the bounding box are considered to intersect if they share
 * at least one point together.
 * </p>
 */
public enum EntityIntersectsBB implements BBPredicate<Entity> {

    /**
     * Singleton instance of {@link EntityIntersectsBB}.
     */
    INSTANCE;

    @Override
    public boolean appliesTo(Entity item, double x0, double y0, double x1,
            double y1) {
        return !item.removed && item.intersects(x0, y0, x1, y1);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
