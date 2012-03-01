package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.List;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class LevelSelect extends GuiMenu {
    
    private final int LEVELS_PER_PAGE = 9;
    
	private List<LevelInformation> levels;

    private int currentPage = 0;
	private LevelButton[] levelButtons = null;
	
	private final int xButtons = (MojamComponent.GAME_WIDTH / LevelButton.WIDTH);
	private final int xSpacing = LevelButton.WIDTH + 8;
	private final int ySpacing = LevelButton.HEIGHT + 8;
	private final int xStart = (MojamComponent.GAME_WIDTH - (xSpacing * xButtons) + 8) / 2;
	private final int yStart = 50;

	private LevelButton activeButton;
	
	private Button startGameButton;
	private Button cancelButton;
    private Button previousPageButton;
    private Button nextPageButton;
    private boolean outdatedLevelButtons = false;
	
	public boolean bHosting;
	
	public LevelSelect(boolean bHosting) {
		super();		
		this.bHosting = bHosting;
		
		// Get all levels
		LevelList.resetLevels();
		levels = LevelList.getLevels();
		TitleMenu.level = levels.get(0);

		// Add main buttons
		startGameButton = (Button) addButton(new Button(bHosting ? TitleMenu.SELECT_DIFFICULTY_HOSTING_ID : 
			TitleMenu.SELECT_DIFFICULTY_ID, MojamComponent.texts.getStatic("levelselect.start"), 
			MojamComponent.GAME_WIDTH - 256 - 30, MojamComponent.GAME_HEIGHT - 24 - 25));
		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID, MojamComponent.texts.getStatic("cancel"), 
				MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 24 - 25));
		/*addButton(new Button(TitleMenu.UPDATE_LEVELS, MojamComponent.texts.getStatic("levelselect.update"), 
				MojamComponent.GAME_WIDTH - 128 - 18, 20));
		 //levels already load by default, no update needed
		*/

		// Add page buttons
		if (levels.size() > LEVELS_PER_PAGE) {
	        previousPageButton = (Button) addButton(new Button(TitleMenu.LEVELS_PREVIOUS_PAGE_ID, "(", 
	                xStart, MojamComponent.GAME_HEIGHT - 24 - 25, 30, Button.BUTTON_HEIGHT));
	        nextPageButton = (Button) addButton(new Button(TitleMenu.LEVELS_PREVIOUS_PAGE_ID, ")", 
	                xStart + 40, MojamComponent.GAME_HEIGHT - 24 - 25, 30, Button.BUTTON_HEIGHT));
		}
        
        // Create level
		goToPage(0);
	}

	private void goToPage(int page) {
        currentPage = page;
        outdatedLevelButtons = true;
    }

    private void updateLevelButtons() {
    	int y = 0;
    	
    	// Remove previous buttons
    	if (levelButtons != null) {
            for (int i = 0; i < levelButtons.length; i++) {
                if (levelButtons[i] != null) {
                    removeButton(levelButtons[i]);
                }
            }
    	}
    	
    	// Create level buttons
        levelButtons = new LevelButton[Math.min(LEVELS_PER_PAGE,
                levels.size() - currentPage * LEVELS_PER_PAGE)];
    	for (int i = currentPage * LEVELS_PER_PAGE;
    	         i < Math.min((currentPage + 1) * LEVELS_PER_PAGE, levels.size());
    	         i++) {
    		int x = i % xButtons;
    		int buttonIndex = i % LEVELS_PER_PAGE;
    		
    		levelButtons[buttonIndex] = (LevelButton) addButton(new LevelButton(i, levels.get(i), 
    		        xStart + x * xSpacing, yStart + ySpacing * y));
    		if (buttonIndex == 0) {
    			activeButton = levelButtons[buttonIndex];
    			activeButton.setActive(true);
    		}
    
    		if (x == (xButtons - 1)) {
    			y++;
    		}
    	}
    }

    private boolean hasPreviousPage() {
	    return currentPage > 0;
	}
	
    private boolean hasNextPage() {
        return (currentPage + 1) * LEVELS_PER_PAGE < levels.size();
    }
	
    @Override
    public void tick(MouseButtons mouseButtons) {
        super.tick(mouseButtons);
        if (outdatedLevelButtons) {
            updateLevelButtons();
            outdatedLevelButtons = false;
        }
    }
    
    @Override
    public void render(Screen screen) {
    	screen.blit(Art.emptyBackground, 0, 0);
    	super.render(screen);
    	Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("levelselect.title"), 20, 20);
    	
    	// Draw disabled page buttons
    	if (levels.size() > LEVELS_PER_PAGE) {
	    	if (!hasPreviousPage()) {
	    	    previousPageButton.render(screen);
	    	    screen.fill(previousPageButton.getX() + 4, previousPageButton.getY() + 4,
	    	            previousPageButton.getWidth() - 8, previousPageButton.getHeight() - 8, 0x75401f);
	    	}
	        if (!hasNextPage()) {
	            nextPageButton.render(screen);
	            screen.fill(nextPageButton.getX() + 4, nextPageButton.getY() + 4,
	                    nextPageButton.getWidth() - 8, nextPageButton.getHeight() - 8, 0x75401f);
	        }
    	}
    	
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
    	
    	else if (button == previousPageButton && hasPreviousPage()) {
    	    goToPage(currentPage - 1);
    	}
    	
        else if (button == nextPageButton && hasNextPage()) {
            goToPage(currentPage + 1);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    	if (e.getKeyCode() == KeyEvent.VK_PAGE_UP && hasPreviousPage()) {
    		goToPage(currentPage - 1);
    	}
    	else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN && hasNextPage()) {
    		goToPage(currentPage + 1);
    	}
    	else {
    	
	        // Compute new id
	        int activeButtonId = activeButton.getId();
	    	int nextActiveButtonId = -2;
	    	if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
	    		nextActiveButtonId = bestExistingLevelId(activeButtonId - 1, currentPage * LEVELS_PER_PAGE + 8);
	    	}
	    	else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
	            nextActiveButtonId = bestExistingLevelId(activeButtonId + 1, currentPage * LEVELS_PER_PAGE);
	    	}
	    	else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
	    		nextActiveButtonId = bestExistingLevelId(activeButtonId - 3, activeButtonId + 6, activeButtonId + 3);
	    	}
	    	else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
	    		nextActiveButtonId = bestExistingLevelId(activeButtonId + 3, activeButtonId - 6, activeButtonId - 3);
	    	}
	    
	    	// Update active button
	    	if (nextActiveButtonId >= 0 && nextActiveButtonId < levelButtons.length) {
	    		activeButton.setActive(false);
	    		activeButton = levelButtons[nextActiveButtonId];
	    		activeButton.setActive(true);
	    		activeButton.postClick();
	    	}
	    
	    	// Start on Enter, Cancel on Escape
	    	if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
	    		startGameButton.postClick();
	    	}
	    	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	    		cancelButton.postClick();
	    	}
    	
    	}
    	
    }

    public int bestExistingLevelId(int... options) {
    	for (int option : options) {
    		if (option >= 0 && option < levels.size()) {
        		return option % LEVELS_PER_PAGE;
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
