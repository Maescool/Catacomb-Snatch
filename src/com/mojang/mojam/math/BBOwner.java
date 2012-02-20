package com.mojang.mojam.math;

import com.mojang.mojam.entity.Entity;

public interface BBOwner {
    void handleCollision(Entity entity, double xa, double ya);
}
