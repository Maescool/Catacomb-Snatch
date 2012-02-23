package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.DifficultyList;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class DifficultySelect extends GuiMenu {
	
	private ArrayList<DifficultyInformation> difficulties = DifficultyList.getDifficulties();
	
	private DifficultyButton[] DifficultyButtons;
	private final int xButtons = 3;
	private final int xSpacing = DifficultyButton.WIDTH + 8;
	private final int ySpacing = DifficultyButton.HEIGHT + 8;
	private final int xStart = (MojamComponent.GAME_WIDTH - (xSpacing * xButtons)) / 2;
	private final int yStart = 75;
	
	private DifficultyButton activeButton;
	private Button startGameButton;
	private Button cancelButton;

	public DifficultySelect(boolean hosting) {
		super();
		
		DifficultyButtons = new DifficultyButton[difficulties.size()];
		setupDifficultyButtons();
		
		TitleMenu.difficulty = difficulties.get(0);
		
		startGameButton = new Button(hosting ? TitleMenu.HOST_GAME_ID : TitleMenu.START_GAME_ID,  "Start Game", (MojamComponent.GAME_WIDTH - 256 - 30), MojamComponent.GAME_HEIGHT - 24 - 25);
		cancelButton = new Button(TitleMenu.CANCEL_JOIN_ID, "Cancel", MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25);
		
		addButton(startGameButton);
		addButton(cancelButton);
		addButtonListener(this);
	}
	
	private void setupDifficultyButtons() {
		int y = 0;
		for (int i = 0; i < difficulties.size(); i++) {
			int x = i % xButtons;

			DifficultyButtons[i] = (DifficultyButton) addButton(new DifficultyButton(i, difficulties.get(i).difficultyName, xStart + x * xSpacing, yStart + ySpacing * y));
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
			TitleMenu.difficulty = difficulties.get(DB.getId());

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
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId - 1, difficulties.size() - 1);
		}else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId + 1, 0);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId - 3, activeButtonId + 6, activeButtonId + 3);
		}else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId + 3, activeButtonId - 6, activeButtonId - 3);
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
			if (option >= 0 && option < difficulties.size()) {
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
