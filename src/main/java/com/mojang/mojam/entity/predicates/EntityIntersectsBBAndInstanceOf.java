package com.mojang.mojam.entity.predicates;

import java.util.Collection;
import java.util.HashSet;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.math.BBPredicate;

/**
 * <p>
 * This predicate applies for an {@link Entity} if it intersects with the given
 * bounding box AND is an instance of at least one of the given classes.
 * The only exception is when the item is already removed.
 * </p>
 * <p>
 * The entity and the bounding box are considered to intersect if they share
 * at least one point together.
 * </p>
 */
public class EntityIntersectsBBAndInstanceOf implements BBPredicate<Entity> {
	
	private final Collection<Class<? extends Entity>> entityClasses;

	/**
	 * Constructs a predicate that is true if the given entity is an instance of
	 * class1 and intersects the bb.
	 * @param class1 first class to consider
	 */
	public EntityIntersectsBBAndInstanceOf(Class<? extends Entity> class1) {
		this.entityClasses = new HashSet<Class<? extends Entity>>();
		this.entityClasses.add(class1);
	}

	/**
	 * Constructs a predicate that is true if the given entity is an instance of
	 * either class1 or class2 and intersects the bb.
	 * @param class1 first class to consider
	 * @param class2 second class to consider
	 */
	public EntityIntersectsBBAndInstanceOf(Class<? extends Entity> class1,
			Class<? extends Entity> class2) {
		this(class1);
		entityClasses.add(class2);
	}

	// no var-args constructor,
	// because then there would be an unchecked assignment warning;
	// just add the necessary constructors as needed

	@Override
	public boolean appliesTo(Entity item, double x0, double y0, double x1, double y1) {
		for (final Class<? extends Entity> entityClass : entityClasses) {
			if (entityClass.isInstance(item)) {
				return EntityIntersectsBB.INSTANCE.appliesTo(item, x0, y0, x1, y1);
			}
		}
		return false;
	}

	public String toString() {
		return "EntityIntersectsOneOfBBPredicate{" +
				"entityClasses=" + entityClasses +
				'}';
	}
}
