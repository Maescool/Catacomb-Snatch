package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.List;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class LevelSelect extends GuiMenu {

	private List<LevelInformation> levels;

	private LevelButton[] levelButtons;
	private final int xButtons = (MojamComponent.GAME_WIDTH / LevelButton.WIDTH);
	//private final int yButtons = 3; // unused yet, need to add pages later.
	private final int xSpacing = LevelButton.WIDTH + 8;
	private final int ySpacing = LevelButton.HEIGHT + 8;
	private final int xStart = (MojamComponent.GAME_WIDTH - (xSpacing * xButtons) + 8) / 2;
	private final int yStart = 50;

	private LevelButton activeButton;
	private Button startGameButton;
	private Button cancelButton;
	private Button updateButton;
	public boolean bHosting;
	
	public LevelSelect(boolean bHosting) {

		super();
		this.bHosting = bHosting;
		
		// get all levels
		LevelList.resetLevels();
		levels = LevelList.getLevels();

		// create buttons
		levelButtons = new LevelButton[levels.size()];
		setupLevelButtons();

		// -
		TitleMenu.level = levels.get(0);

		// start + cancel button
		startGameButton = (Button) addButton(new Button(bHosting ? TitleMenu.SELECT_DIFFICULTY_HOSTING_ID : TitleMenu.SELECT_DIFFICULTY_ID, "Start", MojamComponent.GAME_WIDTH - 256 - 30, MojamComponent.GAME_HEIGHT - 24 - 25));
		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID, "Cancel", MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
		updateButton = (Button) addButton(new Button(TitleMenu.UPDATE_LEVELS, "Update Levels", MojamComponent.GAME_WIDTH - 386 - 40, MojamComponent.GAME_HEIGHT - 24 - 25));

		addButtonListener(this);
	}

	private void setupLevelButtons() {
		int y = 0;
		for (int i = 0; i < levels.size(); i++) {
			int x = i % xButtons;

			levelButtons[i] = (LevelButton) addButton(new LevelButton(i, levels.get(i), xStart + x * xSpacing, yStart + ySpacing * y));
			if (i == 0) {
				activeButton = levelButtons[i];
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
		Font.draw(screen, "Choose a level", 20, 20);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {

		if (button instanceof LevelButton) {

			LevelButton lb = (LevelButton) button;
			TitleMenu.level = levels.get(lb.getId());

			if (activeButton != null && activeButton != lb) {
				activeButton.setActive(false);
			}

			activeButton = lb;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

		// Compute new id
		int activeButtonId = activeButton.getId();
		int nextActiveButtonId = -2;
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			nextActiveButtonId = bestExistingLevelId(activeButtonId - 1, levels.size() - 1);
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            nextActiveButtonId = bestExistingLevelId(activeButtonId + 1, 0);
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP) {
			nextActiveButtonId = bestExistingLevelId(activeButtonId - 3, activeButtonId + 6, activeButtonId + 3);
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			nextActiveButtonId = bestExistingLevelId(activeButtonId + 3, activeButtonId - 6, activeButtonId - 3);
		}

		// Update active button
		if (nextActiveButtonId >= 0 && nextActiveButtonId < levelButtons.length) {
			activeButton.setActive(false);
			activeButton = levelButtons[nextActiveButtonId];
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
	
	public int bestExistingLevelId(int... options) {
		for (int option : options) {
			if (option >= 0 && option < levels.size()) {
				return option;
			}
		}
		return -2;
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
