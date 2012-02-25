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
	private float soundsVolume;
	private float volume;
	private boolean creative;
	private boolean alternative;
	
	private int textY;

	private ClickableComponent back;

	private int selectedItem;

	public OptionsMenu() {
		loadOptions();

		int gameWidth = MojamComponent.GAME_WIDTH;
		int gameHeight = MojamComponent.GAME_HEIGHT;
		int offset = 32;
		int xOffset = (gameWidth - Button.BUTTON_WIDTH) / 2;
		int yOffset = (gameHeight - (7 * offset + 20 + 32)) / 2;
		textY = yOffset;
		yOffset += 32;

		addButton(new Button(TitleMenu.KEY_BINDINGS_ID,
				MojamComponent.texts.getStatic("options.keyBindings"), xOffset, yOffset));

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
				MojamComponent.texts.getStatic("options.music"), xOffset - xOffset/3 - 4, yOffset += offset,
				musicVolume));
		ClickableComponent soundsVol = addButton(new Slider(TitleMenu.SOUNDS,
				MojamComponent.texts.getStatic("options.sounds"), xOffset + xOffset/3 + 4, yOffset,
				soundsVolume));

		ClickableComponent creativeModeBtn = addButton(new Checkbox(TitleMenu.CREATIVE_ID,
			MojamComponent.texts.getStatic("options.creative"), xOffset, yOffset += offset,
			Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE)));  

		ClickableComponent alternativeSkinBtn = addButton(new Checkbox(TitleMenu.ALTERNATIVE_ID,
				MojamComponent.texts.getStatic("options.alternative"), xOffset, yOffset += offset,
				Options.getAsBoolean(Options.ALTERNATIVE, Options.VALUE_FALSE)));
		
		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
				xOffset, (yOffset += offset) + 20));

		fullscreenBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
			    fullscreen = !fullscreen;
			    Options.set(Options.FULLSCREEN, fullscreen);
			    MojamComponent.toggleFullscreen();
			}
		});
		fpsBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fps = !fps;
				Options.set(Options.DRAW_FPS, fps);
			}
		});
		soundVol.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				volume = slider.value;

				Options.set(Options.VOLUME, volume + "");
				MojamComponent.soundPlayer.soundSystem.setMasterVolume(slider.value);
			}
		});
		musicVol.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				musicVolume = slider.value;

				Options.set(Options.MUSIC, musicVolume + "");
				MojamComponent.soundPlayer.soundSystem.setVolume(SoundPlayer.BACKGROUND_TRACK,
						slider.value);
			}
		});
		soundsVol.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				soundsVolume = slider.value;

				Options.set(Options.SOUNDS, soundsVolume + "");
			}
		});
		creativeModeBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				creative = !creative;
				Options.set(Options.CREATIVE, creative);
			}
		});

		alternativeSkinBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				alternative = !alternative;
				Options.set(Options.ALTERNATIVE, alternative);
			}
		});
		back.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Options.saveProperties();
			}
		});
	}

	private void loadOptions() {
		fullscreen = Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE);
		fps = Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE);
		musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
		soundsVolume = Options.getAsFloat(Options.SOUNDS, "1.0f");
		volume = Options.getAsFloat(Options.VOLUME, "1.0f");
		creative = Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE);
		alternative = Options.getAsBoolean(Options.ALTERNATIVE, Options.VALUE_FALSE);
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		super.render(screen);
		Font.drawCentered(screen, MojamComponent.texts.getStatic("titlemenu.options"),
				MojamComponent.GAME_WIDTH / 2, textY);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(selectedItem).getX() - 40,
				buttons.get(selectedItem).getY() - 8);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			back.postClick();
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			selectedItem--;
			if (selectedItem < 0) {
				selectedItem = buttons.size() - 1;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			selectedItem++;
			if (selectedItem >= buttons.size()) {
				selectedItem = 0;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			ClickableComponent button = buttons.get(selectedItem);
			if (button instanceof Slider) {
				Slider slider = (Slider) button;
				float value = slider.value - 0.1f;
				if (value < 0) {
					value = 0;
				}
				slider.setValue(value);
				slider.postClick();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			ClickableComponent button = buttons.get(selectedItem);
			if (button instanceof Slider) {
				Slider slider = (Slider) button;
				float value = slider.value + 0.1f;
				if (value > 1) {
					value = 1;
				}
				slider.setValue(value);
				slider.postClick();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			e.consume();
			ClickableComponent button = buttons.get(selectedItem);
			if (button instanceof Slider) {
				Slider slider = (Slider) button;
				if (slider.value == 1) {
					slider.setValue(0);
				} else {
					slider.setValue(1);
				}
			}
			button.postClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

}
