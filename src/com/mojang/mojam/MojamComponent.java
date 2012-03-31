package com.mojang.mojam;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gui.AudioVideoMenu;
import com.mojang.mojam.gui.CharacterSelectionMenu;
import com.mojang.mojam.gui.CreditsScreen;
import com.mojang.mojam.gui.DifficultySelect;
import com.mojang.mojam.gui.ExitMenu;
import com.mojang.mojam.gui.GuiError;
import com.mojang.mojam.gui.HostingWaitMenu;
import com.mojang.mojam.gui.HowToPlayMenu;
import com.mojang.mojam.gui.JoinGameMenu;
import com.mojang.mojam.gui.KeyBindingsMenu;
import com.mojang.mojam.gui.LevelEditorMenu;
import com.mojang.mojam.gui.LevelSelect;
import com.mojang.mojam.gui.LocaleMenu;
import com.mojang.mojam.gui.MenuStack;
import com.mojang.mojam.gui.OptionsMenu;
import com.mojang.mojam.gui.PauseMenu;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.gui.WinMenu;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ButtonListener;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.mc.EnumOS2;
import com.mojang.mojam.mc.EnumOSMappingHelper;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.network.kryo.Network.ChangeKeyMessage;
import com.mojang.mojam.network.kryo.Network.ChangeMouseButtonMessage;
import com.mojang.mojam.network.kryo.Network.ChangeMouseCoordinateMessage;
import com.mojang.mojam.network.kryo.Network.CharacterMessage;
import com.mojang.mojam.network.kryo.Network.ChatMessage;
import com.mojang.mojam.network.kryo.Network.PauseMessage;
import com.mojang.mojam.network.kryo.Network.StartGameCustomMessage;
import com.mojang.mojam.network.kryo.Network.StartGameMessage;
import com.mojang.mojam.network.kryo.SnatchClient;
import com.mojang.mojam.network.kryo.SnatchServer;
import com.mojang.mojam.resources.Constants;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.AbstractBitmap;
import com.mojang.mojam.screen.AbstractScreen;
import com.mojang.mojam.screen.MojamScreen;
import com.mojang.mojam.sound.ISoundPlayer;
import com.mojang.mojam.sound.NoSoundPlayer;
import com.mojang.mojam.sound.SoundPlayer;

public class MojamComponent extends Canvas implements Runnable, MouseMotionListener, MouseListener, ButtonListener {

	public static final String GAME_TITLE = "Catacomb Snatch";
	public static final String GAME_VERSION = "1.1.1-SNAPSHOT";

	public static MojamComponent instance;
	public static Locale locale;
	public static Texts texts;
	public static Constants constants;
	private static final long serialVersionUID = 1L;
	public static final int GAME_WIDTH = 512;
	public static final int GAME_HEIGHT = GAME_WIDTH * 3 / 4;
	public int scale = 2;
	private static JFrame guiFrame;
	private boolean running = true;
	public boolean paused;
	private Cursor emptyCursor;
	private double framerate = 60;
	private int fps;
	public static MojamScreen screen;
	private Level level;
	public Chat chat = new Chat();
	public Console console = new Console();

	public int latency;
	
	public MenuStack menuStack = new MenuStack();

	private InputHandler inputHandler;
	private int lastX = 0;
	private int lastY = 0;
	private boolean mouseMoved = false;
	private int mouseHideTime = 0;
	public MouseButtons mouseButtons = new MouseButtons();
	public Keys keys = new Keys();
	public Keys[] synchedKeys = { new Keys(), new Keys() };
	public MouseButtons[] synchedMouseButtons = { new MouseButtons(), new MouseButtons() };
	public Player[] players = new Player[2];
	public Player player;
	public TurnSynchronizer synchronizer;
	
	private boolean isMultiplayer;
	public boolean isServer;
	public int localId;
	public static int localTeam; //local team is the team of the client. This can be used to check if something should be only rendered on one person's screen

	public GameCharacter playerCharacter;
	public boolean sendCharacter = false;

	private static boolean fullscreen = false;
	public static ISoundPlayer soundPlayer;
	private long nextMusicInterval = 0;
	private byte sShotCounter = 0;

	public int createServerState = 0;
	private static volatile File mojamDir = null;
	
