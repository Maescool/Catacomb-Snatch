package com.mojang.mojam.level;

import com.mojang.mojam.math.Vec2;

public class TestLevel {


    static final public int[][] tiles = {
            {
                    1, 1, 1, 1, 1, 1
            }, {
                    1, 0, 0, 0, 0, 1
            }, {
                    1, 0, 1, 1, 0, 1
            }, {
                    1, 0, 1, 1, 0, 1
            }, {
                    1, 0, 0, 0, 0, 1
            }, {
                    1, 1, 1, 1, 1, 1
            }
    };

    static final int w = tiles[0].length;
    static final int h = tiles.length;

    public boolean isOnMap(Vec2 pos) {
        Vec2 gridPos = pos.floor();
        if (gridPos.x < 0 || gridPos.y < 0) return false;
        return gridPos.x < w && gridPos.y < h;
    }

    public boolean canWalk(Vec2 pos) {
        if (!isOnMap(pos)) return false;
        int tileId = tiles[(int) pos.y][(int) pos.x];
        return tileId == 0;
    }

    public Node getNode(Vec2 pos) {
        return new Node(pos.floor());
    }
}
