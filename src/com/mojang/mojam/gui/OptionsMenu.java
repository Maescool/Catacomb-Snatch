package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.sound.SoundPlayer;

public class OptionsMenu extends GuiMenu {

	private boolean fullscreen;
	private boolean fps;
	private float musicVolume;
	private float volume;
	private boolean creative;
	private boolean alternative;

	private ClickableComponent btnMusic;
	private ClickableComponent btnFx;

	int tab1 = 30;

	public OptionsMenu() {
		loadOptions();

		ClickableComponent back = addButton(new Button(TitleMenu.BACK_ID,
				MojamComponent.texts.getStatic("back"),
				MojamComponent.GAME_WIDTH - 128 - 20,
				MojamComponent.GAME_HEIGHT - 24 - 25));
		back.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Options.saveProperties();
			}
		});

		addButton(new Button(TitleMenu.KEY_BINDINGS_ID,
				MojamComponent.texts.getStatic("options.keyBindings"), tab1, 30));

		ClickableComponent btnFs = addButton(new Checkbox(
				TitleMenu.FULLSCREEN_ID,
				MojamComponent.texts.getStatic("options.fullscreen"), tab1, 60,
				Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE)));
		btnFs.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fullscreen = !fullscreen;
				Options.set(Options.FULLSCREEN, fullscreen);
				MojamComponent.setFullscreen(fullscreen);
			}
		});

		ClickableComponent btnFps = addButton(new Checkbox(TitleMenu.FPS_ID,
				MojamComponent.texts.getStatic("options.showfps"), tab1, 90,
				Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)));
		btnFps.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fps = !fps;
				Options.set(Options.DRAW_FPS, fps);
			}
		});

		btnFx = addButton(new Slider(TitleMenu.VOLUME,
				MojamComponent.texts.getStatic("options.volume"), tab1, 120,
				volume));

		btnFx.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) btnFx;
				volume = slider.value;

				Options.set(Options.VOLUME, volume + "");
				MojamComponent.soundPlayer.soundSystem
						.setMasterVolume(slider.value);
			}
		});

		btnMusic = addButton(new Slider(TitleMenu.MUSIC,
				MojamComponent.texts.getStatic("options.music"), tab1, 150,
				musicVolume));

		btnMusic.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) btnMusic;
				musicVolume = slider.value;

				Options.set(Options.MUSIC, musicVolume + "");
				MojamComponent.soundPlayer.soundSystem.setVolume(
						SoundPlayer.BACKGROUND_TRACK, slider.value);
			}
		});

		ClickableComponent btnCrea = addButton(new Checkbox(
				TitleMenu.CREATIVE_ID,
				MojamComponent.texts.getStatic("options.creative"), tab1, 180,
				Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE)));
		btnCrea.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				creative = !creative;
				Options.set(Options.CREATIVE, creative);
			}
		});

		ClickableComponent btnAlt = addButton(new Checkbox(
				TitleMenu.ALTERNATIVE_ID,
				MojamComponent.texts.getStatic("options.alternative"), tab1,
				210, Options.getAsBoolean(Options.ALTERNATIVE, Options.VALUE_FALSE)));
		btnAlt.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				alternative = !alternative;
				Options.set(Options.ALTERNATIVE, alternative);
			}
		});
	}

	private void loadOptions() {
		fullscreen = Options.getAsBoolean(Options.FULLSCREEN,
				Options.VALUE_FALSE);
		fps = Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE);
		musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
		volume = Options.getAsFloat(Options.VOLUME, "1.0f");
		creative = Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE);
		alternative = Options.getAsBoolean(Options.ALTERNATIVE, Options.VALUE_FALSE);
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
		if (e.getKeyCode() == KeyEvent.VK_ENTER
				|| e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			buttons.get(0).postClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
