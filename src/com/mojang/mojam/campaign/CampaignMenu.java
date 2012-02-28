package com.mojang.mojam.campaign;

import java.awt.event.KeyEvent;
import java.util.List;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.gui.Button;
import com.mojang.mojam.gui.ClickableComponent;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.GuiMenu;
import com.mojang.mojam.gui.Panel;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.screen.Screen;

public class CampaignMenu extends GuiMenu{
	
	private final int CAMPAIGNS_PER_PAGE = 3;
	
	private List<LevelInformation> levels;
	
	private int currentPage = 0;
	private CampaignButton[] campaignButtons = null;
	
	private final int buttons = (MojamComponent.GAME_WIDTH / CampaignButton.WIDTH);
	private final int spacing = CampaignButton.HEIGHT + 16;
	private final int start = 40;

	private Panel listPanel;
	private Panel detailPanel;
	private Panel buttonPanel;
	
	private CampaignButton activeButton;
	
	private Button startGameButton;
	private Button cancelButton;
    private Button previousPageButton;
    private Button nextPageButton;
    private boolean outdatedLevelButtons = false;

	public CampaignMenu(int gameWidth, int gameHeight) {
		super();
		
		// Get all levels
		LevelList.resetLevels();
		levels = LevelList.getLevels();
		TitleMenu.level = levels.get(0);

		// Add background Panels
		listPanel = (Panel) addButton(new Panel(0,0,160, MojamComponent.GAME_HEIGHT));
		detailPanel = (Panel) addButton(new Panel(160,0,MojamComponent.GAME_WIDTH-160, MojamComponent.GAME_HEIGHT-45));
		buttonPanel = (Panel) addButton(new Panel(160, MojamComponent.GAME_HEIGHT-45, MojamComponent.GAME_WIDTH-160, 45));
		
		// Add main buttons
		startGameButton = (Button) addButton(new Button(TitleMenu.SELECT_DIFFICULTY_ID, MojamComponent.texts.getStatic("levelselect.start"), 160 + 10, MojamComponent.GAME_HEIGHT - 35, 128));
		cancelButton = (Button) addButton(new Button(TitleMenu.CANCEL_JOIN_ID, MojamComponent.texts.getStatic("cancel"), MojamComponent.GAME_WIDTH - 128 - 20, MojamComponent.GAME_HEIGHT - 35, 128));

		// Add pagination buttons
		if (levels.size() > CAMPAIGNS_PER_PAGE) {
	        previousPageButton = (Button) addButton(new Button(TitleMenu.LEVELS_PREVIOUS_PAGE_ID, "(", 10, MojamComponent.GAME_HEIGHT - 35, 30));
	        nextPageButton = (Button) addButton(new Button(TitleMenu.LEVELS_PREVIOUS_PAGE_ID, ")", 10 + 110, MojamComponent.GAME_HEIGHT - 35, 30));
		}
        
        // Create level
		goToPage(0);
		
		addButtonListener(this);
	}

	private void goToPage(int page) {
        currentPage = page;
        outdatedLevelButtons = true;
    }

    private void updateLevelButtons() {
    	
    	// Remove previous buttons
    	if (campaignButtons != null) {
            for (int i = 0; i < campaignButtons.length; i++) {
                if (campaignButtons[i] != null) {
                    removeButton(campaignButtons[i]);
                }
            }
    	}
    	
    	// Create level buttons
    	campaignButtons = new CampaignButton[Math.min(CAMPAIGNS_PER_PAGE,
                levels.size() - currentPage * CAMPAIGNS_PER_PAGE)];
    	for (int i = currentPage * CAMPAIGNS_PER_PAGE;
    	         i < Math.min((currentPage + 1) * CAMPAIGNS_PER_PAGE, levels.size());
    	         i++) {
    		int y = i % buttons;
    		int buttonIndex = i % CAMPAIGNS_PER_PAGE;
    		
    		campaignButtons[buttonIndex] = (CampaignButton) addButton(new CampaignButton(i, levels.get(i), 
    		        10, start + spacing * y));
    		if (buttonIndex == 0) {
    			activeButton = campaignButtons[buttonIndex];
    			activeButton.setActive(true);
    		}
    		
    		y++;
    	}
    }

    private boolean hasPreviousPage() {
	    return currentPage > 0;
	}
	
    private boolean hasNextPage() {
        return (currentPage + 1) * CAMPAIGNS_PER_PAGE < levels.size();
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
    	screen.clear(0);
    	super.render(screen);
    	Font.drawCentered(screen, MojamComponent.texts.getStatic("campaignmenu.title"), 80, 20);
    	
    	// Draw disabled page buttons
    	if (levels.size() > CAMPAIGNS_PER_PAGE) {
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
		if (button instanceof CampaignButton) {
		    
    		CampaignButton lb = (CampaignButton) button;
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
	    	if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
	    		nextActiveButtonId = bestExistingLevelId(activeButtonId - 1, currentPage * CAMPAIGNS_PER_PAGE + 8);
	    	} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
	            nextActiveButtonId = bestExistingLevelId(activeButtonId + 1, currentPage * CAMPAIGNS_PER_PAGE);
	    	} else if (e.getKeyCode() == KeyEvent.VK_LEFT && hasPreviousPage() || e.getKeyCode() == KeyEvent.VK_A && hasPreviousPage()) {
	    		goToPage(currentPage -1);
	    	} else if (e.getKeyCode() == KeyEvent.VK_RIGHT && hasNextPage() || e.getKeyCode() == KeyEvent.VK_D && hasNextPage()) {
	    		goToPage(currentPage + 1);
	    	}
	    
	    	// Update active button
	    	if (nextActiveButtonId >= 0 && nextActiveButtonId < campaignButtons.length) {
	    		activeButton.setActive(false);
	    		activeButton = campaignButtons[nextActiveButtonId];
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
			if (option >= 0 && option < levels.size()) return option % CAMPAIGNS_PER_PAGE;
		}
		return -2;
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
