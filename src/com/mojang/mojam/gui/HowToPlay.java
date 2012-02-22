package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HowToPlay extends GuiMenu {
	
	public static int TOP_MARGIN = 50;

	public HowToPlay() {
		addButton(new Button(TitleMenu.RETURN_TO_TITLESCREEN, "back", MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
	}

	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		printHelpText(screen);
		super.render(screen);
	}

	private void printHelpText(Screen screen) {
		int centerX = screen.w/2;
		int tab1 = 20;
		int tab2 = 80;
		int tab3 = 170;
		int goalTopMargin = TOP_MARGIN;
		int line = 110;

		Font.draw(screen, "Goal:", centerX, goalTopMargin);
		Font.draw(screen, "collect 50 batches of treasure", centerX, goalTopMargin+10);
		Font.draw(screen, "from treasure trove located", centerX, goalTopMargin+20);
		Font.draw(screen, "at the center of the map", centerX, goalTopMargin+30);
		Font.draw(screen, "before your opponent(s)", centerX, goalTopMargin+40);

		Font.draw(screen, "items:", tab1, line);
		
		line += 20;
		screen.blit(Art.turret[7][0], tab2-20, line);
		Font.draw(screen, "turret", tab2, line);
		Font.draw(screen, "shoots enemies around it.", tab3, line);

		
		line += 20;
		screen.blit(Art.harvester[7][0], tab2-20, line+20);
		Font.draw(screen, "collector", tab2, line);
		Font.draw(screen, "suck up coins around it. pick it up", tab3, line);
		Font.draw(screen, "to recieve the collected coins.", tab3, line+10);

		screen.blit(Art.bomb, tab2-20, line+50);
		Font.draw(screen, "bomb", tab2, line+50);
		Font.draw(screen, "used to destroy purple gemmed walls.", tab3, line+50);
		Font.draw(screen, "shoot to detonate.", tab3, line+60);

		screen.blit(Art.rails[1][0], tab2-20, line+80);
		Font.draw(screen, "track", tab2, line+80);
		Font.draw(screen, "for rail-droids to collect treasure", tab3, line+80);
		
		screen.blit(Art.pickupCoinGold[0][0], tab2-20, line+150);

		}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			buttons.get(0).postClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

}
