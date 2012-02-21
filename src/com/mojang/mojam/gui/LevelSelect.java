package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.screen.Screen;

public class LevelSelect extends GuiMenu {

	private ArrayList<LevelInformation> levels;
	private Button[] levelButtons;
	private int selectedIndex = 0;
	private int previousIndex = 0;
	private final int spacing = 15;
	private final int xStringPosition = 50;
	private final int yStringPosition = 50;
	
	private Button startGameButton;

	public LevelSelect(boolean bHosting) {
		super();
		levels = LevelList.getLevels();
		levelButtons = new Button[levels.size()];
		TitleMenu.level = levels.get(0).levelFile;
		setupLevelButtons();
		
		if (bHosting) {
			startGameButton = addButton(new Button(TitleMenu.HOST_GAME_ID, "start", true,
					125, 300));
		} else {
			startGameButton = addButton(new Button(TitleMenu.SELECT_DIFFICULTY_ID, "start", true,
					125, 300));
		}
		addButton(new Button(TitleMenu.CANCEL_JOIN_ID, "cancel", true, 275, 300));
		
		addButtonListener(this);
	}
	
	private void setupLevelButtons() {
		for (int i = 0; i < levels.size(); i++) {
			if(i == 0)
				levelButtons[i] = addButton(new Button(i, levels.get(i).levelName,
					xStringPosition + spacing, yStringPosition + spacing * i));
			else
				levelButtons[i] = addButton(new Button(i, levels.get(i).levelName,
						xStringPosition, yStringPosition + spacing * i));
		}
	}
	
	@Override
	public void render(Screen screen) {
		screen.clear(0);
		super.render(screen);
		Font.draw(screen, "Choose a level", 10, 10);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		previousIndex = selectedIndex;
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			selectedIndex--;
			if (selectedIndex < 0) {
				selectedIndex = levels.size() - 1;
			}
			
			updateSelectedButtons();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			selectedIndex++;
			if (selectedIndex >= levels.size()) {
				selectedIndex = 0;
			}
			
			updateSelectedButtons();
		} else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			startGameButton.postClick();
		}
		TitleMenu.level = levels.get(selectedIndex).levelFile;
	}
	
	@Override
	public void buttonPressed(Button button) {
		if(button.getId() >= 0 && button.getId() < levels.size()) {
			previousIndex = selectedIndex;
			TitleMenu.level = levels.get(button.getId()).levelFile;
			selectedIndex = button.getId();
			
			updateSelectedButtons();
		}
	}
	
	private void updateSelectedButtons() {
		levelButtons[previousIndex].setXPosition(xStringPosition);
		levelButtons[selectedIndex].setXPosition(xStringPosition + spacing);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
