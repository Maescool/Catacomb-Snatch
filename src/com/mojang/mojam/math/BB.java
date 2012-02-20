package com.mojang.mojam.math;

public class BB {
    public double x0, y0;
    public double x1, y1;
    public BBOwner owner;

    public BB(BBOwner owner, double x0, double y0, double x1, double y1) {
        this.owner = owner;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public boolean intersects(double xx0, double yy0, double xx1, double yy1) {
        if (xx0 >= x1 || yy0 >= y1 || xx1 <= x0 || yy1 <= y0) return false;
        return true;
    }

    public BB grow(double s) {
        return new BB(owner, x0 - s, y0 - s, x1 + s, y1 + s);
    }

    public boolean intersects(BB bb) {
        if (bb.x0 >= x1 || bb.y0 >= y1 || bb.x1 <= x0 || bb.y1 <= y0) return false;
        return true;
    }
}
