package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;

/**
 * Generic shop item, available from the players base
 */
public abstract class ShopItem extends Building {

	private final String name;
	private AbstractBitmap image;
	private final int fullCost;
	private int cost;
	private int effectiveCost;

	public ShopItem(String name, double x, double y, int team, int cost, int yOffset) {
		super(x, y, team);
		this.name = name;
		// Set building cost depending if creative mode is on or not
		fullCost = cost;
		this.cost = (Options.getAsBoolean(Options.CREATIVE)) ? 0 : fullCost;
		yOffs = yOffset;
		image = null;
		isImmortal = true;
	}

	public static void updatePrices() {
		for (Entity e : MojamComponent.instance.player.level.entities) {
			if (e instanceof ShopItem) {
				ShopItem s = (ShopItem) e;
				s.updatePrice();
			}
		}
	}

	public void updatePrice() {
		cost = (Options.getAsBoolean(Options.CREATIVE)) ? 0 : fullCost;
		init();
	}

	@Override
	public void render(AbstractScreen screen) {
		super.render(screen);
		if (team == MojamComponent.localTeam) {
			// Render the Cost text
			Font.defaultFont().draw(screen,
					MojamComponent.texts.cost(effectiveCost), (int) (pos.x),
					(int) (pos.y + 10), Font.Align.CENTERED);
		}
	}
	
	@Override
	public void renderTop(AbstractScreen screen) {
		renderInfo(screen);
	}

	/**
	 * Render the shop info text onto the given screen
	 * 
	 * @param screen
	 *            AbstractScreen
	 */
	protected void renderInfo(AbstractScreen screen) {
		// Draw iiAtlas' shop item info graphics, thanks whoever re-wrote this!
		if (isHighlight()) {
			AbstractBitmap image = getSprite();

			String[] tooltip = this.getTooltip();
			int width = getLongestWidth(tooltip, Font.FONT_WHITE_SMALL) + 4;
			Font font = Font.FONT_GOLD_SMALL;
			int height = tooltip.length * (font.getFontHeight() + 3);

			screen.alphaBlit(screen.tooltipBitmap(width, height), (int) (pos.x - image.getWidth() / 2 - 10),
					(int) (pos.y - 50), 0x80);

			for (int i = 0; i < tooltip.length; i++) {
				font.draw(screen, tooltip[i], (int) (pos.x - image.getWidth() + 8),
						(int) pos.y - 48
						+ (i == 0 ? 0 : 1) + i
						* (font.getFontHeight() + 2));
				font = Font.FONT_WHITE_SMALL;
			}
		}
	}

	private String[] getTooltip() {
		return MojamComponent.texts.shopTooltipLines(name);
	}

	private int getLongestWidth(String[] string, Font font) {
		int res = 0;
		for (String s : string) {
			int w = font.calculateStringWidth(s.trim());
			res = w > res ? w : res;
		}
		return res;
	}

	@Override
	public void init() {
		effectiveCost = TitleMenu.difficulty.calculateCosts(cost);
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public AbstractBitmap getSprite() {
		return image;
	}

	public void setSprite(AbstractBitmap shopItemImage) {
		image = shopItemImage;
	}

	/**
	 * Action to take when when item is used. For most cases use useAction
	 * instead.
	 */
	@Override
	public void use(Entity user) {
		if (user instanceof Player && ((Player) user).getTeam() == team) {
			Player player = (Player) user;
			if (!player.isCarrying() && canBuy(player)
					&& player.getScore() >= effectiveCost) {
				player.payCost(effectiveCost);
				useAction(player);
			} else if (player.getScore() < effectiveCost) {
				if (this.team == MojamComponent.localTeam) {
					Notifications.getInstance().add(
							MojamComponent.texts
							.upgradeNotEnoughMoney(effectiveCost));
				}

			}
		}
	}

	/**
	 * Action to take when the user uses the ShopItem after cost has been
	 * deducted. Should be used in most cases Override use() if greater
	 * flexibility is needed
	 */
	abstract void useAction(Player player);

	/**
	 * Checks if the user can currently buy this (not checking money). E.g.
	 * check if maximum number of this item is in inventory or if a certain
	 * experience level is reached
	 * 
	 * @param player
	 *            the player trying to buy this
	 * @return true if he is allowed to buy
	 */
	abstract boolean canBuy(Player player);

	@Override
	public boolean upgrade(Player p) {
		if (this.team == MojamComponent.localTeam) {
			Notifications.getInstance().add(
					MojamComponent.texts.getStatic("upgrade.shopItem"));
		}
		return false;
	}

}
