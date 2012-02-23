package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class OptionsMenu extends GuiMenu {
	
	private boolean fullscreen;
	private boolean fps;
    private boolean muteMusic;
	private boolean gameScale;
    
	int tab1 = 30;
	int tab1i = 170;
	
	public OptionsMenu() {
		loadOptions();
		
		ClickableComponent back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
		back.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Options.saveProperties();
			}
		});
		
		ClickableComponent btnFs = addButton(new Checkbox(TitleMenu.FULLSCREEN_ID, MojamComponent.texts.getStatic("options.fullscreen"), tab1, 30, Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE)));
		btnFs.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fullscreen = !fullscreen;
				Options.set(Options.FULLSCREEN, fullscreen);
				MojamComponent.setFullscreen(fullscreen);
			}
		});
		
		ClickableComponent btnFps = addButton(new Checkbox(TitleMenu.FPS_ID, MojamComponent.texts.getStatic("options.showfps"), tab1, 60, Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)));
		btnFps.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fps = !fps;
                Options.set(Options.DRAW_FPS, fps);
			}
		});

        ClickableComponent btnPlayMusic = addButton(new Checkbox(TitleMenu.MUTE_MUSIC, MojamComponent.texts.getStatic("options.mutemusic"), tab1, 90, muteMusic));
        btnPlayMusic.addListener(new ButtonListener() {
            @Override
            public void buttonPressed(ClickableComponent button) {
                muteMusic = !muteMusic;
                Options.set(Options.MUTE_MUSIC, muteMusic);
                if(muteMusic)
                    MojamComponent.soundPlayer.stopBackgroundMusic();
                else
                    MojamComponent.soundPlayer.startBackgroundMusic();
            }
        });
        
        ClickableComponent btnScale = addButton(new Checkbox(TitleMenu.GAME_SCALE, MojamComponent.texts.getStatic("options.scale"), tab1, 120, gameScale));
        btnScale.addListener(new ButtonListener() {
            @Override
            public void buttonPressed(ClickableComponent button) {
            	gameScale = !gameScale;
            	Options.set(Options.GAME_SCALE, gameScale);
				if(gameScale)
				    MojamComponent.setScale(2);
				else
					MojamComponent.setScale(1);
            }
        });
	}
	
	private void loadOptions() {
		fullscreen = Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE);
		fps = Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE);
        muteMusic = Options.getAsBoolean(Options.MUTE_MUSIC, Options.VALUE_FALSE);
        gameScale = Options.getAsBoolean(Options.GAME_SCALE, Options.VALUE_TRUE);
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
