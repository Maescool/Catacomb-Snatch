package com.mojang.mojam;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gui.AudioVideoMenu;
import com.mojang.mojam.gui.Button;
import com.mojang.mojam.gui.ButtonListener;
import com.mojang.mojam.gui.CharacterSelectionMenu;
import com.mojang.mojam.gui.ClickableComponent;
import com.mojang.mojam.gui.CreditsScreen;
import com.mojang.mojam.gui.DifficultySelect;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.GuiError;
import com.mojang.mojam.gui.GuiMenu;
import com.mojang.mojam.gui.HostingWaitMenu;
import com.mojang.mojam.gui.HowToPlayMenu;
import com.mojang.mojam.gui.JoinGameMenu;
import com.mojang.mojam.gui.KeyBindingsMenu;
import com.mojang.mojam.gui.LevelEditorMenu;
import com.mojang.mojam.gui.LevelSelect;
import com.mojang.mojam.gui.LocaleMenu;
import com.mojang.mojam.gui.OptionsMenu;
import com.mojang.mojam.gui.PauseMenu;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.gui.WinMenu;
import com.mojang.mojam.level.DifficultyList;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.mc.EnumOS2;
import com.mojang.mojam.mc.EnumOSMappingHelper;
import com.mojang.mojam.network.ClientSidePacketLink;
import com.mojang.mojam.network.CommandListener;
import com.mojang.mojam.network.NetworkCommand;
import com.mojang.mojam.network.NetworkPacketLink;
import com.mojang.mojam.network.Packet;
import com.mojang.mojam.network.PacketLink;
import com.mojang.mojam.network.PacketListener;
import com.mojang.mojam.network.PauseCommand;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.network.packet.ChangeKeyCommand;
import com.mojang.mojam.network.packet.ChangeMouseButtonCommand;
import com.mojang.mojam.network.packet.ChangeMouseCoordinateCommand;
import com.mojang.mojam.network.packet.CharacterCommand;
import com.mojang.mojam.network.packet.ChatCommand;
import com.mojang.mojam.network.packet.PingPacket;
import com.mojang.mojam.network.packet.StartGamePacket;
import com.mojang.mojam.network.packet.StartGamePacketCustom;
import com.mojang.mojam.network.packet.TurnPacket;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.sound.ISoundPlayer;
import com.mojang.mojam.sound.NoSoundPlayer;
import com.mojang.mojam.sound.SoundPlayer;

public class MojamComponent extends Canvas implements Runnable, MouseMotionListener, CommandListener, PacketListener, MouseListener, ButtonListener, KeyListener {

	public static final String GAME_TITLE = "Catacomb Snatch";
	public static final String GAME_VERSION = "1.0.0-SNAPSHOT";

	public static MojamComponent instance;
	public static Locale locale;
	public static Texts texts;
	private static final long serialVersionUID = 1L;
	public static final int GAME_WIDTH = 512;
	public static final int GAME_HEIGHT = GAME_WIDTH * 3 / 4;
	public static final int SCALE = 2;
	private static JFrame guiFrame;
	private boolean running = true;
	private boolean paused;
	private Cursor emptyCursor;
	private double framerate = 60;
	private int fps;
	public static Screen screen = new Screen(GAME_WIDTH, GAME_HEIGHT);
	private Level level;
	private Chat chat = new Chat();
	
	private LatencyCache latencyCache = new LatencyCache();

	private Stack<GuiMenu> menuStack = new Stack<GuiMenu>();

	private InputHandler inputHandler;
	private boolean mouseMoved = false;
	private int mouseHideTime = 0;
	public MouseButtons mouseButtons = new MouseButtons();
	public Keys keys = new Keys();
	public Keys[] synchedKeys = { new Keys(), new Keys() };
	public MouseButtons[] synchedMouseButtons = { new MouseButtons(), new MouseButtons() };
	public Player[] players = new Player[2];
	public Player player;
	public TurnSynchronizer synchronizer;
	private PacketLink packetLink;
	private ServerSocket serverSocket;
	private boolean isMultiplayer;
	private boolean isServer;
	private int localId;
	public static int localTeam; //local team is the team of the client. This can be used to check if something should be only rendered on one person's screen

	public GameCharacter playerCharacter;
	private boolean sendCharacter = false;

	private Thread hostThread;
	private static boolean fullscreen = false;
	public static ISoundPlayer soundPlayer;
	private long nextMusicInterval = 0;
	private byte sShotCounter = 0;

