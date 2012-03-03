package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import paulscode.sound.SoundSystem;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.sound.ISoundPlayer;

public class AudioVideoMenu extends GuiMenu {
	private boolean fullscreen;
	private boolean fps;
	private float musicVolume;
	private float soundsVolume;
	private float volume;
	private int gameWidth;
	private int gameHeight;

    private boolean inGame;
	private int textY;

	private Button back;
	private ClickableComponent fullscreenBtn;
	private ClickableComponent fpsBtn;
	private ClickableComponent soundVol;
	private ClickableComponent musicVol;
	private ClickableComponent soundsVol;

	public AudioVideoMenu(boolean inGame) {

		loadOptions();
		
		this.inGame = inGame;

		gameWidth = MojamComponent.GAME_WIDTH;
		gameHeight = MojamComponent.GAME_HEIGHT;
		int offset = 32;
		int xOffset = (gameWidth - Button.BUTTON_WIDTH) / 2;
		int yOffset = (gameHeight - (7 * offset + 20 + (offset * 2))) / 2;
		textY = yOffset;
		yOffset += offset;

		fullscreenBtn = (ClickableComponent) addButton(new Checkbox(TitleMenu.FULLSCREEN_ID, MojamComponent.texts.getStatic("options.fullscreen"), xOffset, yOffset += offset, Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE)));
		fpsBtn = (ClickableComponent) addButton(new Checkbox(TitleMenu.FPS_ID, MojamComponent.texts.getStatic("options.showfps"), xOffset, yOffset += offset, Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)));
		soundVol = (ClickableComponent) addButton(new Slider(TitleMenu.VOLUME, MojamComponent.texts.getStatic("options.volume"), xOffset, yOffset += offset, volume));
		musicVol = (ClickableComponent) addButton(new Slider(TitleMenu.MUSIC, MojamComponent.texts.getStatic("options.music"), xOffset - xOffset / 3 - 6, yOffset += offset, musicVolume));
		soundsVol = (ClickableComponent) addButton(new Slider(TitleMenu.SOUND, MojamComponent.texts.getStatic("options.sounds"), xOffset + xOffset / 3 + 6, yOffset, soundsVolume));

		back = (Button) addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), xOffset, (yOffset += offset) + 20));

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
	}

	@Override
	public void render(Screen screen) {

		if (!inGame) {
			screen.blit(Art.background, 0, 0);
		} else {
			screen.alphaFill(0, 0, gameWidth, gameHeight, 0xff000000, 0x30);
		}

		super.render(screen);
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("titlemenu.sound_and_video"), MojamComponent.GAME_WIDTH / 2, textY, Font.Align.CENTERED);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(selectedItem).getX() - 40, buttons.get(selectedItem).getY() - 8);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
}
