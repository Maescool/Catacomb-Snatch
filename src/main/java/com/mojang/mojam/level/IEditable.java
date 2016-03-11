package com.mojang.mojam.level;

import com.mojang.mojam.screen.AbstractBitmap;

public interface IEditable {

	public int getColor();
	public int getMiniMapColor();
	public String getName();
	public AbstractBitmap getBitMapForEditor();
}