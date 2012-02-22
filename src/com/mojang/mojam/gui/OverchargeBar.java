package com.mojang.mojam.gui;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.*;

public class OverchargeBar {
	public static void draw(Screen screen, int gw, int w, int h, int x, int y)
	{
		screen.blit(Art.overchargeBar[0][1], x, y, w, h);
		screen.blit(Art.overchargeBar[0][0], x, y, gw, h);
	}
}
