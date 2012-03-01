package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HowToPlayMenu extends GuiMenu {

	private int goalX = 350;
	private int imgTab = 70;
	private int tab1 = 100;
	private int tab2 = 210;
	private int goalTopMargin = 20;
	private int vspace = 55;
    private boolean inGame;
	
	public HowToPlayMenu(boolean inGame) {
	    this.inGame = inGame;
	    
		addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
		
		// Background panels
		addButton(new Panel(goalX - 155, goalTopMargin - 15, 314, 78));
		addButton(new Panel(goalX - 155, goalTopMargin + 55 + 10, 314, 235));
		addButton(new Panel(imgTab - 67, goalTopMargin + 55 + 10, 189, 235));
	}

	public void render(Screen screen) {
	    
	    if( ! inGame) {
	        screen.blit(Art.background, 0, 0);
	    } else {
            screen.alphaFill(0, 0, MojamComponent.GAME_WIDTH, MojamComponent.GAME_HEIGHT, 0xff000000, 0x30);
	    }
		
		
		super.render(screen);
		printHelpText(screen);
	}

	private void printHelpText(Screen screen) {

		// Game goal
		Font font = Font.defaultFont();
		font.draw(screen, MojamComponent.texts.getStatic("help.goal") + ":", goalX, goalTopMargin, Font.Align.CENTERED);
		font.draw(screen, MojamComponent.texts.getStatic("help.help1"), goalX, goalTopMargin+10, Font.Align.CENTERED);
		font.draw(screen, MojamComponent.texts.getStatic("help.help2"), goalX, goalTopMargin+20, Font.Align.CENTERED);
		font.draw(screen, MojamComponent.texts.getStatic("help.help3"), goalX, goalTopMargin+30, Font.Align.CENTERED);
		font.draw(screen, MojamComponent.texts.getStatic("help.help4"), goalX, goalTopMargin+40, Font.Align.CENTERED);
		
		// Turret
		int line = 102;
		screen.blit(Art.turret[7][0], imgTab-60, line);
		screen.blit(Art.turret2[7][0], imgTab-30, line);
		screen.blit(Art.turret3[7][0], imgTab, line);
		font.draw(screen, MojamComponent.texts.getStatic("help.turret"), tab1, line);
		screen.blit(Art.pickupGemEmerald[0][0], tab1, line+10);
		screen.blit(Art.pickupCoinGold[0][0], tab1+15, line+10);
		font.draw(screen, "150", tab1+35, line+15);
		font.draw(screen, MojamComponent.texts.getStatic("help.turret1"), tab2, line);
		font.draw(screen, MojamComponent.texts.getStatic("help.turret2"), tab2, line+10);
		font.draw(screen, MojamComponent.texts.getStatic("help.turret3"), tab2, line+20);

		// Harvester
		line += vspace;
		screen.blit(Art.harvester[7][0], imgTab-60, line-10);
		screen.blit(Art.harvester2[7][0], imgTab-30, line-10);
		screen.blit(Art.harvester3[7][0], imgTab, line-10);
		font.draw(screen, MojamComponent.texts.getStatic("help.collector"), tab1, line);
		screen.blit(Art.pickupGemEmerald[0][0], tab1, line+10);
		screen.blit(Art.pickupGemRuby[0][0], tab1+15, line+10);
		font.draw(screen, "300", tab1+35, line+15);
		font.draw(screen, MojamComponent.texts.getStatic("help.collector1"), tab2, line);
		font.draw(screen, MojamComponent.texts.getStatic("help.collector2"), tab2, line+10);
		font.draw(screen, MojamComponent.texts.getStatic("help.collector3"), tab2, line+20);

		// Bomb
		line += vspace;
		screen.blit(Art.bomb, imgTab, line);
		font.draw(screen, MojamComponent.texts.getStatic("help.bomb"), tab1, line);
		screen.blit(Art.pickupGemDiamond[3][0], tab1, line+10);
		font.draw(screen, "500", tab1+30, line+15);
		font.draw(screen, MojamComponent.texts.getStatic("help.bomb1"), tab2, line);
		font.draw(screen, MojamComponent.texts.getStatic("help.bomb2"), tab2, line+10);
		font.draw(screen, MojamComponent.texts.getStatic("help.bomb3"), tab2, line+20);
		
		// Rail
		line += vspace;
		screen.blit(Art.rails[1][0], imgTab, line);
		font.draw(screen, MojamComponent.texts.getStatic("help.rails"), tab1, line);
		screen.blit(Art.pickupCoinBronze[0][0], tab1+10, line+15);
		font.draw(screen, "10", tab1+30, line+20);
		font.draw(screen, MojamComponent.texts.getStatic("help.rails1"), tab2, line);
		font.draw(screen, MojamComponent.texts.getStatic("help.rails2"), tab2, line+10);
		font.draw(screen, MojamComponent.texts.getStatic("help.rails3"), tab2, line+20);
        
        // Panel separation lines
        for (int i = 0; i < 3; i++) {
        	screen.fill(8, 150 + 55 * i, 180, 1, 0xFF442200);
        	screen.fill(200, 150 + 55 * i, 305, 1, 0xFF442200);
        }
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER) {
			buttons.get(0).postClick();
		} else {
			super.keyPressed(e);
		}		
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		
	}

}
