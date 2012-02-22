package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.DifficultyList;
import com.mojang.mojam.screen.Screen;

public class DifficultySelect extends GuiMenu {
	private ArrayList<DifficultyInformation> difficulties = DifficultyList.getDifficulties();
	private int selectedIndex = 1;
	private final int spacing = 15;
	private final int xStringPosition = 50;
	private final int yStringPosition = 50;
	private Button startGameButton;
	private Button cancelButton;

	public DifficultySelect() {
		super();
		startGameButton = (Button) addButton(new Button(TitleMenu.START_GAME_ID, "Start Game", 125, 350));
		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID, "Cancel", 275, 350));
	}

	@Override
	public void render(Screen screen) {
		screen.clear(0);
		drawDifficulty(screen);
		super.render(screen);
		Font.draw(screen, "Choose a difficulty", 10, 10);
	}

	private void drawDifficulty(Screen screen) {
		for (int i = 0; i < difficulties.size(); i++) {
			int xpos = xStringPosition;
			if (selectedIndex == i)
				xpos += spacing;
			int ypos = yStringPosition + spacing * i;
			Font.draw(screen, difficulties.get(i).difficultyName, xpos, ypos);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			selectedIndex--;
			if (selectedIndex < 0) {
				selectedIndex = difficulties.size() - 1;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			selectedIndex++;
			if (selectedIndex >= difficulties.size()) {
				selectedIndex = 0;
			}
		} else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			startGameButton.postClick();
		} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();
		}
		TitleMenu.difficulty = difficulties.get(selectedIndex);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// nothing.
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// nothing.
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing.
	}

}
