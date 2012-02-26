package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.level.CreativeSettingsList;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.DifficultyList;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class DifficultySelect extends GuiMenu {
	
	private static final int DEFAULT_DIFFICULTY = 1;
	
	private ArrayList<DifficultyInformation> difficulties = DifficultyList.getDifficulties();
	private String[] creativeSettings = CreativeSettingsList.getCreativeSettings();
	
	private Checkbox[] DifficultyCheckboxes;
	private Checkbox[] CreativeModeCheckboxes;
	
	private final int xButtons = 3;
	private final int xSpacing = Checkbox.WIDTH + 8;
	private final int ySpacing = Checkbox.HEIGHT + 8;
	private final int xStart = (MojamComponent.GAME_WIDTH - (xSpacing * xButtons)) / 2;
	private final int yStart = 75;
	
	private Button startGameButton;
	private Button cancelButton;

	public DifficultySelect(boolean hosting) {
		super();
		
		DifficultyCheckboxes = new Checkbox[difficulties.size()];
		CreativeModeCheckboxes = new Checkbox[creativeSettings.length];
		
		setupDifficultyButtons();
		
		// Add creative mode options if activated
		if(Options.getAsBoolean(Options.CREATIVE))
			setupCreativeModeButtons();
		
		TitleMenu.difficulty = difficulties.get(DEFAULT_DIFFICULTY);
		
		startGameButton = new Button(hosting ? TitleMenu.HOST_GAME_ID : TitleMenu.START_GAME_ID,  
				MojamComponent.texts.getStatic("diffselect.start"), (MojamComponent.GAME_WIDTH - 256 - 30), 
				MojamComponent.GAME_HEIGHT - 24 - 25);
		cancelButton = new Button(TitleMenu.CANCEL_JOIN_ID, MojamComponent.texts.getStatic("cancel"), 
				MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25);
		
		addButton(startGameButton);
		addButton(cancelButton);
		addButtonListener(this);
	}
	
	private void setupDifficultyButtons() {
		int y = 0;

        for (int i = 0; i < difficulties.size(); i++) {
            int x = i % xButtons;
            
            DifficultyCheckboxes[i] = (Checkbox) addButton(new Checkbox(i, difficulties.get(i).difficultyName, xStart + x * xSpacing, yStart + ySpacing * y));
            
            if (i == DEFAULT_DIFFICULTY) {
                DifficultyCheckboxes[i].checked = true;
            }
        
            if (x == (xButtons - 1))
                y++;
        }
	}
	
	/**
	 * Add checkboxes for creative mode
	 */
	private void setupCreativeModeButtons()
	{
        for (int i = 0; i < creativeSettings.length; i++) {
       
        	CreativeModeCheckboxes[i] = (Checkbox) addButton(
        			new Checkbox(i + difficulties.size(), MojamComponent.texts.getStatic(creativeSettings[i]),
        					xStart, yStart + ySpacing * (i + 3)));
            
            if (Options.getAsBoolean(creativeSettings[i])) {
            	CreativeModeCheckboxes[i].checked = true;
            }
        }
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.draw(screen, MojamComponent.texts.getStatic("diffselect.title"), 20, 20);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		if (button instanceof Checkbox) {
		    Checkbox cb = (Checkbox) button;
		    
		    // Handle difficulties
		    if(cb.getId() < difficulties.size()){
		    	TitleMenu.difficulty = difficulties.get(cb.getId());
			
		    	checkOnlyOne(cb);
		    }
		    else
		    {
		    	// Handle creative mode settings
		    	CreativeModeCheckboxes[cb.getId() - difficulties.size()].checked = !CreativeModeCheckboxes[cb.getId() - difficulties.size()].checked;
		    	
		    	Options.set(creativeSettings[cb.getId() - difficulties.size()], CreativeModeCheckboxes[cb.getId() - difficulties.size()].checked);
		    }
		    
		    Options.saveProperties();
		}
	}
    
    public Checkbox getActiveCheckbox()
    {
        for(Checkbox box : DifficultyCheckboxes) {
            if(box.checked == true) {
                return box;
            }   
        } 
        return null;
    }
	
	public void checkOnlyOne(Checkbox active)
	{
        for(Checkbox box : DifficultyCheckboxes) {
            if(active.getId() == box.getId()) {
                box.checked = true;
            } else {
                box.checked = false;
            }
        } 
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// Compute new id
		int activeButtonId = getActiveCheckbox().getId();
		
		
		int nextActiveButtonId = -2;
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId - 1, difficulties.size() - 1);
		}else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId + 1, 0);
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId - 3, activeButtonId + 6, activeButtonId + 3);
		}else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId + 3, activeButtonId - 6, activeButtonId - 3);
		}
		
		// Update active button
		if (nextActiveButtonId >= 0 && nextActiveButtonId < DifficultyCheckboxes.length) {
            checkOnlyOne(DifficultyCheckboxes[nextActiveButtonId]);
		}

		// Start on Enter, Cancel on Escape
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
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
}
