package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class OptionsMenu extends GuiMenu {
	
	private boolean fullscreen;
	private boolean fps;
	
	int tab1 = 30;
	int tab1i = 170;
	
	public OptionsMenu() {
		loadOptions();
		
		ClickableComponent back = addButton(new Button(TitleMenu.BACK_ID, "back", MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
		back.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Options.saveProperties();
			}
		});
		
		ClickableComponent btnFs = addButton(new Checkbox(TitleMenu.FULLSCREEN_ID, MojamComponent.texts.getStatic("options.fullscreen"), tab1, 30));
		btnFs.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fullscreen = !fullscreen;
				Options.set("fullscreen", fullscreen ? "true" : "false");
				MojamComponent.setFullscreen(fullscreen);
			}
		});
		
		ClickableComponent btnFps = addButton(new Checkbox(TitleMenu.FPS_ID, MojamComponent.texts.getStatic("options.showfps"), tab1, 60));
		btnFps.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fps = !fps;
				Options.set("drawFPS", fps ? "true" : "false");
			}
		});
	}
	
	private void loadOptions() {
		fullscreen = Options.get("fullscreen") != null && Boolean.parseBoolean(Options.get("fullscreen"));
		fps = Options.get("drawFPS") != null && Boolean.parseBoolean(Options.get("drawFPS"));
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		
		super.render(screen);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {

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

}
