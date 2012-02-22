package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HowToPlay extends GuiMenu {

	public HowToPlay() {
		addButton(new Button(TitleMenu.BACK_ID, "back", MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
	}

	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		printHelpText(screen);
		super.render(screen);
	}

	private void printHelpText(Screen screen) {
		int goalX = 350;
		int tab1 = 20;
		int imgTab = 70;
		int tab2 = 100;
		int tab3 = 210;
		int goalTopMargin = 20;
		int vspace = 50;
		int line = 110;

		Font.drawCentered(screen, "Goal:", goalX, goalTopMargin);
		Font.drawCentered(screen, "collect 50 batches of treasure", goalX, goalTopMargin+10);
		Font.drawCentered(screen, "from treasure trove located", goalX, goalTopMargin+20);
		Font.drawCentered(screen, "at the center of the map", goalX, goalTopMargin+30);
		Font.drawCentered(screen, "before your opponent(s)", goalX, goalTopMargin+40);
		
		screen.blit(Art.turret[7][0], imgTab-66, line);
		screen.blit(Art.turret2[7][0], imgTab-33, line);
		screen.blit(Art.turret3[7][0], imgTab, line);
		Font.draw(screen, "turret", tab2, line);
		screen.blit(Art.pickupCoinGold[0][0], tab2, line+10);
		Font.draw(screen, "150", tab2+20, line+15);
		Font.draw(screen, "shoots enemies around it.", tab3, line);

		
		line += vspace;
		screen.blit(Art.harvester[7][0], imgTab-66, line-10);
		screen.blit(Art.harvester2[7][0], imgTab-33, line-10);
		screen.blit(Art.harvester3[7][0], imgTab, line-10);
		Font.draw(screen, "collector", tab2, line);
		screen.blit(Art.pickupCoinGold[0][0], tab2, line+10);
		Font.draw(screen, "300", tab2+20, line+15);
		Font.draw(screen, "suck up coins around it. pick it up", tab3, line);
		Font.draw(screen, "to recieve the collected coins.", tab3, line+10);

		line += vspace+10;
		screen.blit(Art.bomb, imgTab, line);
		Font.draw(screen, "bomb", tab2, line);
		screen.blit(Art.pickupCoinGold[0][0], tab2, line+10);
		Font.draw(screen, "500", tab2+20, line+15);
		Font.draw(screen, "used to destroy purple gemmed walls.", tab3, line);
		Font.draw(screen, "shoot to detonate.", tab3, line+10);

		line += vspace;
		screen.blit(Art.rails[1][0], imgTab, line);
		Font.draw(screen, "rails", tab2, line);
		screen.blit(Art.pickupCoinGold[0][0], tab2, line+10);
		Font.draw(screen, "10 (+)", tab2+20, line+15);
		Font.draw(screen, "15 (-)", tab2+20, line+25);
		Font.draw(screen, "for rail-droids to collect treasure", tab3, line);
		
		

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
