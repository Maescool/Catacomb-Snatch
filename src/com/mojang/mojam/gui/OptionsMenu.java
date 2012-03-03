package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import paulscode.sound.SoundSystem;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.sound.ISoundPlayer;

public class OptionsMenu extends GuiMenu {

	private boolean creative;
    private boolean inGame;
    private int gameWidth;
    private int gameHeight;

	private int textY;

	private ClickableComponent back;

	public void change_locale() {
	}

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

		addButton(new Button(TitleMenu.KEY_BINDINGS_ID, MojamComponent.texts.getStatic("options.keyBindings"), xOffset, yOffset));

		if (!inGame) {
			addButton(new Button(TitleMenu.CHARACTER_ID, MojamComponent.texts.getStatic("options.characterSelect"), xOffset, yOffset += offset));
		}

		addButton(new Button(TitleMenu.AUDIO_VIDEO_ID, MojamComponent.texts.getStatic("options.sound_and_video"), xOffset, yOffset += offset));

		addButton(new Button(TitleMenu.LOCALE_ID, MojamComponent.texts.getStatic("options.locale_selection"), xOffset, yOffset += offset));

		ClickableComponent creativeModeBtn = addButton(new Checkbox(TitleMenu.CREATIVE_ID, MojamComponent.texts.getStatic("options.creative"), xOffset, yOffset += offset, Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE)));

		addButton(new Button(TitleMenu.CREDITS_ID, MojamComponent.texts.getStatic("options.credits"), xOffset, yOffset += offset));

		back = addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), xOffset, (yOffset += offset) + 20));

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
