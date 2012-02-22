package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class Help extends GuiMenu {
	
	public static int TOP_MARGIN = 150;
	public ClickableComponent backButton;
	
	public Help() {
		backButton = addButton(new Button(TitleMenu.CANCEL_JOIN_ID, "Back", 190, 320));
	}
	
	@Override
	public void render(Screen screen) {
		screen.clear(0);
		screen.blit(Art.titleScreen, 0, 0);
		super.render(screen);	
		
		printHelpText(screen);
	}
	
	private void printHelpText(Screen screen) {
		int centerX = screen.w/2;
		int tab1 = 20;
		int tab2 = 80;
		int tab3 = 170;
		int goalTopMargin = TOP_MARGIN;
		int itemsTopMargin = TOP_MARGIN+60;		
		
		Font.drawCentered(screen, "Goal:", centerX, goalTopMargin);
		Font.drawCentered(screen, "collect 50 batches of treasure", centerX, goalTopMargin+10);
		Font.drawCentered(screen, "from treasure trove located", centerX, goalTopMargin+20);
		Font.drawCentered(screen, "at the center of the map", centerX, goalTopMargin+30);
		Font.drawCentered(screen, "before your opponent(s)", centerX, goalTopMargin+40);
		
		Font.draw(screen, "items:", tab1, itemsTopMargin);
		Font.draw(screen, "turret", tab2, itemsTopMargin);
		Font.draw(screen, "shoots enemies around it.", tab3, itemsTopMargin);
				
		Font.draw(screen, "collector", tab2, itemsTopMargin+20);
		Font.draw(screen, "suck up coins around it. pick it up", tab3, itemsTopMargin+20);
		Font.draw(screen, "to recieve the collected coins.", tab3, itemsTopMargin+30);
		
		Font.draw(screen, "bomb", tab2, itemsTopMargin+50);
		Font.draw(screen, "used to destroy purple gemmed walls.", tab3, itemsTopMargin+50);
		Font.draw(screen, "shoot to detonate.", tab3, itemsTopMargin+60);
				
		Font.draw(screen, "track", tab2, itemsTopMargin+80);
		Font.draw(screen, "for rail-droids to collect treasure", tab3, itemsTopMargin+80);
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER || e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			backButton.postClick();
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// TODO Auto-generated method stub
		
	}

}
