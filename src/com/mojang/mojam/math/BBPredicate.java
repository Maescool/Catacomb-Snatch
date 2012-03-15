package com.mojang.mojam.math;

/**
 * A BBPredicate is a generic predicate that tests whether an item that has a
 * bounding box (i.e. it implements {@link BBOwner}) fulfills some conditions
 * in interaction with a given bounding box (for example
 */
public interface BBPredicate<T extends BBOwner> {
    
    /**
     * Returns true if the specific conditions of this BBPredicate apply to
     * the given item and the given bounding box (for example: the item is
     * completely in the given bounding box, the item intersects the bounding
     * box and extends a certain class ...).
     *
     * @param item the item that should be checked
     * @param x0   the top left corner of the box (x coordinate)
     * @param y0   the top left corner of the box (y coordinate)
     * @param x1   the bottom right corner of the box (x coordinate)
     * @param y1   the bottom right corner of the box (y coordinate)
     * @return true if the predicate applies to the item and the box,
     *         false otherwise
     */
    boolean appliesTo(T item, double x0, double y0, double x1, double y1);
    
}
