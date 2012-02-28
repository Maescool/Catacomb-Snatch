package com.mojang.mojam.campaign;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.gui.ClickableComponent;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * A Campaign is a button with a campaign picture drawn on it.
 */
public class CampaignButton extends ClickableComponent {
	public static final int WIDTH = 140;
	public static final int HEIGHT = 84;

	private int id;
	private Bitmap minimap;

    private final int MAX_LABEL_LENGTH = 15;
    
    private final String campaignName;

   	private boolean isActive = false;

   	// Background bitmaps for pressed/unpressed/inactive state
   	private static Bitmap background[] = new Bitmap[3];
   	
   	// Initialize background bitmaps
 	static {
 		background[0] = new Bitmap(WIDTH, HEIGHT);
 		background[0].fill(0, 0, WIDTH, HEIGHT, 0xff522d16);
 		background[0].fill(1, 1, WIDTH-2, HEIGHT-2, 0);
 		background[1] = new Bitmap(WIDTH, HEIGHT);
 		background[1].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
 		background[1].fill(1, 1, WIDTH-2, HEIGHT-2, 0);
 		background[2] = new Bitmap(WIDTH, HEIGHT);
 		background[2].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
 		background[2].fill(1, 1, WIDTH-2, HEIGHT-2, 0xff3a210f);
 	}
 	
 	public CampaignButton(int id, LevelInformation campaignInfo, int x, int y) {
		super(x, y, WIDTH, HEIGHT);

		this.id = id;
		this.campaignName = campaignInfo.levelName;
	}
 	
 	public int getId() {
		return id;
	}
 	
 	@Override
	public void render(Screen screen) {

		// Render background
		screen.blit(background[isPressed() ? 1 : (isActive ? 2 : 0)], getX(), getY());
		
		// Render minimap
		if (minimap != null) {
			screen.blit(minimap, getX() + (getWidth() - minimap.w) / 2, getY() + 4);

			// map name
			Font.drawCentered(screen, trimToFitButton(campaignName), getX() + getWidth() / 2, getY() + 4 + minimap.h + 8);
		} else {
			Font.drawCentered(screen, trimToFitButton(campaignName), getX() + getWidth() / 2, getY() + HEIGHT - 16);
		}
	}

	@Override
	protected void clicked(MouseButtons mouseButtons) {
		isActive = true;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	/**
	 * Return a version of the string which fits into the button,
	 * trimming it in case it should be too long
	 * 
	 * @param label Label text
	 * @return Adapted string
	 */
	public String trimToFitButton(String label) {
		if (label.length() > MAX_LABEL_LENGTH) {
			return label.substring(0, MAX_LABEL_LENGTH - 2) + "...";
		}
		else {
			return label;
		}
	}
}
