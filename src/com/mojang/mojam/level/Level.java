package com.mojang.mojam.level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.entity.predicates.EntityIntersectsBB;
import com.mojang.mojam.entity.predicates.EntityIntersectsBBAndInstanceOf;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.level.gamemode.ILevelTickItem;
import com.mojang.mojam.level.gamemode.IVictoryConditions;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.level.tile.AnimatedTile;
import com.mojang.mojam.level.tile.WallTile;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.math.BBPredicate;
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
	private boolean largeMap = false, smallMap = false;
	private boolean seen[];
	final int[] neighbourOffsets;

	public List<ILevelTickItem> tickItems = new ArrayList<ILevelTickItem>();;
	public int maxMonsters;
	
	public int[][] monsterDensity;
	public int densityTileWidth = 5;
	public int densityTileHeight = 5;

	public IVictoryConditions victoryConditions;
	public int player1Score = 0;
	public int player2Score = 0;

	@SuppressWarnings("unchecked")
	public Level(int width, int height) {
		neighbourOffsets = new int[] { -1, 1, -width, width };
		this.width = width;
		this.height = height;
		
		int denseTileArrayWidth;
		int denseTileArrayHeight;
		if(width % 3 == 0)
			denseTileArrayWidth = width/densityTileWidth;
		else
			denseTileArrayWidth = width/densityTileWidth+1;
		
		if(height % 3 == 0)
			denseTileArrayHeight = height/densityTileHeight;
		else
			denseTileArrayHeight = height/densityTileHeight+1;
		
		monsterDensity = new int[denseTileArrayWidth][denseTileArrayHeight];

		minimap = new Bitmap(width, height);
		
		largeMap = height > 64 || width > 64;
		smallMap = height < 64 && width < 64;
		
		initializeTiles();

		entityMap = new List[width * height];
		for (int i = 0; i < width * height; i++) {
			entityMap[i] = new ArrayList<Entity>();
		}

		setSeen(new boolean[(width + 1) * (height + 1)]);
	}

	private void initializeTiles(){
	    tiles = new Tile[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = new FloorTile();
                setTile(x, y, tile);
            }
        }
	}

	public void setTile(int x, int y, Tile tile) {
		final int index = x + (y * width);
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
		if(tile instanceof AnimatedTile) { 
			tickItems.add((ILevelTickItem) tile);
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

    public Set<Entity> getEntities(double x0, double y0, double x1, double y1) {
        return getEntities(x0, y0, x1, y1, EntityIntersectsBB.INSTANCE);
    }

    public Set<Entity> getEntities(BB bb, Class<? extends Entity> c) {
        return getEntities(bb.x0, bb.y0, bb.x1, bb.y1, c);
    }

    public Set<Entity> getEntities(double x0, double y0, double x1, double y1,
            Class<? extends Entity> c) {
        return getEntities(x0, y0, x1, y1, new EntityIntersectsBBAndInstanceOf(c));
    }

    public Set<Entity> getEntities(double xx0, double yy0, double xx1,
            double yy1, BBPredicate<Entity> predicate) {
        final int x0 = Math.max((int) (xx0) / Tile.WIDTH, 0);
        final int x1 = Math.min((int) (xx1) / Tile.WIDTH, width - 1);
        final int y0 = Math.max((int) (yy0) / Tile.HEIGHT, 0);
        final int y1 = Math.min((int) (yy1) / Tile.HEIGHT, height - 1);

        final Set<Entity> result = new TreeSet<Entity>(new EntityComparator());

        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                for (Entity e : entityMap[x + y * width]) {
                    if (predicate.appliesTo(e, xx0, yy0, xx1, yy1)) {
                        result.add(e);
                    }
                }
            }
        }

        return result;
    }

	public Set<Entity> getEntitiesSlower(double xx0, double yy0, double xx1,
			double yy1, Class<? extends Entity> c) {
		final Set<Entity> result = new TreeSet<Entity>(new EntityComparator());
        final BBPredicate<Entity> predicate =
                new EntityIntersectsBBAndInstanceOf(c);

        for (Entity e : this.entities) {
            if (predicate.appliesTo(e, xx0, yy0, xx1, yy1)) {
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
	
	public void addMob(Mob m, int xTile, int yTile)
	{
		updateDensityList();
		if(monsterDensity[(int)(xTile/densityTileWidth)][(int)(yTile/densityTileHeight)] < TitleMenu.difficulty.getAllowedMobDensity())
		{
			addEntity(m);
		}
	}

	public void removeEntity(Entity e) {
		e.removed = true;
	}
	
	public void updateDensityList()
	{
		for(int x=0;x < monsterDensity.length;x++)
		{
			for(int y=0;y < monsterDensity[x].length;y++)
			{
				int entityNumb = getEntities(x*densityTileWidth*Tile.WIDTH,y*densityTileHeight*Tile.HEIGHT,x*(densityTileWidth+1)*Tile.WIDTH,y*(densityTileHeight+1)*Tile.HEIGHT).size();
				monsterDensity[x][y] = entityNumb;
			}
		}
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
		Notifications.getInstance().tick();
	}

	private boolean hasSeen(int x, int y) {
		return getSeen()[x + y * (width + 1)] || getSeen()[(x + 1) + y * (width + 1)]
				|| getSeen()[x + (y + 1) * (width + 1)]
				|| getSeen()[(x + 1) + (y + 1) * (width + 1)];
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

		screen.setOffset(-xScroll, -yScroll);

		renderTilesAndBases(screen, x0, y0, x1, y1);

		for (Entity e : visibleEntities) {
			e.render(screen);
			// this renders players carrying something
	        e.renderTop(screen);
		}

		renderTopOfWalls(screen, x0, y0, x1, y1);
		renderDarkness(screen, x0, y0, x1, y1);
		
		screen.setOffset(0, 0);
		
		updateMinimap();
		renderPanelAndMinimap(screen, x0, y0);
		renderPlayerScores(screen);
		
		Notifications.getInstance().render(screen);
	}

	private void renderTilesAndBases(Screen screen, int x0, int y0, int x1, int y1){
	    // go through each currently visible cell
	    for (int y = y0; y <= y1; y++) {
	        for (int x = x0; x <= x1; x++) {

	            // draw sand outside the level
	            if (x < 0 || x >= width || y < 0 || y >= height) {
	                screen.blit(Art.floorTiles[5][0], x * Tile.WIDTH, y
	                        * Tile.HEIGHT);
	                continue;
	            }

	            Bitmap[][] playerBaseZero = Art.getPlayerBase(getPlayerCharacter(0));
	            Bitmap[][] playerBaseOne = Art.getPlayerBase(getPlayerCharacter(1));
	            int baseOneTileHeight = playerBaseOne[0].length;
	            int baseOneTileWidth = playerBaseOne.length;
	            int baseZeroTileHeight = playerBaseZero[0].length;
	            int baseZeroTileWidth = playerBaseZero.length;
	            
	            
	            // if we are in the center area (4*7 Tiles): draw player bases
				int xt = x - (width / 2) + ((baseOneTileWidth-(baseOneTileWidth%2))/2) + (baseOneTileWidth%2);
				int yt = y - baseOneTileHeight;

	            if (xt >= 0 && yt >= 0 && xt < baseOneTileWidth && yt < baseOneTileHeight && (isNotBaseRailTile(x) || yt < baseOneTileHeight-1)) {
	                screen.blit(playerBaseOne[xt][yt], x * Tile.WIDTH, y
	                        * Tile.HEIGHT);
	                continue;
	            }
	            
	            // if we are in the center area (4*7 Tiles): draw player bases
				xt = x - (width / 2) + ((baseZeroTileWidth-(baseZeroTileWidth%2))/2) + (baseZeroTileWidth%2);
				
				
	            yt = y - (height - 8);
	            if (xt >= 0 && yt >= 0 && xt < baseZeroTileWidth && yt < baseZeroTileHeight && (isNotBaseRailTile(x) || yt > 0)) {       
					screen.blit(playerBaseZero[xt][yt], x * Tile.WIDTH, y * Tile.HEIGHT);
	                if (yt == 0 && ((xt <=(baseZeroTileWidth-3)/2) || (xt >=((baseZeroTileWidth-3)/2)+3 ))) {
	                    screen.blit(Art.shadow_north, x * Tile.WIDTH, y * Tile.HEIGHT);
	                }
	                if ((xt == 2) && yt == 0) {
	                    screen.blit(Art.shadow_north_west, x * Tile.WIDTH, y * Tile.HEIGHT);
	                }
	                if ((xt == 4) && yt == 0) {
	                    screen.blit(Art.shadow_north_east, x * Tile.WIDTH + Tile.WIDTH - Art.shadow_east.w, y * Tile.HEIGHT);
	                }
	                continue;
	            }

	            if (canSee(x, y)) {
	                tiles[x + y * width].render(screen);
	            }
	        }
	    }
	}

	private GameCharacter getPlayerCharacter(int playerID){
	    Player player = MojamComponent.instance.players[playerID];
	    if (player == null) return GameCharacter.None;
	    else return player.getCharacter();
	}
	
	private boolean isNotBaseRailTile(int x){
	    return (x < (width/2 - 2) || x > (width/2));
	}
	

	private void renderTopOfWalls(Screen screen, int x0, int y0, int x1, int y1){
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
	}
	
	private void renderDarkness(Screen screen, int x0, int y0, int x1, int y1){
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
	}
	
	private void updateMinimap(){
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

        addIconsToMinimap();
	}
	
	private void addIconsToMinimap() {
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
    }
	
	private void renderPanelAndMinimap(Screen screen, int x0, int y0){
	    Bitmap displaymap = new Bitmap(64, 64);
	    
	    if(largeMap){
            displaymap = calculateLargeMapDisplay(x0, y0);
        } else if(smallMap){
            displaymap = calculateSmallMapDisplay();
        } else {
            displaymap = minimap;
        }
        
        screen.blit(Art.panel, 0, screen.h - 80);
        screen.blit(displaymap, 429, screen.h - 80 + 5);
	}
	
	private Bitmap calculateLargeMapDisplay(int x0, int y0) {
	    
	    Bitmap largeMap = new Bitmap(64, 64);
	    int locx = x0 + 8;
        int locy = y0 + 8;
        
        int drawx = 0, drawy = 0;
        int donex = 0, doney = 0;
        int diffx = 0, diffy = 0;
        
        if(width < 64) diffx = (64 - width) / 2;
        if(height < 64) diffy = (64 - height) / 2;
        
        if(locx < 32 || width < 64){
            drawx = 0;
        } else if(locx > (width - 32)){
            drawx = width - 64;
        } else{
            drawx = locx - 32;
        }
        
        if(locy < 32 || height < 64){
            drawy = 0;
        } else if(locy > (height - 32)){
            drawy = height - 64;
        } else {
            drawy = locy - 32;
        }
        
        for (int y = 0; y < 64; y++) {
            if(y < diffy || y >= (64 - diffy)) {
                for (int x = 0; x < 64; x++) {
                    largeMap.pixels[x + (y * 64)] = Art.floorTileColors[5 & 7][5 / 8];
                }
            }
            else{
                for (int x = 0; x < 64; x++) {
                    if(x < diffx || x > (64 - diffx)) 
                        largeMap.pixels[x + (y * 64)] = Art.floorTileColors[5 & 7][5 / 8];
                    else{
                        if(((drawx + donex) + (drawy + doney) * width) < minimap.pixels.length -1) 
                            largeMap.pixels[x + (y * 64)] = minimap.pixels[(drawx + donex) + (drawy + doney) * width];
                            donex++;
                    }
                }
                donex = 0;
                doney++;
            }
        }
        
        return largeMap;
	}
	
	private Bitmap calculateSmallMapDisplay() {
	    
	    Bitmap smallMap = new Bitmap(64, 64);
        int smallx = 0, smally = 0;
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                if(x >= (32 - width/2) && x <= (32 + width/2) && y >= (32 - height/2) && y < (32 + height/2) - 1){
                    smallMap.pixels[x + y * 64] = minimap.pixels[smallx + smally * width];
                    smallx++;
                }
                else
                    smallMap.pixels[x + y * 64] = Art.floorTileColors[5 & 7][5 / 8];
            }
            smallx = 0;
            
            if(y >= (32 - height/2) && y < (32 + height/2) - 1)
                smally++;
        }
        return smallMap;
	}
	
	private void renderPlayerScores(Screen screen){
	    
	    String player1score =  MojamComponent.texts.scoreCharacter(getPlayerCharacter(0), player1Score * 100 / TARGET_SCORE);
        Font.defaultFont().draw(screen, player1score, 280-player1score.length()*10, screen.h - 20); //adjust so it fits in the box
        screen.blit(Art.getPlayer(getPlayerCharacter(0))[0][2], 262, screen.h-42);

        if (MojamComponent.instance.players[1] != null && getPlayerCharacter(1) != GameCharacter.None) {
            Font.defaultFont().draw(screen, MojamComponent.texts.scoreCharacter(getPlayerCharacter(1), player2Score * 100 / TARGET_SCORE), 56, screen.h - 36);
            screen.blit(Art.getPlayer(getPlayerCharacter(1))[0][6], 19, screen.h-42);
        }
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

		if (player != null) {
			setTile(x, y, tile);
		}
	}
	
	// counts how many of a certain entity class are in play
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
}
