package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class CreditsScreen extends GuiMenu {
	private final int gameWidth;
	private final int gameHeight;

	private ClickableComponent back;
	
	public String officialGame   = "Mojang AB";
	public String[] leadDev      = {"Maescool"};
	public String[] officialDev  = {"Borsty", "danielduner", "flet", "judgedead53", "Maescool",
			"master-lincoln", "mkalam-alami", "Scorpion1122"};
	public String[] communityMan = {"Austin01", "zorro300"};
	public String[] others = {"xPaw", "BubblegumBalloon", "Elosanda", "GreenLightning"};

	public CreditsScreen(int gameWidth, int gameHeight) {
		super();
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;

		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
				(gameWidth - 128) / 2, gameHeight - 50 - 10));
	}

	public void render(Screen screen) {
		
		screen.clear(0);
		screen.blit(Art.emptyBackground, 0, 0);

		super.render(screen);

		// Mojang logo
		screen.blit(Art.mojangLogo, (gameWidth - Art.mojangLogo.w) / 2, 30);
		
		Font font = Font.defaultFont();
		font.draw(screen, "* " + MojamComponent.texts.getStatic("credits.note"),
				(gameWidth - 512) / 2 + 30, 80);
		font.draw(screen, MojamComponent.texts.getStatic("credits.leadDev"),
				(gameWidth - 512) / 2 + 30, 110);
		font.draw(screen, MojamComponent.texts.getStatic("credits.maintainers"),
				(gameWidth - 512) / 2 + 30, 140);
		font.draw(screen, MojamComponent.texts.getStatic("credits.communityMan"),
				(gameWidth - 512) / 2 + 30, 200);
		font.draw(screen, MojamComponent.texts.getStatic("credits.others"),
				(gameWidth - 512) / 2 + 30, 230);

		font = Font.FONT_GRAY;
		drawNames(leadDev, screen, 120);
		drawNames(officialDev, screen, 150);
		drawNames(communityMan, screen, 210);
		drawNames(others, screen, 240);

		// Back button character
		screen.blit(Art.getPlayer(MojamComponent.instance.playerCharacter)[0][6], (gameWidth - 128) / 2 - 40,
				gameHeight - 50 - 20);
	
	}
	
	public int drawNames(String[] names, Screen screen, Integer y) {
		List<Vector<String>> data = new Vector<Vector<String>>();
		data.add(new Vector<String>());
		data.add(new Vector<String>());
		for (int i = 0; i < names.length; i++) {
			String string = names[i];
			if (i % 2 == 0) {
				Vector<String> tmp = data.get(0);
				tmp.add(string);
				data.set(0, tmp);
			} else {
				Vector<String> tmp = data.get(1);
				tmp.add(string);
				data.set(1, tmp);
			}
		}
		int groupId  = 0;
		int drawY    = y;
		int drawX    = 50;
		int xOffset  = 200;
		int yOffset  = 10;
		int highestY = drawY;
		for (Vector<String> group: data) {
			drawX += xOffset * groupId;
			for (String name : group) {
				Font.defaultFont().draw(screen, name, drawX, drawY);
				if (drawY > highestY) {
					highestY = drawY;
				}
				drawY += yOffset;
			}
			drawY = y;
			++groupId;
		}
		return highestY;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			back.postClick();
		} else {
			super.keyPressed(e);
		}		
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// TODO Auto-generated method stub

	}

}