	private TitleMenu menu = null;
	private LocaleMenu localemenu = null;
	private SnatchClient snatchClient;
	private SnatchServer server;

	public MojamComponent() {
		screen = new MojamScreen(GAME_WIDTH, GAME_HEIGHT);
		screen.loadResources();
	    final String nativeLibDir = MojamComponent.getMojamDir()
			.getAbsolutePath().toString()
			+ File.separator
			+ "bin"
			+ File.separator
			+ "native"
			+ File.separator;
	    System.setProperty("org.lwjgl.librarypath", nativeLibDir);
		// initialize the constants
		MojamComponent.constants = new Constants();

		this.setScale(Options.getAsInteger(Options.SCALE,2));

		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		String localeString = Options.get(Options.LOCALE, "en");
		setLocale(new Locale(localeString));

		menuStack.setStackButtonListener(this);
		menu = new TitleMenu(GAME_WIDTH, GAME_HEIGHT);
		menuStack.add(menu);
		addKeyListener(menuStack);
		addKeyListener(chat);
		addKeyListener(console);
		snatchClient = new SnatchClient();
		snatchClient.setComponent(this);
		instance = this;
	}

	public void setScale(int i) {
		this.scale = i;
		Dimension dim = new Dimension(GAME_WIDTH * scale, GAME_HEIGHT * scale);

		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
		this.setPreferredSize(dim);
		this.setSize(dim);		
		
		if(guiFrame != null) {
			guiFrame.setSize(new Dimension(guiFrame.getInsets().left + guiFrame.getInsets().right + GAME_WIDTH * scale,
					guiFrame.getInsets().top + guiFrame.getInsets().bottom + GAME_HEIGHT * scale));
		}
		
	}
	
	public void setLocale(String locale) {
		setLocale(new Locale(locale));
		Options.set(Options.LOCALE, locale);
		Options.saveProperties();
		notifyLocaleChange();
	}

	public void setLocale(Locale locale) {
		MojamComponent.locale = locale;
		MojamComponent.texts = new Texts(locale);
		Locale.setDefault(locale);
	}
	
