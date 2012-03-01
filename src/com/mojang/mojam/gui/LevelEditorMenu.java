package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.SpawnerForBat;
import com.mojang.mojam.entity.building.SpawnerForMummy;
import com.mojang.mojam.entity.building.SpawnerForScarab;
import com.mojang.mojam.entity.building.SpawnerForSnake;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.building.TurretTeamOne;
import com.mojang.mojam.entity.building.TurretTeamTwo;
import com.mojang.mojam.entity.mob.SpikeTrap;
import com.mojang.mojam.level.IEditable;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.level.LevelUtils;
import com.mojang.mojam.level.tile.DestroyableWallTile;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.HoleTile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;
import com.mojang.mojam.level.tile.WallTile;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class LevelEditorMenu extends GuiMenu {

    private final int LEVEL_WIDTH = 48;
    private final int LEVEL_HEIGHT = 48;
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;
    private final int MENU_WIDTH = 142;
    
    private final int mapW = LEVEL_WIDTH * TILE_WIDTH;
    private final int mapH = LEVEL_HEIGHT * TILE_HEIGHT;
    private int mapX = MENU_WIDTH;
    private int mapY;
    
    private int[][] mapTile = new int[LEVEL_HEIGHT][LEVEL_WIDTH];
    private Bitmap[][] map = new Bitmap[LEVEL_HEIGHT][LEVEL_WIDTH];
    private Bitmap mapFloor = new Bitmap(mapW, mapH);
    private Bitmap minimap = new Bitmap(LEVEL_WIDTH, LEVEL_HEIGHT);
        
    private Bitmap pencil = new Bitmap(TILE_WIDTH, TILE_HEIGHT);
    private int pencilX;
    private int pencilY;
    private boolean drawing;
    
    private final IEditable[] editableTiles = {
        new FloorTile(),
        new HoleTile(),
        new WallTile(),
        new DestroyableWallTile(),
        new TreasurePile(0, 0),
        new UnbreakableRailTile(new FloorTile()),
        new Turret(0, 0, 0),
        new TurretTeamOne(0, 0),
        new TurretTeamTwo(0, 0),
        new SpikeTrap(0, 0),
        new SpawnerForBat(0, 0),
        new SpawnerForSnake(0, 0),
        new SpawnerForMummy(0, 0),
        new SpawnerForScarab(0, 0)
    };
    
    private final int buttonsPerPage = 12;
    private final int totalPages = (int) Math.ceil(editableTiles.length / (float) buttonsPerPage);
    private int currentPage = 0;
    private final int buttonsCols = 3;
    private final int buttonMargin = 1;
    private final int buttonsX = 7;
    private final int buttonsY = 20;
    
    private LevelEditorButton[] tileButtons;
    private LevelEditorButton selectedButton;
    
    private Button prevPageButton;
    private Button nextPageButton;
    private Button newButton;
    private Button openButton;
    private Button saveButton;
    private Button cancelButton;
    private Button confirmeSaveButton;
    private Button cancelSaveButton;
    
    private Panel savePanel;
    private ClickableComponent editorComponent;
    private Text levelName;
    
    private boolean clicked;
    private boolean updateButtons;
    private boolean updateTileButtons;
    private boolean saveMenuVisible;
    
    private List<LevelInformation> levels;
    private int selectedLevel;
    
    private String saveLevelName = "";
    private Random random = new Random();
    
    public LevelEditorMenu() {
        super();
    	
        createGUI();
        setCurrentPage(0);
        
        // setup pencil
        pencil.fill(0, 0, pencil.w, pencil.h, 0xffcfac02);
        pencil.fill(1, 1, pencil.w - 2, pencil.h - 2, 0);

        // setup map
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                mapFloor.blit(Art.floorTiles[random.nextInt(3)][0], TILE_WIDTH * x, TILE_HEIGHT * y);
            }
        }

        // load levels list
        updateLevels();
        
        // loads first level on the list
        openLevel(levels.get(selectedLevel));

        addButtonListener(this);
    }
    
    @Override
    public void tick(MouseButtons mouseButtons) {
        super.tick(mouseButtons);
        
        // show/hide save menu buttons
        if (updateButtons) {
            updateSaveButtons();
            updateButtons = false;
        }
        
        // update tile buttons
        if (updateTileButtons){
            updateTileButtons();
            updateTileButtons = false;
        }
        
        // lock buttons when save menu is visible
        if(saveMenuVisible) return;

        // update pencil location
        pencilX = (mouseButtons.getX() / 2) - (TILE_WIDTH / 2);
        pencilY = (mouseButtons.getY() / 2) - (TILE_HEIGHT / 2);

        // move level x with mouse
        if (mouseButtons.getX() - MENU_WIDTH > MENU_WIDTH) {
            if (pencilX + TILE_WIDTH > MojamComponent.GAME_WIDTH
                    && -(mapX - MENU_WIDTH) < mapW - (MojamComponent.GAME_WIDTH - MENU_WIDTH) + TILE_HEIGHT) {
                mapX -= TILE_WIDTH / 2;
            } else if (pencilX < MENU_WIDTH && mapX < MENU_WIDTH + 32) {
                mapX += TILE_WIDTH / 2;
            }
        }
        
        // move level y with mouse
        if (pencilY + TILE_HEIGHT > MojamComponent.GAME_HEIGHT
                && -mapY < mapH - MojamComponent.GAME_HEIGHT + TILE_HEIGHT) {
            mapY -= TILE_HEIGHT / 2;
        } else if (pencilY < 0 && mapY < TILE_HEIGHT) {
            mapY += TILE_HEIGHT / 2;
        }
               
        // draw
        if (drawing || editorComponent.isPressed()) {
            int x = (((pencilX + TILE_WIDTH / 2) - mapX) / TILE_WIDTH);
            int y = (((pencilY + TILE_HEIGHT / 2) - mapY) / TILE_HEIGHT);
            draw(selectedButton.getTile(), x, y);
        }
    }

    @Override
    public void render(Screen screen) {
        screen.clear(0);

        // level floor
        screen.blit(mapFloor, mapX, mapY);

        // level tiles
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {

                if (map[x][y] == null) continue;

                if (map[x][y].h == TILE_HEIGHT) {
                    if (mapTile[x][y] == HoleTile.COLOR) {
                        if (y > 0 && !(mapTile[x][y - 1] == HoleTile.COLOR)) {
                            screen.blit(map[x][y], TILE_HEIGHT * x + mapX, TILE_HEIGHT * y + mapY);
                        } else {
                            screen.fill(TILE_HEIGHT * x + mapX, TILE_HEIGHT * y + mapY, TILE_WIDTH, TILE_HEIGHT, 0);
                        }
                    } else {
                        screen.blit(map[x][y], TILE_HEIGHT * x + mapX, TILE_HEIGHT * y + mapY);
                    }
                } else {
                    //tile real height
                    int tileH = (int) (Math.ceil(map[x][y].h / (float) TILE_HEIGHT)) * TILE_WIDTH;
                    int tileY = TILE_HEIGHT - (tileH - map[x][y].h);

                    if (mapTile[x][y] == UnbreakableRailTile.COLOR) {
                        boolean n = y > 0 && mapTile[x][y - 1] == UnbreakableRailTile.COLOR;
                        boolean s = y < 47 && mapTile[x][y + 1] == UnbreakableRailTile.COLOR;
                        boolean w = x > 0 && mapTile[x - 1][y] == UnbreakableRailTile.COLOR;
                        boolean e = x < 47 && mapTile[x + 1][y] == UnbreakableRailTile.COLOR;

                        int c = (n ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0) + (e ? 1 : 0);
                        int img;

                        if (c <= 1) {
                            img = (n || s) ? 1 : 0;     // default is horizontal
                        } else if (c == 2) {
                            if (n && s) {
                                img = 1;                // vertical
                            } else if (w && e) {
                                img = 0;                // horizontal
                            } else {
                                img = n ? 4 : 2;        // north turn
                                img += e ? 0 : 1;       // south turn
                            }
                        } else {                        // 3 or more turning disk
                            img = 6;
                        }
                        screen.blit(Art.rails[img][0], mapX + TILE_HEIGHT * x, mapY + TILE_HEIGHT * y - tileY);
                    } else {
                        screen.blit(map[x][y], mapX + TILE_HEIGHT * x, mapY + TILE_HEIGHT * y - tileY);
                    }
                }
  
            }
        }
        
        // pencil position indicator
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                if (x == (((pencilX + TILE_WIDTH / 2) - mapX) / TILE_WIDTH) && y == (((pencilY + TILE_HEIGHT / 2) - mapY) / TILE_HEIGHT)) {
                    screen.blit(pencil, TILE_HEIGHT * x + mapX, TILE_HEIGHT * y + mapY);
                    break;
                }
            }
        }

        super.render(screen);
        
        // minimap
        screen.blit(minimap, screen.w - minimap.w - 6, 6);
        
        // selected tile name
        Font.defaultFont().draw(screen, selectedButton != null ? selectedButton.getTile().getName() : "",
        		MENU_WIDTH / 2, 13, Font.Align.CENTERED);
        
        // current page and total pages
        Font.defaultFont().draw(screen, (currentPage + 1) + "/" + totalPages,
        		MENU_WIDTH / 2, 261, Font.Align.CENTERED);
    }
       
    private void updateTileButtons() {
        int y = 0;

        // Remove previous buttons
        if (tileButtons != null) {
            for (int i = 0; i < tileButtons.length; i++) {
                if (tileButtons[i] != null) {
                    removeButton(tileButtons[i]);
                }
            }
        }

        tileButtons = new LevelEditorButton[Math.min(buttonsPerPage,
                editableTiles.length - currentPage * buttonsPerPage)];

        for (int i = currentPage * buttonsPerPage;
                i < Math.min((currentPage + 1) * buttonsPerPage, editableTiles.length); i++) {
            int x = i % buttonsCols;
            int id = i % buttonsPerPage;

            tileButtons[id] = (LevelEditorButton) addButton(new LevelEditorButton(i, editableTiles[i],
                    buttonsX + x * (LevelEditorButton.WIDTH + buttonMargin), buttonsY + y));

            if (id == 0) {
                selectedButton = tileButtons[id];
                selectedButton.setActive(true);
            }

            if (x == (buttonsCols - 1)) {
                y += LevelEditorButton.HEIGHT + buttonMargin;
            }
        }
    }
    
    private boolean hasPreviousPage() {
	    return currentPage > 0;
	}
	
    private boolean hasNextPage() {
        return (currentPage + 1) * buttonsPerPage < editableTiles.length;
    }
    
    private void setCurrentPage(int page) {
        currentPage = page;
        updateTileButtons = true;
    }
    
    private void updateSaveButtons() {
        if (saveMenuVisible) {
            addButton(savePanel);
            addButton(confirmeSaveButton);
            addButton(cancelSaveButton);
        } else {
            removeButton(confirmeSaveButton);
            removeButton(cancelSaveButton);
            removeButton(savePanel);
        }
    }

    private void updateLevels() {
        LevelList.resetLevels();
        levels = LevelList.getLevels();
    }

    private void draw(IEditable tileOrEntity, int x, int y) {

        if (x < 0 || x > LEVEL_WIDTH - 1) return;
        if (y < 0 || y > LEVEL_HEIGHT - 1) return;
        if (mapTile[x][y] == tileOrEntity.getColor()) return;
        
        if (tileOrEntity.getColor() != FloorTile.COLOR) {
            map[x][y] = tileOrEntity.getBitMapForEditor();
        } else {
            map[x][y] = null;
        }
        
        mapTile[x][y] = tileOrEntity.getColor();
        minimap.fill(x, y, 1, 1, tileOrEntity.getMiniMapColor() );
    }

    private void newLevel() {
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                mapTile[x][y] = FloorTile.COLOR;
                map[x][y] = null;
            }
        }
       minimap.fill(0, 0, minimap.w, minimap.h, editableTiles[0].getMiniMapColor());
       removeText(levelName);
       levelName = new Text(1, "<New Level>", 120, 5);
       addText(levelName);
    }

    private void openLevel(LevelInformation li) {
        BufferedImage bufferedImage = null;
        
        try {
            if (li.vanilla) {
                bufferedImage = ImageIO.read(MojamComponent.class.getResource(li.getPath()));
            } else {
                bufferedImage = ImageIO.read(new File(li.getPath()));
            }
        } catch (IOException ioe) {
        }

        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();

        int[] rgbs = new int[w * h];

        bufferedImage.getRGB(0, 0, w, h, rgbs, 0, w);

        newLevel();
        
	removeText(levelName);
        levelName = new Text(1, li.levelName, 120, 5);
        addText(levelName);
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int col = rgbs[x + y * w] & 0xffffffff;

                IEditable tile = LevelUtils.getNewTileFromColor(col);
                draw(tile, x, y);

                if (tile instanceof FloorTile) {
                    Entity entity = LevelUtils.getNewEntityFromColor(col, x, y);
                    if (entity instanceof IEditable) {
                        draw((IEditable) entity, x, y);
                    }
                }
            }
        }
    }
    
    private boolean saveLevel(String name) {

        BufferedImage image = new BufferedImage(LEVEL_WIDTH, LEVEL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                image.setRGB(x, y, mapTile[x][y]);
            }
        }

        try {
            File newLevel = new File(LevelList.getBaseDir(), name + ".bmp");
            newLevel.createNewFile();
            ImageIO.write(image, "BMP", newLevel);
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException : " + ex);
            return true;
        } catch (IOException ioe) {
            System.out.println("IOException : " + ioe);
            return false;
        }

        return true;
    }
    
    private void createGUI() {
        
        levelName = new Text(1,"", 120, 5);
        
        // map clickable component
        editorComponent = addButton(new ClickableComponent(MENU_WIDTH, 0, MojamComponent.GAME_WIDTH - MENU_WIDTH, MojamComponent.GAME_HEIGHT) {

            @Override
            protected void clicked(MouseButtons mouseButtons) {
                // do nothing, handled by button listeners
            }
        });
        
        // menu panel
        addButton(new Panel(0, 0, MENU_WIDTH, MojamComponent.GAME_HEIGHT));
        
        // minimap panel
        addButton(new Panel(MojamComponent.GAME_WIDTH - minimap.w - 11, 1, minimap.w + 10, minimap.w + 10));
        
        // save menu panel
        savePanel = new Panel(180, 120, 298, 105) {

            @Override
            public void render(Screen screen) {
                super.render(screen);
                Font.defaultFont().draw(screen, MojamComponent.texts.getStatic("leveleditor.enterLevelName"),
                        getX() + getWidth() / 2, getY() + 20, Font.Align.CENTERED);
                Font.defaultFont().draw(screen, saveLevelName + "_",
                        getX() + getWidth() / 2, getY() + 40, Font.Align.CENTERED);
            }
        };

        // save menu buttons
        confirmeSaveButton = new Button(-1, MojamComponent.texts.getStatic("leveleditor.save"), 195, 190);
        cancelSaveButton = new Button(-1, MojamComponent.texts.getStatic("cancel"), 335, 190);

        // actions buttons
        int startY = (MojamComponent.GAME_HEIGHT - 5) - 26 * 5;
        prevPageButton = (Button) addButton(new Button(-1, "(",
                7, startY, 30, Button.BUTTON_HEIGHT));
        nextPageButton = (Button) addButton(new Button(-1, ")",
                MENU_WIDTH - 37, startY, 30, Button.BUTTON_HEIGHT));
        newButton = (Button) addButton(new Button(-1, MojamComponent.texts.getStatic("leveleditor.new"),
                7, startY += 26));
        openButton = (Button) addButton(new Button(-1, MojamComponent.texts.getStatic("leveleditor.open"),
                7, startY += 26));
        saveButton = (Button) addButton(new Button(-1, MojamComponent.texts.getStatic("leveleditor.save"),
                7, startY += 26));
        cancelButton = (Button) addButton(new Button(TitleMenu.BACK_ID, MojamComponent.texts.getStatic("back"),
                7, startY += 26));
    }
    
    @Override
    public void buttonPressed(ClickableComponent button) {
              
        // save menu buttons
        if (saveMenuVisible) {
            if (button == confirmeSaveButton) {
                if (saveLevel(saveLevelName)) {
                    removeText(levelName);
                    levelName = new Text(1, "+ " + saveLevelName, 120, 5);
                    addText(levelName);
                    
                    updateLevels();
                }
            }
            
            if (button == confirmeSaveButton || button == cancelSaveButton) {
                saveMenuVisible = false;
                updateButtons = true;
                saveLevelName = "";
            }

            return;
        }
        
        // tile buttons
        if (button instanceof LevelEditorButton) {
            LevelEditorButton lb = (LevelEditorButton) button;

            if (selectedButton != null && selectedButton != lb) {
                selectedButton.setActive(false);
                selectedButton = lb;
            }

            return;
        }

        // menu buttons
        if (!clicked) {
            if (button == newButton) {
                newLevel();
            } else if (button == openButton) {
                selectedLevel = (selectedLevel < levels.size() - 1 ? selectedLevel + 1 : 0);
                openLevel(LevelList.getLevels().get(selectedLevel));
                
            } else if (button == saveButton) {
                saveMenuVisible = true;
                updateButtons = true;
            } else if (button == prevPageButton && hasPreviousPage()) {
                setCurrentPage(currentPage - 1);
            } else if (button == nextPageButton && hasNextPage()) {
                setCurrentPage(currentPage + 1);
            }

            clicked = true;
        } else {
            clicked = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        // cancel/goback
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (saveMenuVisible) {
                cancelSaveButton.postClick();
            } else {
                cancelButton.postClick();
            }
            return;
        }

        // confirme
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (saveMenuVisible) {
                confirmeSaveButton.postClick();
            }
            return;
        }
        
        // disable keys if save menu is visible
        if (saveMenuVisible) {
            return;
        }

        // start/toggle drawing
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            drawing = true;
        } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            drawing = !drawing;
        }

        // move level with keys
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            mapX += 32;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            mapX -= 32;
        } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            mapY += 32;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            mapY -= 32;
        }
        
        //tab to scroll through tiles
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            int id = (selectedButton.getId() - (buttonsPerPage * currentPage));
            
            if (selectedButton.getId() + 1 == editableTiles.length) {
                setCurrentPage(0);
            } else if (id == buttonsPerPage - 1 && hasNextPage()) {
                setCurrentPage(currentPage + 1);
            } else if (selectedButton.getId() + 1 < editableTiles.length) {
                tileButtons[id + 1].postClick();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // stop drawing
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            drawing = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // read input for new level name
        if (saveMenuVisible) {
            if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && saveLevelName.length() > 0) {
                saveLevelName = saveLevelName.substring(0, saveLevelName.length() - 1);
            } else {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) return;
                
                saveLevelName += e.getKeyChar();;
            }
        }
    }
}