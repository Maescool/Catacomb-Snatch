package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
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
    
    private final String[] tileNames = new String[]{
        "FLOOR", "HOLE", "WALL", "B.WALL", "TREASURE", "RAIL"};
    private final int[] tileColors = new int[]{
        0xffffffff, 0xff000000, 0xffff0000, 0xffFF7777, 0xffffff00, 0xff969696};
    private final Bitmap[] tiles = new Bitmap[]{
        Art.floorTiles[0][0], Art.floorTiles[4][0], Art.wallTiles[0][0],
        Art.treasureTiles[4][0], Art.treasureTiles[0][0], Art.rails[1][0]
    };
    
    private final int buttonsCols = 2;
    private final int buttonMargin = 2;
    private final int buttonsX = 7;
    private final int buttonsY = 7;
    
    private LevelEditorButton[] tileButtons = new LevelEditorButton[tiles.length];
    private LevelEditorButton selectedButton;
    
    private Button newButton;
    private Button openButton;
    private Button saveButton;
    private Button cancelButton;
    private Button confirmeSaveButton;
    private Button cancelSaveButton;
    
    private Panel savePanel;
    private ClickableComponent editorComponent;
    
    private boolean clicked;
    private boolean updateButtons;
    private boolean saveMenuVisible;
    
    private List<LevelInformation> levels;
    private int selectedLevel;
    
    private String saveLevelName = "";
    private Random random = new Random();

    public LevelEditorMenu() {
        super();

        createGUI();
        updateTileButtons();
        
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
            draw(selectedButton.getId(), x, y);
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
                    if (mapTile[x][y] == 1) {
                        if (y > 0 && !(mapTile[x][y - 1] == 1)) {
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

                    if (mapTile[x][y] == 5) {
                        boolean n = y > 0 && mapTile[x][y - 1] == 5;
                        boolean s = y < 47 && mapTile[x][y + 1] == 5;
                        boolean w = x > 0 && mapTile[x - 1][y] == 5;
                        boolean e = x < 47 && mapTile[x + 1][y] == 5;

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
        
        // title
        //Font.defaultFont().drawCentered(screen, MojamComponent.texts.getStatic("leveleditor.title"), MENU_WIDTH / 2, 10);
    }
    
    private void updateTileButtons() {
        int y = 0;

        for (int i = 0; i < tileButtons.length; i++) {
            int x = i % buttonsCols;

            tileButtons[i] = (LevelEditorButton) addButton(new LevelEditorButton(i, tiles[i], tileNames[i],
                    buttonsX + x * (i > 0 ? tileButtons[i - 1].getWidth() + buttonMargin : 0), buttonsY + y));

            if (i == 0) {
                selectedButton = tileButtons[i];
                selectedButton.setActive(true);
            }

            if (x == (buttonsCols - 1)) {
                y += LevelEditorButton.HEIGHT + buttonMargin;
            }
        }
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

    private void setTile(int id, Bitmap tile, int x, int y) {
        mapTile[x][y] = id;
        map[x][y] = tile;
        minimap.fill(x, y, 1, 1, (tile == null ? 0 : tileColors[id]));
    }

    private void draw(int id, int x, int y) {

        if (x < 0 || x > LEVEL_WIDTH - 1) return;
        if (y < 0 || y > LEVEL_HEIGHT - 1) return;
        if (mapTile[x][y] == id) return;

        switch (id) {
            case 0:
                setTile(id, null, x, y);
                break;
            case 1:
                setTile(id, Art.floorTiles[4][0], x, y);
                break;
            case 2:
                setTile(id, Art.wallTiles[random.nextInt(4)][0], x, y);
                break;
            case 3:
                setTile(id, Art.treasureTiles[4][0], x, y);
                break;
            case 4:
                setTile(id, Art.treasureTiles[0][0], x, y);
                break;
            case 5:
                setTile(id, Art.rails[0][0], x, y);
                break;
        }
    }

    private void newLevel() {
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                mapTile[x][y] = 0;
                map[x][y] = null;
            }
        }
       minimap.fill(0, 0, minimap.w, minimap.h, 0);
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

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int col = rgbs[x + y * w] & 0xffffffff;
                for (int i = 0; i < tileColors.length; i++) {
                    if (col == (tileColors[i])) {
                        draw(i, x, y);
                        break;
                    }
                }
            }
        }
    }

    private void saveLevel(String name) {
        File newLevel = new File(LevelList.getBaseDir(), name + ".bmp");

        BufferedImage image = new BufferedImage(LEVEL_WIDTH, LEVEL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                image.setRGB(x, y, tileColors[mapTile[x][y]]);
            }
        }

        try {
            ImageIO.write(image, "BMP", newLevel);
        } catch (IOException ioe) {
            System.out.println("Error saving new level: " + ioe);
        }
    }
    
    private void createGUI() {
        
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
        addButton(new Panel(MojamComponent.GAME_WIDTH - minimap.w - 11, 1, minimap.w + 10, minimap.h + 10));

        // save menu panel
        savePanel = new Panel(180, 120, 298, 105) {

            @Override
            public void render(Screen screen) {
                super.render(screen);
                Font.defaultFont().drawCentered(screen, MojamComponent.texts.getStatic("leveleditor.enterLevelName"),
                        getX() + getWidth() / 2, getY() + 20);
                Font.defaultFont().drawCentered(screen, saveLevelName + "_",
                        getX() + getWidth() / 2, getY() + 40);
            }
        };

        // save menu buttons
        confirmeSaveButton = new Button(-1, MojamComponent.texts.getStatic("leveleditor.save"), 195, 190);
        cancelSaveButton = new Button(-1, MojamComponent.texts.getStatic("cancel"), 335, 190);

        // actions buttons
        int startY = (MojamComponent.GAME_HEIGHT - 5) - 26 * 4;
        newButton = (Button) addButton(new Button(-1, MojamComponent.texts.getStatic("leveleditor.new"),
                7, startY));
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
                saveLevel(saveLevelName);
                updateLevels();
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
            tileButtons[selectedButton.getId() < tileButtons.length - 1 ? selectedButton.getId() + 1 : 0].postClick();
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
                saveLevelName += e.getKeyChar();
            }
        }
    }
}