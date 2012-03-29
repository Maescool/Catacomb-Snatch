package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import paulscode.sound.SoundSystem;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ButtonListener;
import com.mojang.mojam.gui.components.Checkbox;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.gui.components.Slider;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.sound.ISoundPlayer;

public class AudioVideoMenu extends GuiMenu {
	private boolean fullscreen;
	private boolean opengl;
	private boolean trapMouse;
	private boolean fps;
	private float musicVolume;
	private float soundsVolume;
	private float volume;
	private int gameWidth;
	private int gameHeight;
    private boolean openAL;

    private boolean inGame;
	private int textY;

	private ClickableComponent back;
	private ClickableComponent fullscreenBtn;
	private ClickableComponent openGlBtn;
	private ClickableComponent trapMouseBtn;
	private ClickableComponent fpsBtn;
	private ClickableComponent soundVol;
	private ClickableComponent musicVol;
	private ClickableComponent soundsVol;
    private ClickableComponent soundOpenALBtn;

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

		fullscreenBtn = addButton(
					new Checkbox(TitleMenu.FULLSCREEN_ID,
						MojamComponent.texts.getStatic("options.fullscreen"), xOffset,
						yOffset += offset, Options.getAsBoolean(Options.FULLSCREEN,
						Options.VALUE_FALSE))
				);
		openGlBtn = addButton(
				new Checkbox(TitleMenu.OPEN_GL_ID,
					MojamComponent.texts.getStatic("options.opengl"), xOffset,
					yOffset += offset, Options.getAsBoolean(Options.OPENGL,
					Options.VALUE_FALSE))
			);
		trapMouseBtn = addButton(
				new Checkbox(TitleMenu.MOUSE_TRAP_ID,
					MojamComponent.texts.getStatic("options.trapmouse"), xOffset,
					yOffset += offset, Options.getAsBoolean(Options.TRAP_MOUSE,
					Options.VALUE_FALSE))
			);
		fpsBtn = addButton(
					new Checkbox(TitleMenu.FPS_ID,
						MojamComponent.texts.getStatic("options.showfps"), xOffset,
						yOffset += offset, Options.getAsBoolean(Options.DRAW_FPS,
						Options.VALUE_FALSE))
				);
        soundOpenALBtn = addButton(
                    new Checkbox(TitleMenu.OPEN_AL_ID,
                        MojamComponent.texts.getStatic("options.openal"), xOffset,
                        yOffset += offset, Options.getAsBoolean(Options.OPEN_AL,
                        Options.VALUE_TRUE))
                );
		soundVol = addButton(
					new Slider(TitleMenu.VOLUME,
						MojamComponent.texts.getStatic("options.volume"), xOffset,
						yOffset += offset, volume)
				);
		musicVol = addButton(
					new Slider(TitleMenu.MUSIC,
						MojamComponent.texts.getStatic("options.music"),
						xOffset	- xOffset / 3 - 20, yOffset += offset, musicVolume)
				);
		soundsVol = addButton(
					new Slider(TitleMenu.SOUND,
						MojamComponent.texts.getStatic("options.sounds"),
						xOffset + xOffset / 3 + 20, yOffset, soundsVolume)
				);
		back = addButton(
					new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
							xOffset, (yOffset += offset) + 20)
				);

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
		openGlBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				opengl = !opengl;
				Options.set(Options.OPENGL, opengl);
				// TODO
			}

			@Override
			public void buttonHovered(ClickableComponent clickableComponent) {
			}
		});
		trapMouseBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				trapMouse = !trapMouse;
				Options.set(Options.TRAP_MOUSE, trapMouse);
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
        soundOpenALBtn.addListener(new ButtonListener() {
            @Override
            public void buttonPressed(ClickableComponent button) {
                openAL = !openAL;
                Options.set(Options.OPEN_AL, openAL);
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
		trapMouse = Options.getAsBoolean(Options.TRAP_MOUSE, Options.VALUE_FALSE);
		opengl = Options.getAsBoolean(Options.OPENGL, Options.VALUE_FALSE);
		fps = Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE);
        openAL = Options.getAsBoolean(Options.OPEN_AL, Options.VALUE_TRUE);
		musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
		soundsVolume = Options.getAsFloat(Options.SOUND, "1.0f");
		volume = Options.getAsFloat(Options.VOLUME, "1.0f");
	}

	@Override
	public void render(AbstractScreen screen) {

		if (!inGame) {
			screen.blit(Art.background, 0, 0);
		} else {
			screen.alphaFill(0, 0, gameWidth, gameHeight, 0xff000000, 0xC0);
		}

		super.render(screen);
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("titlemenu.sound_and_video"), MojamComponent.GAME_WIDTH / 2, textY, Font.Align.CENTERED);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(selectedItem).getX() - 40, buttons.get(selectedItem).getY() - 8);
	}

	@Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			back.postClick();
		} else {
			super.keyPressed(e);
		}
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
