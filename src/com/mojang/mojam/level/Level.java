package com.mojang.mojam.level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.level.gamemode.ILevelTickItem;
import com.mojang.mojam.level.gamemode.IVictoryConditions;
import com.mojang.mojam.level.gamemode.events.Event;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.level.tile.WallTile;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.math.Vec2;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Level {
	public int TARGET_SCORE = 100;

	public final int width, height;

	public Tile[] tiles;
	public List<Entity>[] entityMap;
	public List<Entity> entities = new ArrayList<Entity>();
	private Bitmap minimap;
	private boolean seen[];
	final int[] neighbourOffsets;

	public List<Event> events;
	
	public List<ILevelTickItem> tickItems = new ArrayList<ILevelTickItem>();;
	public int maxMonsters;

	public IVictoryConditions victoryConditions;
	public int player1Score = 0;
	public int player2Score = 0;
	
	public int player1Character;
	public int player2Character;

	@SuppressWarnings("unchecked")
	public Level(int width, int height, int player1Character, int player2Character) {
		neighbourOffsets = new int[] { -1, 1, -width, width };
		this.width = width;
		this.height = height;
		this.player1Character = player1Character;
		this.player2Character = player2Character;

		minimap = new Bitmap(width, height);

		tiles = new Tile[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Tile tile = new FloorTile();

				setTile(x, y, tile);
			}
		}

		entityMap = new List[width * height];
		for (int i = 0; i < width * height; i++) {
			entityMap[i] = new ArrayList<Entity>();
		}

		setSeen(new boolean[(width + 1) * (height + 1)]);

		events = new ArrayList<Event>();
		/*
		 * for (int i = 0; i < 10; i++) { double x = random.nextInt(width) *
		 * Tile.WIDTH + Tile.WIDTH / 2; double y = random.nextInt(height) *
		 * Tile.HEIGHT + Tile.HEIGHT / 2; addEntity(new SpawnerEntity(x, y,
		 * random.nextInt(Team.MaxTeams))); }
		 */
	}


	public void setTile(int x, int y, Tile tile) {
		final int index = x + y * width;
		tiles[index] = tile;
		tile.init(this, x, y);
		for (int of : neighbourOffsets) {
			final int nbIndex = index + of;
			if (nbIndex >= 0 && nbIndex < width * height) {
				final Tile neighbour = tiles[nbIndex];
				if (neighbour != null)
					neighbour.neighbourChanged(tile);
			}
		}
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return null;
		return tiles[x + y * width];
	}

	public Tile getTile(Vec2 pos) {
		int x = (int) pos.x / Tile.WIDTH;
		int y = (int) pos.y / Tile.HEIGHT;
		return getTile(x, y);
	}

	public void insertToEntityMap(Entity e) {
		e.xto = (int) (e.pos.x - e.radius.x) / Tile.WIDTH;
		e.yto = (int) (e.pos.y - e.radius.y) / Tile.HEIGHT;

		int x1 = e.xto + (int) (e.radius.x * 2 + 1) / Tile.WIDTH;
		int y1 = e.yto + (int) (e.radius.y * 2 + 1) / Tile.HEIGHT;

		for (int y = e.yto; y <= y1; y++) {
			if (y < 0 || y >= height)
				continue;
			for (int x = e.xto; x <= x1; x++) {
				if (x < 0 || x >= width)
					continue;
				entityMap[x + y * width].add(e);
			}
		}
	}

	public void removeFromEntityMap(Entity e) {
		int x1 = e.xto + (int) (e.radius.x * 2 + 1) / Tile.WIDTH;
		int y1 = e.yto + (int) (e.radius.y * 2 + 1) / Tile.HEIGHT;

		for (int y = e.yto; y <= y1; y++) {
			if (y < 0 || y >= height)
				continue;
			for (int x = e.xto; x <= x1; x++) {
				if (x < 0 || x >= width)
					continue;
				entityMap[x + y * width].remove(e);
			}
		}
	}

	public Set<Entity> getEntities(BB bb) {
		return getEntities(bb.x0, bb.y0, bb.x1, bb.y1);
	}

	public Set<Entity> getEntities(double xx0, double yy0, double xx1,
			double yy1) {
		int x0 = (int) (xx0) / Tile.WIDTH;
		int x1 = (int) (xx1) / Tile.WIDTH;
		int y0 = (int) (yy0) / Tile.HEIGHT;
		int y1 = (int) (yy1) / Tile.HEIGHT;

		Set<Entity> result = new TreeSet<Entity>(new EntityComparator());

		for (int y = y0; y <= y1; y++) {
			if (y < 0 || y >= height)
				continue;
			for (int x = x0; x <= x1; x++) {
				if (x < 0 || x >= width)
					continue;
				List<Entity> entities = entityMap[x + y * width];
				for (int i = 0; i < entities.size(); i++) {
					Entity e = entities.get(i);
					if (e.removed)
						continue;
					if (e.intersects(xx0, yy0, xx1, yy1)) {
						result.add(e);
					}
				}
			}
		}

		return result;
	}

	public Set<Entity> getEntities(BB bb, Class<? extends Entity> c) {
		return getEntities(bb.x0, bb.y0, bb.x1, bb.y1, c);
	}

	public Set<Entity> getEntities(double xx0, double yy0, double xx1,
			double yy1, Class<? extends Entity> c) {
		int x0 = (int) (xx0) / Tile.WIDTH;
		int x1 = (int) (xx1) / Tile.WIDTH;
		int y0 = (int) (yy0) / Tile.HEIGHT;
		int y1 = (int) (yy1) / Tile.HEIGHT;

		Set<Entity> result = new TreeSet<Entity>(new EntityComparator());

		for (int y = y0; y <= y1; y++) {
			if (y < 0 || y >= height)
				continue;
			for (int x = x0; x <= x1; x++) {
				if (x < 0 || x >= width)
					continue;
				List<Entity> entities = entityMap[x + y * width];
				for (int i = 0; i < entities.size(); i++) {
					Entity e = entities.get(i);
					if (!c.isInstance(e))
						continue;
					if (e.removed)
						continue;
					if (e.intersects(xx0, yy0, xx1, yy1)) {
						result.add(e);
					}
				}
			}
		}

		return result;
	}

	public Set<Entity> getEntitiesSlower(double xx0, double yy0, double xx1,
			double yy1, Class<? extends Entity> c) {
		Set<Entity> result = new TreeSet<Entity>(new EntityComparator());

		List<Entity> entities = this.entities;
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (!c.isInstance(e))
				continue;
			if (e.removed)
				continue;
			if (e.intersects(xx0, yy0, xx1, yy1)) {
				result.add(e);
			}
		}

		return result;
	}

	public void addEntity(Entity e) {
		e.init(this);
		entities.add(e);
		insertToEntityMap(e);
	}

	public void removeEntity(Entity e) {
		e.removed = true;
	}

	public void tick() {		
		for(int i = 0; i < tickItems.size(); i++) {
			tickItems.get(i).tick(this);
		}

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (!e.removed) {
				e.tick();

				int xtn = (int) (e.pos.x - e.radius.x) / Tile.WIDTH;
				int ytn = (int) (e.pos.y - e.radius.y) / Tile.HEIGHT;
				if (xtn != e.xto || ytn != e.yto) {
					removeFromEntityMap(e);
					insertToEntityMap(e);
				}
			}
			if (e.removed) {
				entities.remove(i--);
				removeFromEntityMap(e);
			}
		}
		if(victoryConditions != null)
			victoryConditions.updateVictoryConditions(this);

		updateEvents();
		Notifications.getInstance().tick();
	}

	private boolean hasSeen(int x, int y) {
		return getSeen()[x + y * (width + 1)] || getSeen()[(x + 1) + y * (width + 1)]
				|| getSeen()[x + (y + 1) * (width + 1)]
				|| getSeen()[(x + 1) + (y + 1) * (width + 1)];
	}
	
	private void updateEvents() {
		//create seperate array, so that events can be changed while itterated
		final List<Event> eventArray = events;
		for (int i = 0; i < eventArray.size(); i++) {
			eventArray.get(i).updateEvent();
		}
	}

	public void render(Screen screen, int xScroll, int yScroll) {
		int x0 = xScroll / Tile.WIDTH;
		int y0 = yScroll / Tile.HEIGHT;
		int x1 = (xScroll + screen.w) / Tile.WIDTH;
		int y1 = (yScroll + screen.h) / Tile.HEIGHT;
		if (xScroll < 0)
			x0--;
		if (yScroll < 0)
			y0--;

		Set<Entity> visibleEntities = getEntities(xScroll - Tile.WIDTH, yScroll
				- Tile.HEIGHT, xScroll + screen.w + Tile.WIDTH, yScroll
				+ screen.h + Tile.HEIGHT);
		// Sort the entities
		/*
		 * final int EntityBlitPixelRowOffset = -12; Set<Entity> visibleEntities
		 * = getEntities(xScroll - Tile.WIDTH, yScroll - Tile.HEIGHT, xScroll +
		 * screen.w + Tile.WIDTH, yScroll + screen.h + Tile.HEIGHT);
		 * ArrayList<Entity>[] rowEntities = new ArrayList[8 + y1 - y0]; // fuck
		 * // me... // And how to remove this warning (without supressing)? ^
		 * for (Entity e : visibleEntities) { int y = (int) ((e.pos.y -
		 * EntityBlitPixelRowOffset) / Tile.WIDTH) - y0 + 2; if (rowEntities[y]
		 * == null) rowEntities[y] = new ArrayList<Entity>();
		 * rowEntities[y].add(e); }
		 */

		screen.setOffset(-xScroll, -yScroll);

		for (int y = y0; y <= y1; y++) {
			for (int x = x0; x <= x1; x++) {
				if (x < 0 || x >= width || y < 0 || y >= height) {
					screen.blit(Art.floorTiles[5][0], x * Tile.WIDTH, y
							* Tile.HEIGHT);
					continue;
				}
				int xt = x - 28;
				int yt = y - 4;
				if (xt >= 0 && yt >= 0 && xt < 7 && yt < 4
						&& (xt != 3 || yt < 3)) {
					screen.blit(Art.getPlayerBase(player2Character)[xt][yt], x * Tile.WIDTH, y
							* Tile.HEIGHT);
					continue;
				}

				yt = y - (64 - 8);
				if (xt >= 0 && yt >= 0 && xt < 7 && yt < 4
						&& (xt != 3 || yt > 0)) {
					screen.blit(Art.getPlayerBase(player1Character)[xt][yt], x * Tile.WIDTH, y
							* Tile.HEIGHT);
					continue;
				}
				if (canSee(x, y)) {
					tiles[x + y * width].render(screen);
				}
			}
		}

		for (Entity e : visibleEntities) {
			e.render(screen);
		}

		for (int y = y0; y <= y1; y++) {
			for (int x = x0; x <= x1; x++) {
				if (x < 0 || x >= width || y < 0 || y >= height) {
					continue;
				}
				if (canSee(x, y)) {
					tiles[x + y * width].renderTop(screen);
				}
			}
		}
		for (Entity e : visibleEntities) {
			e.renderTop(screen);
		}
		/*
		 * for (int y = y0; y <= y1 + 2; y++) { for (int x = x0; x <= x1; x++) {
		 * if (x < 0 || x >= width || y < 0 || y >= height) {
		 * screen.blit(Art.floorTiles[5][0], x * Tile.WIDTH, y * Tile.HEIGHT);
		 * continue; } int xt = x - 28; int yt = y - 4; if (xt >= 0 && yt >= 0
		 * && xt < 7 && yt < 4) { screen.blit(Art.startLordLard[xt][yt], x *
		 * Tile.WIDTH, y * Tile.HEIGHT); continue; } yt = y - (64 - 8); if (xt
		 * >= 0 && yt >= 0 && xt < 7 && yt < 4) {
		 * screen.blit(Art.startLordLard[xt][yt], x * Tile.WIDTH, y *
		 * Tile.HEIGHT); continue; } if (canSee(x, y)) { tiles[x + y *
		 * width].render(screen); } } //
		 * 
		 * @todo: actually go through the correct rows int row = 2 + y - y0; if
		 * (rowEntities[row] != null) { for (Entity e : rowEntities[row]) {
		 * e.render(screen); } } }
		 */

		for (int y = y0; y <= y1; y++) {
			if (y < 0 || y >= height)
				continue;
			for (int x = x0; x <= x1; x++) {
				if (x < 0 || x >= width)
					continue;
				boolean c0 = !getSeen()[x + y * (width + 1)];
				boolean c1 = !getSeen()[(x + 1) + y * (width + 1)];
				boolean c2 = !getSeen()[x + (y + 1) * (width + 1)];
				boolean c3 = !getSeen()[(x + 1) + (y + 1) * (width + 1)];

				if (!(c0 || c1 || c2 || c3))
					continue;

				int count = 0;
				if (c0)
					count++;
				if (c1)
					count++;
				if (c2)
					count++;
				if (c3)
					count++;
				int yo = -16;

				if (count == 4) {
					screen.blit(Art.darkness[1][1], x * Tile.WIDTH, y
							* Tile.WIDTH + yo);
				} else if (count == 3) {
					if (!c0)
						screen.blit(Art.darkness[1][4], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (!c1)
						screen.blit(Art.darkness[0][4], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (!c2)
						screen.blit(Art.darkness[1][3], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (!c3)
						screen.blit(Art.darkness[0][3], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
				} else if (count == 1) {
					if (c0)
						screen.blit(Art.darkness[2][2], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (c1)
						screen.blit(Art.darkness[0][2], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (c2)
						screen.blit(Art.darkness[2][0], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (c3)
						screen.blit(Art.darkness[0][0], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
				} else {
					if (c0 && c3)
						screen.blit(Art.darkness[2][4], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (c1 && c2)
						screen.blit(Art.darkness[2][3], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (c0 && c1)
						screen.blit(Art.darkness[1][2], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (c2 && c3)
						screen.blit(Art.darkness[1][0], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (c0 && c2)
						screen.blit(Art.darkness[2][1], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
					if (c1 && c3)
						screen.blit(Art.darkness[0][1], x * Tile.WIDTH, y
								* Tile.WIDTH + yo);
				}
			}
		}

		screen.setOffset(0, 0);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int i = x + y * width;
				if (hasSeen(x, y)) {
					minimap.pixels[i] = tiles[i].minimapColor;
				} else {
					minimap.pixels[i] = 0xff000000;
				}
			}
		}

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (!e.removed) {
				if (e.minimapIcon >= 0) {
					int x = (int) (e.pos.x / Tile.WIDTH);
					int y = (int) (e.pos.y / Tile.WIDTH);
					if (x >= 0 && y >= 0 && x < width && y < height) {
						if (hasSeen(x, y)) {
							minimap.blit(
									Art.mapIcons[e.minimapIcon % 4][e.minimapIcon / 4],
									x - 2, y - 2);
						}
					}
				}
			}
		}
		screen.blit(Art.panel, 0, screen.h - 80);
		screen.blit(minimap, 429, screen.h - 80 + 5);
		
		String player1score =  MojamComponent.texts.scoreCharacter(player1Character, player1Score * 100 / TARGET_SCORE);
		Font.defaultFont().draw(screen, player1score, 280-player1score.length()*10, screen.h - 20); //adjust so it fits in the box
		Font.defaultFont().draw(screen, MojamComponent.texts.scoreCharacter(player2Character, player2Score * 100 / TARGET_SCORE), 56, screen.h - 36);
		
		screen.blit(Art.getPlayer(player1Character)[0][2], 262, screen.h-42);
		screen.blit(Art.getPlayer(player2Character)[0][6], 19, screen.h-42);
		
		Notifications.getInstance().render(screen);
	}

	private boolean canSee(int x, int y) {
		if (x < 0 || y < 1 || x >= width || y >= height)
			return true;
		return getSeen()[x + (y - 1) * (width + 1)]
				|| getSeen()[(x + 1) + (y - 1) * (width + 1)]
				|| getSeen()[x + y * (width + 1)] || getSeen()[(x + 1) + y * (width + 1)]
				|| getSeen()[x + (y + 1) * (width + 1)]
				|| getSeen()[(x + 1) + (y + 1) * (width + 1)];
	}

	public List<BB> getClipBBs(Entity e) {
		List<BB> result = new ArrayList<BB>();
		BB bb = e.getBB().grow(Tile.WIDTH);

		int x0 = (int) (bb.x0 / Tile.WIDTH);
		int x1 = (int) (bb.x1 / Tile.WIDTH);
		int y0 = (int) (bb.y0 / Tile.HEIGHT);
		int y1 = (int) (bb.y1 / Tile.HEIGHT);

		result.add(new BB(null, 0, 0, 0, height * Tile.HEIGHT));
		result.add(new BB(null, 0, 0, width * Tile.WIDTH, 0));
		result.add(new BB(null, width * Tile.WIDTH, 0, width * Tile.WIDTH,
				height * Tile.HEIGHT));
		result.add(new BB(null, 0, height * Tile.HEIGHT, width * Tile.WIDTH,
				height * Tile.HEIGHT));

		for (int y = y0; y <= y1; y++) {
			if (y < 0 || y >= height) {
				continue;
			}
			for (int x = x0; x <= x1; x++) {
				if (x < 0 || x >= width)
					continue;
				tiles[x + y * width].addClipBBs(result, e);
			}
		}

		Set<Entity> visibleEntities = getEntities(bb);
		for (Entity ee : visibleEntities) {
			if (ee != e && ee.blocks(e)) {
				result.add(ee.getBB());
			}
		}

		return result;
	}

	public void reveal(int x, int y, int radius) {
		for (int i = 0; i < radius * 2 + 1; i++) {
			revealLine(x, y, x - radius + i, y - radius, radius);
			revealLine(x, y, x - radius + i, y + radius, radius);
			revealLine(x, y, x - radius, y - radius + i, radius);
			revealLine(x, y, x + radius, y - radius + i, radius);
		}
		/*
		 * int x0 = x - radius; int x1 = x + radius; int y0 = y - radius; int y1
		 * = y + radius; double radiusSqr = radius * radius; if (x0 < 0) x0 = 0;
		 * if (y0 < 0) y0 = 0; if (x1 > width) x1 = width; if (y1 > height) y1 =
		 * height; for (int yy = y0; yy <= y1; yy++) { double yd = yy - (y +
		 * 0.5); for (int xx = x0; xx <= x1; xx++) { double xd = xx - (x + 0.5);
		 * double dist = xd * xd + yd * yd; if (dist < radiusSqr) seen[xx + yy *
		 * (width + 1)] = true; } }
		 */
	}

	private void revealLine(int x0, int y0, int x1, int y1, int radius) {
		for (int i = 0; i <= radius; i++) {
			int xx = x0 + (x1 - x0) * i / radius;
			int yy = y0 + (y1 - y0) * i / radius;
			if (xx < 0 || yy < 0 || xx >= width || yy >= height)
				return;
			int xd = xx - x0;
			int yd = yy - y0;
			if (xd * xd + yd * yd > radius * radius)
				return;
			Tile tile = getTile(xx, yy);
			if (tile instanceof WallTile)
				return;
			getSeen()[xx + yy * (width + 1)] = true;
			getSeen()[(xx + 1) + yy * (width + 1)] = true;
			getSeen()[xx + (yy + 1) * (width + 1)] = true;
			getSeen()[(xx + 1) + (yy + 1) * (width + 1)] = true;
		}
	}

	public void placeTile(int x, int y, Tile tile, Player player) {
		if (!getTile(x, y).isBuildable())
			return;

		// if (player != null && player.useMoney(tile.getCost())) {
		if (player != null) {
			setTile(x, y, tile);
		}
	}
	
	// counts how many of a certain entitiy class are in play
	public <T> int countEntities(Class<T> entityType) {
		int count = 0;
		for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
			if (entityType.isInstance(it.next())) {
				count++;
			}
		}
		return count;
	}

	public boolean[] getSeen() {
		return seen;
	}

	public void setSeen(boolean seen[]) {
		this.seen = seen;
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public void removeEvent(Event event) {
		events.remove(event);
	}
}