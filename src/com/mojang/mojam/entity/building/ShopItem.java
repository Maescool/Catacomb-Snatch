package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Generic shop item, available from the players base
 */
public class ShopItem extends Building {

    private int facing = 0;
    public static final int SHOP_TURRET = 0;
    public static final int SHOP_HARVESTER = 1;
    public static final int SHOP_BOMB = 2;
    public static final int[] YOFFS = { 10, 22, 7 };
    public static final int[] COST = {150, 300, 500};
    private final int type;
    private int effectiveCost;
    
    private final String[][] TOOLTIPS = { 
            MojamComponent.texts.shopTooltipLines("turret"),
            MojamComponent.texts.shopTooltipLines("harvester"),
            MojamComponent.texts.shopTooltipLines("bomb")
    };

	/**
	 * Constructor
	 * 
	 * @param x Initial X coordinate
	 * @param y Initial Y coordinate
	 * @param team Team number
	 * @param localTeam Local team number
	 */
    public ShopItem(double x, double y, int type, int team, int localTeam) {
        super(x, y, team, localTeam);
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
    		COST[0] = 150;
    		COST[1] = 300;
    		COST[2] = 500;
    	}
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);
        if(team == localTeam) {
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
            case SHOP_TURRET:
                return Art.turret[facing][0];
            case SHOP_HARVESTER:
                return Art.harvester[facing][0];
            case SHOP_BOMB:
                return Art.bomb;
        }
        return Art.turret[facing][0];
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
            if (!player.isCarrying() && player.getScore() >= effectiveCost) {
                player.payCost(effectiveCost);
                Building item = null;
                switch (type) {
                    case SHOP_TURRET:
                        item = new Turret(pos.x, pos.y, team,localTeam);
                        break;
                    case SHOP_HARVESTER:
                        item = new Harvester(pos.x, pos.y, team, localTeam);
                        break;
                    case SHOP_BOMB:
                        item = new Bomb(pos.x, pos.y, localTeam);
                        break;
                }
                level.addEntity(item);
                player.pickup(item);
            } else if (player.getScore() < effectiveCost) {
            	if(this.team == this.localTeam) {
            		 Notifications.getInstance().add(MojamComponent.texts.upgradeNotEnoughMoney(effectiveCost));
            	}
               
            }
        }
    }
}
