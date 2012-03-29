package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.Checkbox;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractScreen;

public class DifficultySelect extends GuiMenu {
	
	private static final int DEFAULT_DIFFICULTY = 1;
	
	private Checkbox[] DifficultyCheckboxes;
	private final int xButtons = 3;
	private final int xSpacing = Checkbox.WIDTH + 8;
	private final int ySpacing = Checkbox.HEIGHT + 8;
	private final int xStart = (MojamComponent.GAME_WIDTH - (xSpacing * xButtons)) / 2;
	private final int yStart = 75;
	
	private Button startGameButton;
	private Button cancelButton;

	public DifficultySelect(boolean hosting) {
		super();
		
		DifficultyCheckboxes = new Checkbox[DifficultyInformation.values().length];
		setupDifficultyButtons();
		
		TitleMenu.difficulty = DifficultyInformation.getByInt(DEFAULT_DIFFICULTY);
		
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
        int counter = 0;
        for (DifficultyInformation curInfo: DifficultyInformation.values()) {
            counter = curInfo.ordinal();
        	int x = counter % xButtons;
            
            DifficultyCheckboxes[counter] = (Checkbox) addButton(new Checkbox(counter, curInfo.getDifficultyName(), xStart + x * xSpacing, yStart + ySpacing * y));
            
            if (counter == DEFAULT_DIFFICULTY) {
                DifficultyCheckboxes[counter].checked = true;
            }
        
            if (x == (xButtons - 1))
                y++;
        }
	}

	@Override
	public void render(AbstractScreen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("diffselect.title"), 20, 20);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		if (button instanceof Checkbox) {

		    Checkbox cb = (Checkbox) button;
			TitleMenu.difficulty = DifficultyInformation.getByInt(cb.getId());
			
			checkOnlyOne(cb);
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
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId - 1, DifficultyInformation.values().length - 1);
		}else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId + 1, 0);
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId - 3, activeButtonId + 6, activeButtonId + 3);
		}else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			nextActiveButtonId = bestExistingDifficultyId(activeButtonId + 3, activeButtonId - 6, activeButtonId - 3);
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			startGameButton.postClick();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();
		} else {
			super.keyPressed(e);
		}
		
		// Update active button
		if (nextActiveButtonId >= 0 && nextActiveButtonId < DifficultyCheckboxes.length) {
            checkOnlyOne(DifficultyCheckboxes[nextActiveButtonId]);
		}	
	}
	
	public int bestExistingDifficultyId(int... options) {
		for (int option : options) {
			if (option >= 0 && option < DifficultyInformation.values().length) {
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
