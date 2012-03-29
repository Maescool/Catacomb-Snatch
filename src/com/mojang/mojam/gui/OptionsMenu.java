package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.building.ShopItem;
import com.mojang.mojam.entity.weapon.IWeapon;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ButtonListener;
import com.mojang.mojam.gui.components.Checkbox;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;

public class OptionsMenu extends GuiMenu {

	private boolean creative;
    private boolean inGame;
    private int gameWidth;
    private int gameHeight;

	private int textY;

	private Button key_bindings;
	private Button character_select;
	private Button sound_and_video;
	private Button locale;
	private Checkbox creativeModeBtn;
	private Button credits;
	private Button back;

	public void changeLocale() {
		key_bindings.setLabel(MojamComponent.texts.getStatic("options.keyBindings"));
		if (!inGame) {
			character_select.setLabel(MojamComponent.texts.getStatic("options.characterSelect"));
		}
		sound_and_video.setLabel(MojamComponent.texts.getStatic("options.sound_and_video"));
		locale.setLabel(MojamComponent.texts.getStatic("options.locale_selection"));
		creativeModeBtn.setLabel(MojamComponent.texts.getStatic("options.creative"));
		credits.setLabel(MojamComponent.texts.getStatic("options.credits"));
		back.setLabel(MojamComponent.texts.getStatic("back"));
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

		key_bindings = (Button) addButton(new Button(TitleMenu.KEY_BINDINGS_ID, MojamComponent.texts.getStatic("options.keyBindings"), xOffset, yOffset));
		if (!inGame) {
			character_select = (Button) addButton(new Button(TitleMenu.CHARACTER_ID, MojamComponent.texts.getStatic("options.characterSelect"), xOffset, yOffset += offset));
		}
		sound_and_video = (Button) addButton(new Button(TitleMenu.AUDIO_VIDEO_ID, MojamComponent.texts.getStatic("options.sound_and_video"), xOffset, yOffset += offset));
		locale = (Button) addButton(new Button(TitleMenu.LOCALE_ID, MojamComponent.texts.getStatic("options.locale_selection"), xOffset, yOffset += offset));
		creativeModeBtn = (Checkbox) addButton(new Checkbox(TitleMenu.CREATIVE_ID, MojamComponent.texts.getStatic("options.creative"), xOffset, yOffset += offset, Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE)));
		credits = (Button) addButton(new Button(TitleMenu.CREDITS_ID, MojamComponent.texts.getStatic("options.credits"), xOffset, yOffset += offset));
		back = (Button) addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"), xOffset, (yOffset += offset) + 20));

		creativeModeBtn.addListener(new ButtonListener() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				creative = !creative;
				Options.set(Options.CREATIVE, creative);
				setPrices();
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
	
	public void setPrices(){
		if (MojamComponent.instance.player != null) {
			MojamComponent.instance.player.setRailPricesAndImmortality();
			ShopItem.updatePrices();
			for(IWeapon i:MojamComponent.instance.player.weaponInventory.weaponList){
				i.setWeaponMode();
			}
		}
	}

	@Override
	public void render(AbstractScreen screen) {
	    
	    if( ! inGame) {
	        screen.blit(Art.background, 0, 0);
	    } else {
	        screen.alphaFill(0, 0, gameWidth, gameHeight, 0xff000000, 0xC0);
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