	private int createServerState = 0;
	private static File mojamDir = null;
	
	private TitleMenu menu = null;
	private LocaleMenu localemenu = null;

	public MojamComponent() {

		this.setPreferredSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT * SCALE));
		this.setMinimumSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT * SCALE));
		this.setMaximumSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT * SCALE));

		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		String localeString = Options.get(Options.LOCALE, "en");
		setLocale(new Locale(localeString));

		menu = new TitleMenu(GAME_WIDTH, GAME_HEIGHT);
		addMenu(menu);
		addKeyListener(this);
		addKeyListener(chat);

		instance = this;
		LevelList.createLevelList();
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
	
	@SuppressWarnings("unchecked")
	public void notifyLocaleChange(){
		Stack<GuiMenu> menuClone = (Stack<GuiMenu>) menuStack.clone();
		
		while (!menuClone.isEmpty()) {
			menuClone.pop().changeLocale();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mouseMoved = true;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouseMoved = true;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseButtons.releaseAll();
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

	public void stop() {
		running = false;
		soundPlayer.stopBackgroundMusic();
		soundPlayer.shutdown();
	}

	private void init() {
		initInput();
		initCharacters();

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
	
	private void initCharacters(){
		if(!Options.isCharacterIDset()){
			addMenu(new CharacterSelectionMenu());
		}
		playerCharacter = GameCharacter.values()[Options.getCharacterID()];
	}

	public void showError(String s) {
		handleAction(TitleMenu.RETURN_TO_TITLESCREEN);
		addMenu(new GuiError(s));
	}

	private synchronized void createLevel(String levelPath, GameMode mode, GameCharacter character) {
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
			//level = Level.fromFile(li);
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
		// level.init();
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

		// if (!isMultiplayer) {
		// createLevel();
		// }

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
				// long before = System.nanoTime();
				tick();
				// long after = System.nanoTime();
				// System.out.println("Tick time took " + (after - before) *
				// 100.0 / nsPerTick + "% of the max time");
				shouldRender = true;
			}
			// shouldRender = true;

			BufferStrategy bs = getBufferStrategy();
			if (bs == null) {
				createBufferStrategy(3);
				continue;
			}

			if (shouldRender) {
				frames++;
				Graphics g = bs.getDrawGraphics();

				Random lastRandom = TurnSynchronizer.synchedRandom;
				TurnSynchronizer.synchedRandom = null;

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
			int xScroll = (int) (player.pos.x - screen.w / 2);
			int yScroll = (int) (player.pos.y - (screen.h - 24) / 2);
			soundPlayer.setListenerPosition((float) player.pos.x, (float) player.pos.y);
			level.render(screen, xScroll, yScroll);
		}
		if (!menuStack.isEmpty()) {
			menuStack.peek().render(screen);
		}

		if (Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)) {
			Font.defaultFont().draw(screen, texts.FPS(fps), 10, 10);
		}

		if (player != null && menuStack.size() == 0) {
			addHealthBar(screen);
			addXpBar(screen);
			addScore(screen);

			Font font = Font.defaultFont();
			if (isMultiplayer) {
				font.draw(screen, texts.latency(latencyCache.latencyCacheReady() ? "" + latencyCache.avgLatency() : "-"), 10, 20);
			}
		}

		if (isMultiplayer && menuStack.isEmpty()) {
			chat.render(screen);
		}

		g.setColor(Color.BLACK);

		g.fillRect(0, 0, getWidth(), getHeight());
		g.translate((getWidth() - GAME_WIDTH * SCALE) / 2, (getHeight() - GAME_HEIGHT * SCALE) / 2);
		g.clipRect(0, 0, GAME_WIDTH * SCALE, GAME_HEIGHT * SCALE);

		if (!menuStack.isEmpty() || level != null) {

			// render mouse
			renderMouse(screen, mouseButtons);

			g.drawImage(screen.image, 0, 0, GAME_WIDTH * SCALE, GAME_HEIGHT * SCALE, null);
		}

	}

	private void addHealthBar(Screen screen) {
		int maxIndex = Art.panel_healthBar[0].length - 1;
		int index = maxIndex - Math.round(player.health * maxIndex / player.maxHealth);
		if (index < 0)
			index = 0;
		else if (index > maxIndex)
			index = maxIndex;

		screen.blit(Art.panel_healthBar[0][index], 311, screen.h - 17);
		screen.blit(Art.panel_heart, 314, screen.h - 24);
		Font font = Font.defaultFont();
		font.draw(screen, texts.health(player.health, player.maxHealth), 335, screen.h - 21);
	}

	private void addXpBar(Screen screen) {

		int xpSinceLastLevelUp = (int) (player.xpSinceLastLevelUp());
		int xpNeededForNextLevel = (int) (player.nettoXpNeededForLevel(player.plevel + 1));

		int maxIndex = Art.panel_xpBar[0].length - 1;
		int index = maxIndex - Math.round(xpSinceLastLevelUp * maxIndex / xpNeededForNextLevel);
		if (index < 0)
			index = 0;
		else if (index > maxIndex)
			index = maxIndex;

		screen.blit(Art.panel_xpBar[0][index], 311, screen.h - 32);
		screen.blit(Art.panel_star, 314, screen.h - 40);
		Font font = Font.defaultFont();
		font.draw(screen, texts.playerLevel(player.plevel + 1), 335, screen.h - 36);
	}

	private void addScore(Screen screen) {
		screen.blit(Art.panel_coin, 314, screen.h - 55);
		Font font = Font.defaultFont();
		font.draw(screen, texts.money(player.score), 335, screen.h - 52);
	}

	private void renderMouse(Screen screen, MouseButtons mouseButtons) {

		if (mouseButtons.mouseHidden)
			return;

		int crosshairSize = 15;
		int crosshairSizeHalf = crosshairSize / 2;

		Bitmap marker = new Bitmap(crosshairSize, crosshairSize);

		// horizontal line
		for (int i = 0; i < crosshairSize; i++) {
			if (i >= crosshairSizeHalf - 1 && i <= crosshairSizeHalf + 1)
				continue;

			marker.pixels[crosshairSizeHalf + i * crosshairSize] = 0xffffffff;
			marker.pixels[i + crosshairSizeHalf * crosshairSize] = 0xffffffff;
		}

		screen.blit(marker, mouseButtons.getX() / SCALE - crosshairSizeHalf - 2, mouseButtons.getY() / SCALE - crosshairSizeHalf - 2);
	}

	private void tick() {
		// Not-In-Focus-Pause
		if (level != null && !isMultiplayer && !paused && !this.isFocusOwner()) {
			keys.release();
			mouseButtons.releaseAll();
			PauseCommand pauseCommand = new PauseCommand(true);
			synchronizer.addCommand(pauseCommand);
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
				addMenu(new WinMenu(GAME_WIDTH, GAME_HEIGHT, winner, winningCharacter));
                level = null;
                return;
            }
        }
		
		if (packetLink != null) {
			packetLink.tick();
		}

		mouseButtons.setPosition(getMousePosition());
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
			synchronizer.addCommand(new CharacterCommand(localId, playerCharacter.ordinal()));
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
						synchronizer.addCommand(new ChangeMouseButtonCommand(index, nextState));
					}
				}

				synchronizer.addCommand(new ChangeMouseCoordinateCommand(mouseButtons.getX(), mouseButtons.getY(), mouseButtons.mouseHidden));

				mouseButtons.tick();
				for (MouseButtons sMouseButtons : synchedMouseButtons) {
					sMouseButtons.tick();
				}

				if (!paused) {
					for (int index = 0; index < keys.getAll().size(); index++) {
						Keys.Key key = keys.getAll().get(index);
						boolean nextState = key.nextState;
						if (key.isDown != nextState) {
							synchronizer.addCommand(new ChangeKeyCommand(index, nextState));
						}
					}

					keys.tick();
					for (Keys skeys : synchedKeys) {
						skeys.tick();
					}

					if (keys.pause.wasPressed()) {
						keys.release();
						mouseButtons.releaseAll();
						synchronizer.addCommand(new PauseCommand(true));
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

			synchronizer = new TurnSynchronizer(MojamComponent.this, packetLink, localId, 2);

			clearMenus();
			createLevel(TitleMenu.level, TitleMenu.defaultGameMode, playerCharacter);

			synchronizer.setStarted(true);
			if (TitleMenu.level.vanilla) {
				packetLink.sendPacket(new StartGamePacket(TurnSynchronizer.synchedSeed,
						TitleMenu.level.getUniversalPath(), DifficultyList
								.getDifficultyID(TitleMenu.difficulty), playerCharacter.ordinal()));
			} else {
				packetLink.sendPacket(new StartGamePacketCustom(TurnSynchronizer.synchedSeed,
						level, DifficultyList.getDifficultyID(TitleMenu.difficulty),
						playerCharacter.ordinal()));
			}
			packetLink.setPacketListener(MojamComponent.this);

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
			synchronizer.addCommand(new ChatCommand(texts.playerNameCharacter(playerCharacter) + ": " + msg));
		}
	}

	public static void main(String[] args) {
		Options.loadProperties();
		MojamComponent mc = new MojamComponent();
		guiFrame = new JFrame(GAME_TITLE);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mc);
		guiFrame.setContentPane(panel);
		guiFrame.pack();
		guiFrame.setResizable(false);
		guiFrame.setLocationRelativeTo(null);
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	public void handle(int playerId, NetworkCommand packet) {

		if (packet instanceof ChangeKeyCommand) {
			ChangeKeyCommand ckc = (ChangeKeyCommand) packet;
			synchedKeys[playerId].getAll().get(ckc.getKey()).nextState = ckc.getNextState();
		}

		if (packet instanceof ChangeMouseButtonCommand) {
			ChangeMouseButtonCommand ckc = (ChangeMouseButtonCommand) packet;
			synchedMouseButtons[playerId].nextState[ckc.getButton()] = ckc.getNextState();
		}

		if (packet instanceof ChangeMouseCoordinateCommand) {
			ChangeMouseCoordinateCommand ccc = (ChangeMouseCoordinateCommand) packet;
			synchedMouseButtons[playerId].setPosition(new Point(ccc.getX(), ccc.getY()));
			synchedMouseButtons[playerId].mouseHidden = ccc.isMouseHidden();
		}

		if (packet instanceof ChatCommand) {
			ChatCommand cc = (ChatCommand) packet;
			chat.addMessage(cc.getMessage());
		}

		if (packet instanceof CharacterCommand) {
			CharacterCommand charCommand = (CharacterCommand) packet;
			players[charCommand.getPlayerID()].setCharacter(GameCharacter.values()[charCommand.getCharacterID()]);
		}

		if (packet instanceof PauseCommand) {
			PauseCommand pc = (PauseCommand) packet;
			paused = pc.isPause();
			if (paused) {
				addMenu(new PauseMenu(GAME_WIDTH, GAME_HEIGHT));
			} else {
				popMenu();
			}
		}
	}

	@Override
	public void handle(Packet packet) {
		if (packet instanceof StartGamePacket) {
			if (!isServer) {
				sendCharacter = true;
				StartGamePacket sgPacker = (StartGamePacket) packet;
				synchronizer.onStartGamePacket(sgPacker);
				TitleMenu.difficulty = DifficultyList.getDifficulties().get(
						sgPacker.getDifficulty());
				createLevel(sgPacker.getLevelFile(), TitleMenu.defaultGameMode,
						GameCharacter.values()[sgPacker.getOpponentCharacterID()]);
			}
		} else if (packet instanceof TurnPacket) {
			synchronizer.onTurnPacket((TurnPacket) packet);
		} else if (packet instanceof StartGamePacketCustom) {
			if (!isServer) {
				sendCharacter = true;
				StartGamePacketCustom sgPacker = (StartGamePacketCustom) packet;
				synchronizer.onStartGamePacket((StartGamePacket) packet);
				TitleMenu.difficulty = DifficultyList.getDifficulties().get(
						sgPacker.getDifficulty());
				level = sgPacker.getLevel();
				paused = false;
				initLevel(GameCharacter.values()[sgPacker.getOpponentCharacterID()]);
			}
		} else if (packet instanceof PingPacket) {
			PingPacket pp = (PingPacket) packet;
			synchronizer.onPingPacket(pp);
			if (pp.getType() == PingPacket.TYPE_ACK) {
				latencyCache.addToLatencyCache(pp.getLatency());
			}
		}
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
			clearMenus();
			level = null;
			TitleMenu menu = new TitleMenu(GAME_WIDTH, GAME_HEIGHT);
			addMenu(menu);
			this.nextMusicInterval = 0;
			soundPlayer.stopBackgroundMusic();
			soundPlayer.startTitleMusic();
			break;

		case TitleMenu.START_GAME_ID:
			clearMenus();
			isMultiplayer = false;
			chat.clear();

			localId = 0;
			MojamComponent.localTeam = Team.Team1;
			synchronizer = new TurnSynchronizer(this, null, 0, 1);
			synchronizer.setStarted(true);

			createLevel(TitleMenu.level, TitleMenu.defaultGameMode, playerCharacter);
			soundPlayer.stopBackgroundMusic();
			break;

		case TitleMenu.SELECT_LEVEL_ID:
			addMenu(new LevelSelect(false));
			break;

		case TitleMenu.SELECT_HOST_LEVEL_ID:
			addMenu(new LevelSelect(true));
			break;
		/*
		 * case TitleMenu.UPDATE_LEVELS: GuiMenu menu = menuStack.pop(); if
		 * (menu instanceof LevelSelect) { addMenu(new
		 * LevelSelect(((LevelSelect) menu).bHosting)); } else { addMenu(new
		 * LevelSelect(false)); } }
		 */
		case TitleMenu.HOST_GAME_ID:
			addMenu(new HostingWaitMenu());
			isMultiplayer = true;
			isServer = true;
			chat.clear();
			try {
				if (isServer) {
					localId = 0;
					MojamComponent.localTeam = Team.Team1;
					serverSocket = new ServerSocket(Options.getAsInteger(Options.MP_PORT, 3000));
					serverSocket.setSoTimeout(1000);

					hostThread = new Thread() {

						@Override
						public void run() {
							boolean fail = true;
							try {
								while (!isInterrupted()) {
									Socket socket = null;
									try {
										socket = serverSocket.accept();
									} catch (SocketTimeoutException e) {
									}
									if (socket == null) {
										System.out.println("Waiting for player to connect");
										continue;
									}
									fail = false;
									packetLink = new NetworkPacketLink(socket);
									createServerState = 1;
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (fail) {
								try {
									serverSocket.close();
								} catch (IOException e) {
								}
							}
						};
					};
					hostThread.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case TitleMenu.JOIN_GAME_ID:
			addMenu(new JoinGameMenu());
			break;

		case TitleMenu.CANCEL_JOIN_ID:
			popMenu();
			if (hostThread != null) {
				hostThread.interrupt();
				hostThread = null;
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
				packetLink = new ClientSidePacketLink(ip, port);
				synchronizer = new TurnSynchronizer(this, packetLink, localId, 2);
				packetLink.setPacketListener(this);
			} catch (Exception e) {
				e.printStackTrace();
				// System.exit(1);
				addMenu(new TitleMenu(GAME_WIDTH, GAME_HEIGHT));
			}
			break;

		case TitleMenu.HOW_TO_PLAY:
			addMenu(new HowToPlayMenu(level != null));
			break;

		case TitleMenu.OPTIONS_ID:
			addMenu(new OptionsMenu(level != null));
			break;

		case TitleMenu.SELECT_DIFFICULTY_ID:
			addMenu(new DifficultySelect(false));
			break;

		case TitleMenu.SELECT_DIFFICULTY_HOSTING_ID:
			addMenu(new DifficultySelect(true));
			break;

		case TitleMenu.KEY_BINDINGS_ID:
			addMenu(new KeyBindingsMenu(keys, inputHandler));
			break;

		case TitleMenu.LEVEL_EDITOR_ID:
			addMenu(new LevelEditorMenu());
			break;

		case TitleMenu.EXIT_GAME_ID:
			System.exit(0);
			break;

		case TitleMenu.RETURN_ID:
			synchronizer.addCommand(new PauseCommand(false));
			keys.tick();
			break;

		case TitleMenu.BACK_ID:
			popMenu();
			break;

		case TitleMenu.CREDITS_ID:
			addMenu(new CreditsScreen(GAME_WIDTH, GAME_HEIGHT));
			break;

		case TitleMenu.CHARACTER_ID:
			addMenu(new CharacterSelectionMenu());
			break;

		case TitleMenu.AUDIO_VIDEO_ID:
			addMenu(new AudioVideoMenu(level != null));
			break;

		case TitleMenu.LOCALE_ID:
			localemenu = new LocaleMenu(level != null);
			addMenu(localemenu);
			break;
		}
	}

	private void clearMenus() {
		while (!menuStack.isEmpty()) {
			menuStack.pop();
		}
	}

	private void addMenu(GuiMenu menu) {
		menuStack.add(menu);
		menu.addButtonListener(this);
	}

	private void popMenu() {
		if (!menuStack.isEmpty()) {
			menuStack.pop();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!menuStack.isEmpty()) {
			menuStack.peek().keyPressed(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!menuStack.isEmpty()) {
			menuStack.peek().keyReleased(e);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (!menuStack.isEmpty()) {
			menuStack.peek().keyTyped(e);
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
}
