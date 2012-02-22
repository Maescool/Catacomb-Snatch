package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.screen.Screen;

public class LevelSelect extends GuiMenu {

	private ArrayList<LevelInformation> levels;

	private LevelButton[] levelButtons;
	private final int xButtons = (MojamComponent.GAME_WIDTH / LevelButton.WIDTH);
	private final int yButtons = 3; // unused yet, need to add pages later.
	private final int xSpacing = LevelButton.WIDTH + 8;
	private final int ySpacing = LevelButton.HEIGHT + 8;
	private final int xStart = (MojamComponent.GAME_WIDTH - (xSpacing * xButtons) + 8) / 2;
	private final int yStart = 50;

	private LevelButton activeButton;
	private Button startGameButton;

	public LevelSelect(boolean bHosting) {

		super();

		// get all levels
		levels = LevelList.getLevels();

		// create buttons
		levelButtons = new LevelButton[levels.size()];
		setupLevelButtons();

		// -
		TitleMenu.level = levels.get(0).levelFile;

		// start + cancel button
		if (bHosting) {
			addButton(new Button(TitleMenu.HOST_GAME_ID, "Host", 125, 350));
		} else {
			addButton(new Button(TitleMenu.SELECT_DIFFICULTY_ID, "Start", 125, 350));
		}
		addButton(new Button(TitleMenu.CANCEL_JOIN_ID, "Cancel", 275, 350));
		addButtonListener(this);
	}

	private void setupLevelButtons() {
		int y = 0;
		for (int i = 0; i < levels.size(); i++) {
			int x = i % xButtons;

			levelButtons[i] = (LevelButton) addButton(new LevelButton(i, levels.get(i).levelName, levels.get(i).levelFile, xStart + x * xSpacing, yStart + ySpacing * y));
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
		screen.clear(0);
		super.render(screen);
		Font.draw(screen, "Choose a level", 10, 10);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {

		if (button instanceof LevelButton) {

			LevelButton lb = (LevelButton) button;
			TitleMenu.level = levels.get(lb.getId()).levelFile;

			if (activeButton != null && activeButton != lb) {
				activeButton.setActive(false);
			}

			activeButton = lb;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
