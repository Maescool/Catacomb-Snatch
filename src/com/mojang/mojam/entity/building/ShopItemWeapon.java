package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.entity.weapon.Raygun;
import com.mojang.mojam.entity.weapon.Rifle;
import com.mojang.mojam.entity.weapon.Shotgun;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Generic shop item, available from the players base
 */
public class ShopItemWeapon extends Building {

    //private int facing = 0;
    public static final int SHOP_RIFLE = 0;
    public static final int SHOP_SHOTGUN = 1;
    public static final int SHOP_RAYGUN = 2;
    public static final int[] YOFFS = { 7, 7, 7};
    public static final int[] COST = {0, 500, 1000};
    private final int type;
    private int effectiveCost;
    
    private final String[][] TOOLTIPS = { 
            MojamComponent.texts.shopTooltipLines("rifle"),
            MojamComponent.texts.shopTooltipLines("shotgun"),
            MojamComponent.texts.shopTooltipLines("raygun"),
    };

	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param team Team number
	 */
    public ShopItemWeapon(double x, double y, int type, int team) {
        super(x, y, team);
        this.type = type;
        isImmortal = true;
        if (team == Team.Team1) {
            facing = 4;   
        }
        setBuildingCost();
        yOffs = YOFFS[type];
    }
    
    /**
     * Set building cost depending if creative mode is on or not
     */
    public void setBuildingCost(){
    	if(Options.getAsBoolean(Options.CREATIVE)){
    		COST[0] = 0;
    		COST[1] = 0;
    		COST[2] = 0;
    	}else{
    		COST[0] = 0;
    		COST[1] = 500;
    		COST[2] = 1000;
    	}
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);
        if(team == MojamComponent.localTeam) {
            Font.defaultFont().drawCentered(screen, MojamComponent.texts.cost(effectiveCost), (int) (pos.x), (int) (pos.y + 10));
        }
    }

    @Override
    public void init() {
        effectiveCost = DifficultyInformation.calculateCosts(COST[type]);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public Bitmap getSprite() {
        switch (type) {
            case SHOP_RIFLE:
                return Art.rifle[0][0];
            case SHOP_SHOTGUN:
                return Art.rifle[0][0];
            case SHOP_RAYGUN:
                return Art.rifle[0][0];
        }
        return Art.rifle[0][0];
    }
    
    /**
     * Get tool tip
     * 
     * @return Tool tip
     */
    public String[] getTooltip() {
        return TOOLTIPS[type];
    }

    @Override
    public void use(Entity user) {
        if (user instanceof Player && ((Player) user).getTeam() == team) {
            Player player = (Player) user;
            if (player.getScore() >= effectiveCost) {
                player.payCost(effectiveCost);
                switch (type) {
                    case SHOP_RIFLE:
                    	player.weapon = new Rifle(player);
                        break;
                    case SHOP_SHOTGUN:
                    	player.weapon = new Shotgun(player);
                        break;
                    case SHOP_RAYGUN:
                    	player.weapon = new Raygun(player);
                        break;
                }

            } else if (player.getScore() < effectiveCost) {
            	if(this.team == MojamComponent.localTeam) {
            		 Notifications.getInstance().add(MojamComponent.texts.upgradeNotEnoughMoney(effectiveCost));
            	}
               
            }
        }
    }
}