	public void notifyLocaleChange(){
		MenuStack menuClone = (MenuStack) menuStack.clone();
		
		while (!menuClone.isEmpty()) {
			menuClone.pop().changeLocale();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved = true;
		lastX = e.getXOnScreen();
		lastY = e.getYOnScreen();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseMoved = true;
		lastX = e.getXOnScreen();
		lastY = e.getYOnScreen();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if ( Options.getAsBoolean(Options.TRAP_MOUSE, Options.VALUE_FALSE) ) {
			try {
	            Robot robot = new Robot();   
	            robot.mouseMove(lastX, lastY);
	        }
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }
		} else {
			mouseButtons.releaseAll();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseButtons.setNextState(e.getButton(), true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButtons.setNextState(e.getButton(), false);
	}

	@Override
	public void paint(Graphics g) {
	}

	@Override
	public void update(Graphics g) {
	}

	public void start() {
		running = true;
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop(boolean exit) {
	    if (exit) {		
		running = false;
		soundPlayer.stopBackgroundMusic();
		soundPlayer.shutdown();
		System.exit(0);
	    } else if (menuStack.empty()){
		if (level != null && !isMultiplayer && !paused) {
		    paused = true;
		    menuStack.add(new PauseMenu(GAME_WIDTH, GAME_HEIGHT));
		}
		menuStack.add(new ExitMenu(GAME_WIDTH, GAME_HEIGHT));
	    } else if(!(menuStack.peek() instanceof ExitMenu)){
		menuStack.add(new ExitMenu(GAME_WIDTH, GAME_HEIGHT));
	    }
	}

	private void init() {
		initInput();
		initCharacters();
		initLocale();

		soundPlayer = new SoundPlayer();
		if (soundPlayer.getSoundSystem() == null)
			soundPlayer = new NoSoundPlayer();

		soundPlayer.startTitleMusic();

		try {
			emptyCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "empty");
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		setFocusTraversalKeysEnabled(false);
		requestFocus();

		// hide cursor, since we're drawing our own one
		setCursor(emptyCursor);
	}

	private void initInput() {
		inputHandler = new InputHandler(keys);
		addKeyListener(inputHandler);
	}
	
	private void initLocale(){
		if(!Options.isLocaleSet()){
			menuStack.add(new LocaleMenu("select"));
		}
	}
	
	private void initCharacters(){
		if(!Options.isCharacterIDset()){
			menuStack.add(new CharacterSelectionMenu());
		}
		playerCharacter = GameCharacter.values()[Options.getCharacterID()];
	}

	public void showError(String s) {
		handleAction(TitleMenu.RETURN_TO_TITLESCREEN);
		menuStack.add(new GuiError(s));
	}

	public synchronized void createLevel(String levelPath, GameMode mode, GameCharacter character) {
		System.out.println("Creating level: "+levelPath);
		LevelInformation li = LevelInformation.getInfoForPath(levelPath);
		if (li != null) {
			createLevel(li, mode, character);
			return;
		} else if (!isMultiplayer) {
			showError("Missing map.");
		}
		showError("Missing map - Multiplayer");
	}

	private synchronized void createLevel(LevelInformation li, GameMode mode, GameCharacter character) {
		try {
			level = mode.generateLevel(li);
		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Unable to load map.");
			return;
		}
		initLevel(character);
		paused = false;
	}

	private synchronized void initLevel(GameCharacter character) {
		if (level == null)
			return;
		players[0] = new Player(synchedKeys[0], synchedMouseButtons[0], level.width * Tile.WIDTH
				/ 2 - 16, (level.height - 5 - 1) * Tile.HEIGHT - 16, Team.Team1, character);
		players[0].setFacing(4);
		level.addEntity(players[0]);
		if (isMultiplayer) {
			players[1] = new Player(synchedKeys[1], synchedMouseButtons[1], level.width
					* Tile.WIDTH / 2 - 16, 7 * Tile.HEIGHT - 16, Team.Team2, character);
			level.addEntity(players[1]);
		} else {
			players[1] = null;
		}
		player = players[localId];
		player.setCanSee(true);
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double unprocessed = 0;
		int frames = 0;
		long lastTimer1 = System.currentTimeMillis();

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		int toTick = 0;

		long lastRenderTime = System.nanoTime();
		int min = 999999999;
		int max = 0;

		while (running) {
			if (!this.hasFocus()) {
				keys.release();
			}

			double nsPerTick = 1000000000.0 / framerate;
			boolean shouldRender = false;
			while (unprocessed >= 1) {
				toTick++;
				unprocessed -= 1;
			}

			int tickCount = toTick;
			if (toTick > 0 && toTick < 3) {
				tickCount = 1;
			}
			if (toTick > 20) {
				toTick = 20;
			}

			for (int i = 0; i < tickCount; i++) {
				toTick--;
				tick();
				shouldRender = true;
			}

			BufferStrategy bs = getBufferStrategy();
			if (bs == null) {
				createBufferStrategy(3);
				continue;
			}

			if (shouldRender) {
				frames++;
				Graphics g = bs.getDrawGraphics();

				Random lastRandom = TurnSynchronizer.synchedRandom;

				render(g);

				TurnSynchronizer.synchedRandom = lastRandom;

				long renderTime = System.nanoTime();
				int timePassed = (int) (renderTime - lastRenderTime);
				if (timePassed < min) {
					min = timePassed;
				}
				if (timePassed > max) {
					max = timePassed;
				}
				lastRenderTime = renderTime;
			}

			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (shouldRender) {
				if (bs != null) {
					bs.show();
				}
			}

			if (System.currentTimeMillis() - lastTimer1 > 1000) {
				lastTimer1 += 1000;
				fps = frames;
				frames = 0;
			}
		}
	}

	private synchronized void render(Graphics g) {
		if (level != null) {
			int xScroll = (int) (player.pos.x - screen.getWidth() / 2);
			int yScroll = (int) (player.pos.y - (screen.getHeight() - 24) / 2);
			soundPlayer.setListenerPosition((float) player.pos.x, (float) player.pos.y);
			level.render(screen, xScroll, yScroll);
		}
		if (!menuStack.isEmpty()) {
			menuStack.peek().render(screen);
		}

		if (Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)) {
			Font.defaultFont().draw(screen, texts.FPS(fps), GAME_WIDTH - 73, 10);
		}

		if (player != null && menuStack.size() == 0) {
			addHealthBar(screen);
			addXpBar(screen);
			addScore(screen);
			addWeaponSlots(screen);

			Font font = Font.defaultFont();
			if (isMultiplayer) {
				font.draw(screen, texts.latency(latency), GAME_WIDTH - 105, 20);
			}
		}

		if (isMultiplayer && menuStack.isEmpty()) {
			chat.render(screen);
		}
		if(console.isOpen() && menuStack.isEmpty()) {
			console.render(screen);
		}

		g.setColor(Color.BLACK);

		g.fillRect(0, 0, getWidth(), getHeight());
		g.translate((getWidth() - GAME_WIDTH * scale) / 2, (getHeight() - GAME_HEIGHT * scale) / 2);
		g.clipRect(0, 0, GAME_WIDTH * scale, GAME_HEIGHT * scale);

		if (!menuStack.isEmpty() || level != null) {

			// render mouse
			renderMouse(screen, mouseButtons);
			BufferedImage image = toCompatibleImage(screen.image);
			g.drawImage(image, 0, 0, GAME_WIDTH * scale, GAME_HEIGHT * scale, null);
		}

	}
	
	private BufferedImage toCompatibleImage(BufferedImage image)
	{
	        // obtain the current system graphical settings
	        GraphicsConfiguration gfx_config = GraphicsEnvironment.
	                getLocalGraphicsEnvironment().getDefaultScreenDevice().
	                getDefaultConfiguration();

	        /*
	         * if image is already compatible and optimized for current system 
	         * settings, simply return it
	         */
	        if (image.getColorModel().equals(gfx_config.getColorModel()))
	                return image;

	        // image is not optimized, so create a new image that is
	        BufferedImage new_image = gfx_config.createCompatibleImage(
	                        image.getWidth(), image.getHeight(), image.getTransparency());

	        // get the graphics context of the new image to draw the old image on
	        Graphics2D g2d = (Graphics2D) new_image.getGraphics();

	        // actually draw the image and dispose of context no longer needed
	        g2d.drawImage(image, 0, 0, null);
	        g2d.dispose();

	        // return the new optimized image
	        return new_image; 
	}


	private void addHealthBar(AbstractScreen screen) {
		int maxIndex = Art.panel_healthBar[0].length - 1;
		int index = maxIndex - Math.round(player.health * maxIndex / player.maxHealth);
		if (index < 0)
			index = 0;
		else if (index > maxIndex)
			index = maxIndex;

		screen.blit(Art.panel_healthBar[0][index], 311, screen.getHeight() - 17);
		screen.blit(Art.panel_heart, 314, screen.getHeight() - 24);
		Font font = Font.defaultFont();
		font.draw(screen, texts.health(player.health, player.maxHealth), 335, screen.getHeight() - 21);
	}

	private void addXpBar(AbstractScreen screen) {

		int xpSinceLastLevelUp = (int) (player.xpSinceLastLevelUp());
		int xpNeededForNextLevel = (int) (player.nettoXpNeededForLevel(player.plevel + 1));

		int maxIndex = Art.panel_xpBar[0].length - 1;
		int index = maxIndex - Math.round(xpSinceLastLevelUp * maxIndex / xpNeededForNextLevel);
		if (index < 0)
			index = 0;
		else if (index > maxIndex)
			index = maxIndex;

		screen.blit(Art.panel_xpBar[0][index], 311, screen.getHeight() - 32);
		screen.blit(Art.panel_star, 314, screen.getHeight() - 40);
		Font font = Font.defaultFont();
		font.draw(screen, texts.playerLevel(player.plevel + 1), 335, screen.getHeight() - 36);
	}
	
	private void addWeaponSlots(AbstractScreen screen) {
		Font font = Font.FONT_GOLD;
		for(int i = 0; i < 3 && i < player.weaponInventory.size(); i++) {
			if(i == player.getActiveWeaponSlot()) {
				screen.alphaFill(2 + i*32, 2, 30, 30, 0xffaaaaaa, 0x30);
			}
			else {
				screen.alphaFill(2 + i*32, 2, 30, 30, 0xff000000, 0x30);
			}
			screen.blit(player.weaponInventory.get(i).getSprite(), 2 + i*32, 2);
			font.draw(screen, texts.playerWeaponSlot(i+1), 14 + i*32, 30);
		}
	}

	private void addScore(AbstractScreen screen) {
		screen.blit(Art.panel_coin, 314, screen.getHeight() - 55);
		Font font = Font.defaultFont();
		font.draw(screen, texts.money(player.score), 335, screen.getHeight() - 52);
	}

	private void renderMouse(AbstractScreen screen, MouseButtons mouseButtons) {

		if (mouseButtons.mouseHidden)
			return;

		int crosshairSize = 15;
		int crosshairSizeHalf = crosshairSize / 2;

		AbstractBitmap marker = screen.createBitmap(crosshairSize, crosshairSize);

		// horizontal line
		for (int i = 0; i < crosshairSize; i++) {
			if (i >= crosshairSizeHalf - 1 && i <= crosshairSizeHalf + 1)
				continue;

			marker.setPixel(crosshairSizeHalf + i * crosshairSize, 0xffffffff);
			marker.setPixel(i + crosshairSizeHalf * crosshairSize, 0xffffffff);
		}

		screen.blit(marker, mouseButtons.getX() - crosshairSizeHalf - 2, mouseButtons.getY() - crosshairSizeHalf - 2);
	}

	private void tick() {
		//Console open/close
		if(this.isFocusOwner() && level != null) {
			keys.console.tick();
			if(keys.console.wasPressed()) {
				console.toggle();
			}
			if(console.isOpen()) {
				if(menuStack.isEmpty()) {
					keys.release();
					mouseButtons.releaseAll();
				}
				console.tick();
			}
		}
		// Not-In-Focus-Pause
		if (level != null && !isMultiplayer && !paused && !this.isFocusOwner()) {
			keys.release();
			mouseButtons.releaseAll();
			synchronizer.addMessage(new PauseMessage());
			paused = true;
		}

		if (requestToggleFullscreen || keys.fullscreen.wasPressed()) {
			requestToggleFullscreen = false;
			setFullscreen(!fullscreen);
		}

		if (level != null && level.victoryConditions != null) {
			if (level.victoryConditions.isVictoryConditionAchieved()) {
				int winner = level.victoryConditions.playerVictorious();
				GameCharacter winningCharacter = winner == players[0].getTeam() ? players[0].getCharacter()
						: players[1].getCharacter();
				menuStack.add(new WinMenu(GAME_WIDTH, GAME_HEIGHT, winner, winningCharacter));
                level = null;
                return;
            }
        }
		
		if (snatchClient != null) {
			snatchClient.tick();
		}

		// Store virtual position of mouse (pre-scaled)
		Point mousePosition = getMousePosition();
		if (mousePosition != null) {
			mouseButtons.setPosition(mousePosition.x / scale, mousePosition.y / scale);
		}
		if (!menuStack.isEmpty()) {
			menuStack.peek().tick(mouseButtons);
		}
		if (mouseMoved) {
			mouseMoved = false;
			mouseHideTime = 0;
			if (mouseButtons.mouseHidden) {
				mouseButtons.mouseHidden = false;
			}
		}
		if (mouseHideTime < 60) {
			mouseHideTime++;
			if (mouseHideTime == 60) {
				mouseButtons.mouseHidden = true;
			}
		}

		if (sendCharacter) {
			synchronizer.addMessage(new CharacterMessage(localId, playerCharacter.ordinal()));
			sendCharacter = false;
		}

		if (level == null) {
			mouseButtons.tick();
		} else if (level != null) {
			if (synchronizer.preTurn()) {
				synchronizer.postTurn();

				for (int index = 0; index < mouseButtons.currentState.length; index++) {
					boolean nextState = mouseButtons.nextState[index];
					if (mouseButtons.isDown(index) != nextState) {
						synchronizer.addMessage(new ChangeMouseButtonMessage(index, nextState));
					}
				}
				synchronizer.addMessage(new ChangeMouseCoordinateMessage(mouseButtons.getX(), mouseButtons.getY(), mouseButtons.mouseHidden));
				mouseButtons.tick();
				for (MouseButtons sMouseButtons : synchedMouseButtons) {
					sMouseButtons.tick();
				}

				if (!paused) {
					for (int index = 0; index < keys.getAll().size(); index++) {
						Keys.Key key = keys.getAll().get(index);
						boolean nextState = key.nextState;
						if (key.isDown != nextState) {
							synchronizer.addMessage(new ChangeKeyMessage(index, nextState));
						}
					}

					keys.tick();
					for (Keys skeys : synchedKeys) {
						skeys.tick();
					}

					if (keys.pause.wasPressed()) {
						keys.release();
						mouseButtons.releaseAll();
						synchronizer.addMessage(new PauseMessage());
					}

					level.tick();
					if (isMultiplayer) {
						tickChat();
					}
				}

				// every 4 minutes, start new background music :)
				if (System.currentTimeMillis() / 1000 > nextMusicInterval) {
					nextMusicInterval = (System.currentTimeMillis() / 1000) + 4 * 60;
					soundPlayer.startBackgroundMusic();
				}

				if (keys.screenShot.isDown) {
					takeScreenShot();
				}
			}

		}

		if (createServerState == 1) {
			createServerState = 2;

			synchronizer = new TurnSynchronizer(snatchClient, localId, 2);

			menuStack.clear();
			createLevel(TitleMenu.level, TitleMenu.defaultGameMode, playerCharacter);

			synchronizer.setStarted(true);
			if (TitleMenu.level.vanilla) {
				
				
				snatchClient.sendMessage(new StartGameMessage(TurnSynchronizer.synchedSeed,
						TitleMenu.level.getUniversalPath(), TitleMenu.difficulty.ordinal(), playerCharacter.ordinal()));
				
			
			} else {
				snatchClient.sendMessage(new StartGameCustomMessage(TurnSynchronizer.synchedSeed,
						level, TitleMenu.difficulty.ordinal(),
						playerCharacter.ordinal()));
			}
			

		}
	}

	private void tickChat() {
		if (chat.isOpen()) {
			keys.release();
		}

		if (keys.chat.wasReleased()) {
			chat.open();
		}

		chat.tick();

		String msg = chat.getWaitingMessage();
		if (msg != null) {
			synchronizer.addMessage(new ChatMessage(texts.playerNameCharacter(playerCharacter) + ": " + msg));
		}
	}
	
	public static void main(String[] args){
	    System.err.println("YOU SHOULD CHANGE YOUR STARTUP COMPONENT TO MojamStartup!");
	    MojamComponent.startgame();
	}

	public static void startgame() {
		Options.loadProperties();
		MojamComponent mc = new MojamComponent();
		System.out.println("Starting "+(Options.getAsBoolean(Options.OPENGL,Options.VALUE_FALSE)?"with":"without")+" OpenGL support");
		System.setProperty("sun.java2d.opengl", Options.get(Options.OPENGL,Options.VALUE_FALSE));
		//True for verbose console output, true for silent
		guiFrame = new JFrame(GAME_TITLE);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mc);
		guiFrame.setContentPane(panel);
		guiFrame.pack();
		guiFrame.setResizable(false);
		guiFrame.setSize(new Dimension(guiFrame.getInsets().left + guiFrame.getInsets().right + GAME_WIDTH * mc.scale,
				guiFrame.getInsets().top + guiFrame.getInsets().bottom + GAME_HEIGHT * mc.scale));
		guiFrame.setLocationRelativeTo(null);
		guiFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		guiFrame.addWindowListener(newWindowClosinglistener());
		ArrayList<BufferedImage> icoList = new ArrayList<BufferedImage>();
		icoList.add(Art.icon32);
		icoList.add(Art.icon64);
		guiFrame.setIconImages(icoList);
		guiFrame.setVisible(true);
		setFullscreen(Boolean.parseBoolean(Options.get(Options.FULLSCREEN, Options.VALUE_FALSE)));
		mc.start();
	}

	private static void setFullscreen(boolean fs) {
		if (fs != fullscreen) {
			GraphicsDevice device = guiFrame.getGraphicsConfiguration().getDevice();
			// hide window
			guiFrame.setVisible(false);
			guiFrame.dispose();
			// change options
			guiFrame.setUndecorated(fs);
			device.setFullScreenWindow(fs ? guiFrame : null);
			// display window
			guiFrame.setLocationRelativeTo(null);
			guiFrame.setVisible(true);
			instance.requestFocusInWindow();
			fullscreen = fs;
		}
		Options.set(Options.FULLSCREEN, fullscreen);
	}

	private static volatile boolean requestToggleFullscreen = false;

	public static void toggleFullscreen() {
		requestToggleFullscreen = true; // only toggle fullscreen in the tick()
										// loop
	}

	public static boolean isFullscreen() {
		return fullscreen;
	}

	@Override
	public void buttonPressed(ClickableComponent component) {
		if (component instanceof Button) {
			final Button button = (Button) component;
			handleAction(button.getId());
		}
	}

	@Override
	public void buttonHovered(ClickableComponent clickableComponent) {
	}

	public void handleAction(int id) {
		switch (id) {
		case TitleMenu.LOCALE_EN_ID:
			setLocale("en");
			break;
		case TitleMenu.LOCALE_DE_ID:
			setLocale("de");
			break;
		case TitleMenu.LOCALE_ES_ID:
			setLocale("es");
			break;
		case TitleMenu.LOCALE_FR_ID:
			setLocale("fr");
			break;
		case TitleMenu.LOCALE_IND_ID:
			setLocale("ind");
			break;
		case TitleMenu.LOCALE_IT_ID:
			setLocale("it");
			break;
		case TitleMenu.LOCALE_NL_ID:
			setLocale("nl");
			break;
		case TitleMenu.LOCALE_PL_ID:
			setLocale("pl");
			break;
		case TitleMenu.LOCALE_PT_BR_ID:
			setLocale("pt_br");
			break;
		case TitleMenu.LOCALE_RU_ID:
			setLocale("ru");
			break;
		case TitleMenu.LOCALE_SL_ID:
			setLocale("sl");
			break;
		case TitleMenu.LOCALE_SV_ID:
			setLocale("sv");
			break;
		case TitleMenu.LOCALE_AF_ID:
			setLocale("af");
			break;
		case TitleMenu.RETURN_TO_TITLESCREEN:
			if(isMultiplayer) {
				snatchClient.shutdown();
				if(isServer) {
					server.shutdown();
				}
			}
			menuStack.clear();
			level = null;
			TitleMenu menu = new TitleMenu(GAME_WIDTH, GAME_HEIGHT);
			menuStack.add(menu);
			this.nextMusicInterval = 0;
			soundPlayer.stopBackgroundMusic();
			soundPlayer.startTitleMusic();
			break;

		case TitleMenu.START_GAME_ID:
			menuStack.clear();
			isMultiplayer = false;
			chat.clear();

			localId = 0;
			MojamComponent.localTeam = Team.Team1;
			synchronizer = new TurnSynchronizer(snatchClient, 0, 1);
			synchronizer.setStarted(true);

			createLevel(TitleMenu.level, TitleMenu.defaultGameMode, playerCharacter);
			soundPlayer.stopBackgroundMusic();
			break;

		case TitleMenu.SELECT_LEVEL_ID:
			menuStack.add(new LevelSelect(false));
			break;

		case TitleMenu.SELECT_HOST_LEVEL_ID:
			menuStack.add(new LevelSelect(true));
			break;
		case TitleMenu.HOST_GAME_ID:
			menuStack.add(new HostingWaitMenu());
			isMultiplayer = true;
			isServer = true;
			chat.clear();
			try {
				if (isServer) {
					localId = 0;
					MojamComponent.localTeam = Team.Team1;

					server = new SnatchServer();
					
					snatchClient.setComponent(MojamComponent.this);
					snatchClient.connectLocal();				
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case TitleMenu.JOIN_GAME_ID:
			menuStack.add(new JoinGameMenu());
			break;

		case TitleMenu.CANCEL_JOIN_ID:
			menuStack.safePop();
			if(isMultiplayer) {
				snatchClient.shutdown();
				if(isServer) {
					server.shutdown();
				}
			}
			break;

		case TitleMenu.PERFORM_JOIN_ID:
			menuStack.clear();
			isMultiplayer = true;
			isServer = false;
			chat.clear();

			String[] data = TitleMenu.ip.trim().split(":");
			String ip = data[0];
			Integer port = (data.length > 1) ? Integer.parseInt(data[1]) : Options.getAsInteger(Options.MP_PORT, 3000);

			try {
				localId = 1;
				MojamComponent.localTeam = Team.Team2;
	
				snatchClient.setComponent(MojamComponent.this);
				snatchClient.connect(ip, port);

				synchronizer = new TurnSynchronizer(snatchClient, localId, 2);
			} catch (Exception e) {
				e.printStackTrace();
				menuStack.add(new TitleMenu(GAME_WIDTH, GAME_HEIGHT));
			}
			break;

		case TitleMenu.HOW_TO_PLAY:
			menuStack.add(new HowToPlayMenu(level != null));
			break;

		case TitleMenu.OPTIONS_ID:
			menuStack.add(new OptionsMenu(level != null));
			break;

		case TitleMenu.SELECT_DIFFICULTY_ID:
			menuStack.add(new DifficultySelect(false));
			break;

		case TitleMenu.SELECT_DIFFICULTY_HOSTING_ID:
			menuStack.add(new DifficultySelect(true));
			break;

		case TitleMenu.KEY_BINDINGS_ID:
			menuStack.add(new KeyBindingsMenu(keys, inputHandler));
			break;

		case TitleMenu.LEVEL_EDITOR_ID:
			menuStack.add(new LevelEditorMenu());
			break;

		case TitleMenu.EXIT_GAME_ID:
			stop(false);
			break;

		case TitleMenu.REALLY_EXIT_GAME_ID:
			stop(true);
			break;

		case TitleMenu.RETURN_ID:
			synchronizer.addMessage(new PauseMessage(false));
			keys.tick();
			break;

		case TitleMenu.BACK_ID:
			menuStack.safePop();
			break;

		case TitleMenu.CREDITS_ID:
			menuStack.add(new CreditsScreen(GAME_WIDTH, GAME_HEIGHT));
			break;

		case TitleMenu.CHARACTER_ID:
			menuStack.add(new CharacterSelectionMenu());
			break;

		case TitleMenu.AUDIO_VIDEO_ID:
			menuStack.add(new AudioVideoMenu(level != null));
			break;

		case TitleMenu.LOCALE_ID:
			localemenu = new LocaleMenu(level != null);
			menuStack.add(localemenu);
			break;
		}
	}

	public static File getMojamDir() {
		if (mojamDir == null) {
			mojamDir = getAppDir("mojam");
		}
		return mojamDir;
	}

	public static EnumOS2 getOs() {
		String s = System.getProperty("os.name").toLowerCase();
		if (s.contains("win")) {
			return EnumOS2.windows;
		}
		if (s.contains("mac")) {
			return EnumOS2.macos;
		}
		if (s.contains("solaris")) {
			return EnumOS2.solaris;
		}
		if (s.contains("sunos")) {
			return EnumOS2.solaris;
		}
		if (s.contains("linux")) {
			return EnumOS2.linux;
		}
		if (s.contains("unix")) {
			return EnumOS2.linux;
		} else {
			return EnumOS2.unknown;
		}
	}

	public static File getAppDir(String s) {
		String s1 = System.getProperty("user.home", ".");
		File file;
		switch (EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()]) {
		case 1: // '\001'
		case 2: // '\002'
			file = new File(s1, (new StringBuilder()).append('.').append(s).append('/').toString());
			break;

		case 3: // '\003'
			String s2 = System.getenv("APPDATA");
			if (s2 != null) {
				file = new File(s2, (new StringBuilder()).append(".").append(s).append('/').toString());
			} else {
				file = new File(s1, (new StringBuilder()).append('.').append(s).append('/').toString());
			}
			break;

		case 4: // '\004'
			file = new File(s1, (new StringBuilder()).append("Library/Application Support/").append(s).toString());
			break;

		default:
			file = new File(s1, (new StringBuilder()).append(s).append('/').toString());
			break;
		}
		if (!file.exists() && !file.mkdirs()) {
			throw new RuntimeException((new StringBuilder()).append("The working directory could not be created: ").append(file).toString());
		} else {
			return file;
		}
	}

	public void takeScreenShot() {
		BufferedImage screencapture;

		try {
			screencapture = new Robot().createScreenCapture(guiFrame.getBounds());

			File file = new File(getMojamDir() + "/" + "screenShot" + sShotCounter++ + ".png");
			while (file.exists()) {
				file = new File(getMojamDir() + "/" + "screenShot" + sShotCounter++ + ".png");
			}

			ImageIO.write(screencapture, "png", file);
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int clampi(int val, int min, int max){
		return (val < min) ? min : (val > max) ? max : val;
	}
	
	private static WindowListener newWindowClosinglistener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				MojamComponent.instance.stop(false);
			}
		};
	}

}
