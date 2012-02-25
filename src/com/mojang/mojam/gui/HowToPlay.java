package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HowToPlay extends GuiMenu {
	
	public HowToPlay() {
		addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
	}

	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		printHelpText(screen);
		super.render(screen);
	}

	private void printHelpText(Screen screen) {
		int goalX = 350;
		int imgTab = 70;
		int tab1 = 100;
		int tab2 = 210;
		int goalTopMargin = 20;
		int vspace = 50;
		int line = 110;

		Font.drawCentered(screen, MojamComponent.texts.getStatic("help.goal") + ":", goalX, goalTopMargin);
		Font.drawCentered(screen, MojamComponent.texts.getStatic("help.help1"), goalX, goalTopMargin+10);
		Font.drawCentered(screen, MojamComponent.texts.getStatic("help.help2"), goalX, goalTopMargin+20);
		Font.drawCentered(screen, MojamComponent.texts.getStatic("help.help3"), goalX, goalTopMargin+30);
		Font.drawCentered(screen, MojamComponent.texts.getStatic("help.help4"), goalX, goalTopMargin+40);
		
		screen.blit(Art.turret[7][0], imgTab-66, line);
		screen.blit(Art.turret2[7][0], imgTab-33, line);
		screen.blit(Art.turret3[7][0], imgTab, line);
		Font.draw(screen, MojamComponent.texts.getStatic("help.turret"), tab1, line);
		screen.blit(Art.pickupCoinGold[0][0], tab1, line+10);
		Font.draw(screen, "150", tab1+20, line+15);
		Font.draw(screen, MojamComponent.texts.getStatic("help.turret1"), tab2, line);

		
		line += vspace;
		screen.blit(Art.harvester[7][0], imgTab-66, line-10);
		screen.blit(Art.harvester2[7][0], imgTab-33, line-10);
		screen.blit(Art.harvester3[7][0], imgTab, line-10);
		Font.draw(screen, MojamComponent.texts.getStatic("help.collector"), tab1, line);
		screen.blit(Art.pickupCoinGold[0][0], tab1, line+10);
		Font.draw(screen, "300", tab1+20, line+15);
		Font.draw(screen, MojamComponent.texts.getStatic("help.collector1"), tab2, line);
		Font.draw(screen, MojamComponent.texts.getStatic("help.collector2"), tab2, line+10);

		line += vspace+10;
		screen.blit(Art.bomb, imgTab, line);
		Font.draw(screen, MojamComponent.texts.getStatic("help.bomb"), tab1, line);
		screen.blit(Art.pickupCoinGold[0][0], tab1, line+10);
		Font.draw(screen, "500", tab1+20, line+15);
		Font.draw(screen, MojamComponent.texts.getStatic("help.bomb1"), tab2, line);
		Font.draw(screen, MojamComponent.texts.getStatic("help.bomb2"), tab2, line+10);

		line += vspace;
		screen.blit(Art.rails[1][0], imgTab, line);
		Font.draw(screen, MojamComponent.texts.getStatic("help.rails"), tab1, line);
		screen.blit(Art.pickupCoinGold[0][0], tab1, line+10);
		Font.draw(screen, "10 (+)", tab1+20, line+15);
		Font.draw(screen, "15 (-)", tab1+20, line+25);
		Font.draw(screen, MojamComponent.texts.getStatic("help.rails1"), tab2, line);
        Font.draw(screen, MojamComponent.texts.getStatic("help.rails2"), tab2, line+10);
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
