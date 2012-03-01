package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Generic shop item, available from the players base
 */
public class ShopItem extends Building {
	
    private EnumShopItem type;
    private final int cost;
    private int effectiveCost;

	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param type The type of item to buy
	 * @param team Team number
	 */
    public ShopItem(double x, double y, EnumShopItem type, int team) {
        super(x, y, team);
        this.type = type;
        //Set building cost depending if creative mode is on or not
    	cost = (Options.getAsBoolean(Options.CREATIVE)) ? 0:type.getCost();
        yOffs = type.getYOffset();
        isImmortal = true;
    }


    @Override
    public void render(Screen screen) {
        super.render(screen);
        if(team == MojamComponent.localTeam) {
            Font.defaultFont().drawCentered(screen, MojamComponent.texts.cost(effectiveCost), (int) (pos.x), (int) (pos.y + 10));           
        }
        renderInfo(screen);
    }
    

	/**
	 * Render the shop info text onto the given screen
	 * 
	 * @param screen
	 *            Screen
	 */
	protected void renderInfo(Screen screen) {
		// Draw iiAtlas' shop item info graphics, thanks whoever re-wrote this!
		if (highlight) {
		        Bitmap image = getSprite();
		        int teamYOffset = (team == 2) ? 90 : 0;
		        
		        String[] tooltip = this.getTooltip();
		        int width = getLongestWidth(tooltip, Font.FONT_WHITE_SMALL)+4;
		        int height = tooltip.length*(Font.FONT_GOLD_SMALL.getFontHeight()+3);
		        
		        Font font = Font.FONT_GOLD_SMALL;
		        screen.blit(Bitmap.tooltipBitmap(width, height),
                        (int)(pos.x - image.w / 2 - 10),
                        (int)(pos.y + 20 - teamYOffset), width, height);

		        for (int i=0; i<tooltip.length; i++) {
		            font.draw(screen, tooltip[i], (int)(pos.x - image.w + 8), (int)pos.y + 22 - teamYOffset + (i==0?0:1) + i*(font.getFontHeight()+2));
		            font = Font.FONT_WHITE_SMALL;
		        }
		}
	}
	
	private int getLongestWidth(String[] string, Font font) {
		int res = 0;
		for ( String s : string ) {
			int w = font.calculateStringWidth(s.trim());
			res = w > res ? w : res;
		}
		return res;
	}

    @Override
    public void init() {
        effectiveCost = DifficultyInformation.calculateCosts(cost);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public Bitmap getSprite() {
    	return type.getSprite();
    }
    
    /**
     * Get tool tip
     * 
     * @return Tool tip
     */
    public String[] getTooltip() {
        return MojamComponent.texts.shopTooltipLines(type.getItemName());
    }

    @Override
    public void use(Entity user) {
        if (user instanceof Player && ((Player) user).getTeam() == team) {
            Player player = (Player) user;
            if (!player.isCarrying() && player.getScore() >= effectiveCost) {
                player.payCost(effectiveCost);
                Building item = null;
                switch (type) {
                    case TURRET:
                        item = new Turret(pos.x, pos.y, team);
                        break;
                    case HARVESTER:
                        item = new Harvester(pos.x, pos.y, team);
                        break;
                    case BOMB:
                        item = new Bomb(pos.x, pos.y);
                        break;
                }
                level.addEntity(item);
                player.pickup(item);
            }
            else if (player.getScore() < effectiveCost) {
            	if(this.team == MojamComponent.localTeam) {
            		 Notifications.getInstance().add(MojamComponent.texts.upgradeNotEnoughMoney(effectiveCost));
            	}
               
            }
        }
    }
}
