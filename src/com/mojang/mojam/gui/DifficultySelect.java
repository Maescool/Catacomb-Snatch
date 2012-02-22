package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.DifficultyList;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class DifficultySelect extends GuiMenu {
	
	private ArrayList<DifficultyInformation> Difficulties = DifficultyList.getDifficulties();
	
	private DifficultyButton[] DifficultyButtons;
	private final int xButtons = 3;
	private final int xSpacing = DifficultyButton.WIDTH + 8;
	private final int ySpacing = DifficultyButton.HEIGHT + 8;
	private final int xStart = (MojamComponent.GAME_WIDTH - (xSpacing * xButtons) + 8) / 2;
	private final int yStart = 50;
	
	private DifficultyButton activeButton;
	private Button startGameButton;
	private Button cancelButton;

	public DifficultySelect() {
		super();
		
		DifficultyButtons = new DifficultyButton[Difficulties.size()];
		setupDifficultyButtons();
		
		TitleMenu.Difficulty = Difficulties.get(0).DifficultyNumber;
		
		startGameButton = new Button(TitleMenu.START_GAME_ID,  "Start Game", MojamComponent.GAME_WIDTH - 256 - 30, MojamComponent.GAME_HEIGHT - 24 - 25);
		cancelButton = new Button(TitleMenu.CANCEL_JOIN_ID, "Cancel", MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25);
		
		addButton(startGameButton);
		addButton(cancelButton);
		addButtonListener(this);
	}
	
	private void setupDifficultyButtons() {
		int y = 0;
		for (int i = 0; i < Difficulties.size(); i++) {
			int x = i % xButtons;

			DifficultyButtons[i] = (DifficultyButton) addButton(new DifficultyButton(i, Difficulties.get(i).DifficultyName, xStart + x * xSpacing, yStart + ySpacing * y));
			if (i == 0) {
				activeButton = DifficultyButtons[i];
				activeButton.setActive(true);
			}

			if (x == (xButtons - 1))
				y++;
		}
	}
	

	@Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.draw(screen, "Choose a difficulty", 20, 20);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {

		if (button instanceof DifficultyButton) {

			DifficultyButton DB = (DifficultyButton) button;
			TitleMenu.Difficulty = Difficulties.get(DB.getId()).DifficultyNumber;

			if (activeButton != null && activeButton != DB) {
				activeButton.setActive(false);
			}

			activeButton = DB;
		}
	}


	@Override
	public void keyPressed(KeyEvent e) {

		// Compute new id
		int activeButtonId = activeButton.getId();
		int nextActiveButtonId = -2;
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			nextActiveButtonId = (activeButtonId % 3 == 0)
					? bestExistingDifficultyId(activeButtonId + 2, activeButtonId + 1)
				    : activeButtonId - 1;
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (activeButtonId == Difficulties.size() - 1) {
				nextActiveButtonId = activeButtonId - (activeButtonId % 3);
			}
			else {
				nextActiveButtonId = (activeButtonId % 3 == 2) ? activeButtonId - 2 : activeButtonId + 1;
			}
		}
		
		// Update active button
		if (nextActiveButtonId >= 0 && nextActiveButtonId < DifficultyButtons.length) {
			activeButton.setActive(false);
			activeButton = DifficultyButtons[nextActiveButtonId];
			activeButton.setActive(true);
		}

		// Start on Enter, Cancel on Escape
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			startGameButton.postClick();
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();
		}
		
	}
	
	public int bestExistingDifficultyId(int... options) {
		for (int option : options) {
			if (option >= 0 && option < Difficulties.size()) {
				return option;
			}
		}
		return -2;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
