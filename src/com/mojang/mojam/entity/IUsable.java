package com.mojang.mojam.entity;

public interface IUsable {

    public void use(Entity user);
    
    public boolean upgrade(Player player);

    public void setHighlighted(boolean hl);

    public boolean isHighlightable();

    public boolean isAllowedToCancel();
}
