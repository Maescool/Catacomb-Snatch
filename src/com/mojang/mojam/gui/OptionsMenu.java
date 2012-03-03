package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import paulscode.sound.SoundSystem;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.sound.ISoundPlayer;

public class OptionsMenu extends GuiMenu {

	private boolean fullscreen;
	private boolean fps;
	private float musicVolume;
	private float soundsVolume;
	private float volume;
	private boolean creative;
    private boolean inGame;
    private int gameWidth;
    private int gameHeight;

	private int textY;

	private ClickableComponent back;

	public OptionsMenu(boolean inGame) {
		loadOptions();
		
		this.inGame = inGame;

		gameWidth = MojamComponent.GAME_WIDTH;
		gameHeight = MojamComponent.GAME_HEIGHT;
		int offset = 32;
		int xOffset = (gameWidth - Button.BUTTON_WIDTH) / 2;
		int yOffset = (gameHeight - (7 * offset + 20 + (offset * 2))) / 2;
		textY = yOffset;
		yOffset += offset;

		addButton(new Button(TitleMenu.KEY_BINDINGS_ID,
				MojamComponent.texts.getStatic("options.keyBindings"), xOffset, yOffset));

		if (!inGame) {
			addButton(new Button(TitleMenu.CHARACTER_ID,
					MojamComponent.texts.getStatic("options.characterSelect"), xOffset,
					yOffset += offset));
		}

		ClickableComponent fullscreenBtn = addButton(new Checkbox(TitleMenu.FULLSCREEN_ID,
				MojamComponent.texts.getStatic("options.fullscreen"), xOffset, yOffset += offset,
				Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE)));

		ClickableComponent fpsBtn = addButton(new Checkbox(TitleMenu.FPS_ID,
				MojamComponent.texts.getStatic("options.showfps"), xOffset, yOffset += offset,
				Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)));

		ClickableComponent soundVol = addButton(new Slider(TitleMenu.VOLUME,
				MojamComponent.texts.getStatic("options.volume"), xOffset, yOffset += offset,
				volume));

		ClickableComponent musicVol = addButton(new Slider(TitleMenu.MUSIC,
				MojamComponent.texts.getStatic("options.music"), xOffset - xOffset / 3 - 6,
				yOffset += offset, musicVolume));
		ClickableComponent soundsVol = addButton(new Slider(TitleMenu.SOUND,
				MojamComponent.texts.getStatic("options.sounds"), xOffset + xOffset / 3 + 6,
				yOffset, soundsVolume));

		ClickableComponent creativeModeBtn = addButton(new Checkbox(TitleMenu.CREATIVE_ID,
				MojamComponent.texts.getStatic("options.creative"), xOffset, yOffset += offset,
				Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE)));

		addButton(new Button(TitleMenu.CREDITS_ID,
				MojamComponent.texts.getStatic("options.credits"), xOffset, yOffset += offset));

		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
				xOffset, (yOffset += offset) + 20));

		fullscreenBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fullscreen = !fullscreen;
				Options.set(Options.FULLSCREEN, fullscreen);
				MojamComponent.toggleFullscreen();
			}
			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
		});
		fpsBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fps = !fps;
				Options.set(Options.DRAW_FPS, fps);
			}
			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
		});
		soundVol.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				volume = slider.value;

				Options.set(Options.VOLUME, volume + "");
				SoundSystem soundSystem = MojamComponent.soundPlayer.getSoundSystem();
				if (soundSystem != null)
					soundSystem.setMasterVolume(slider.value);
			}
			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
		});
		musicVol.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				musicVolume = slider.value;

				Options.set(Options.MUSIC, musicVolume + "");
				SoundSystem soundSystem = MojamComponent.soundPlayer.getSoundSystem();
				if (soundSystem != null)
					soundSystem.setVolume(ISoundPlayer.BACKGROUND_TRACK, slider.value);
			}
			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
		});
		soundsVol.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				soundsVolume = slider.value;

				Options.set(Options.SOUND, soundsVolume + "");	
			}
			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
		});
		creativeModeBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				creative = !creative;
				Options.set(Options.CREATIVE, creative);
			}
			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
		});
		back.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Options.saveProperties();
			}
			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
		});
	}

	private void loadOptions() {
		fullscreen = Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE);
		fps = Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE);
		musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
		soundsVolume = Options.getAsFloat(Options.SOUND, "1.0f");
		volume = Options.getAsFloat(Options.VOLUME, "1.0f");
		creative = Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE);
	}

	@Override
	public void render(Screen screen) {
	    
	    if( ! inGame) {
	        screen.blit(Art.background, 0, 0);
	    } else {
	        screen.alphaFill(0, 0, gameWidth, gameHeight, 0xff000000, 0x30);
	    }
		
		
		super.render(screen);
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("titlemenu.options"),
				MojamComponent.GAME_WIDTH / 2, textY, Font.Align.CENTERED);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(selectedItem).getX() - 40, buttons
				.get(selectedItem).getY() - 8);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			back.postClick();
		} else {
			super.keyPressed(e);
		}		
	}

	@Override
	public void keyReleased(KeyEvent e) {}

}
