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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.building.Base;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gui.Button;
import com.mojang.mojam.gui.ButtonListener;
import com.mojang.mojam.gui.ClickableComponent;
import com.mojang.mojam.gui.DifficultySelect;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.GuiError;
import com.mojang.mojam.gui.GuiMenu;
import com.mojang.mojam.gui.HostingWaitMenu;
import com.mojang.mojam.gui.HowToPlay;
import com.mojang.mojam.gui.JoinGameMenu;
import com.mojang.mojam.gui.LevelSelect;
import com.mojang.mojam.gui.PauseMenu;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.DifficultyList;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
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
import com.mojang.mojam.network.packet.StartGamePacket;
import com.mojang.mojam.network.packet.StartGamePacketCustom;
import com.mojang.mojam.network.packet.TurnPacket;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.sound.SoundPlayer;

public class MojamComponent extends Canvas implements Runnable,
		MouseMotionListener, CommandListener, PacketListener, MouseListener,
		ButtonListener, KeyListener {

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
	private Screen screen = new Screen(GAME_WIDTH, GAME_HEIGHT);
	private Level level;

	private Stack<GuiMenu> menuStack = new Stack<GuiMenu>();

	private boolean mouseMoved = false;
	private boolean mouseHidden = false;
	private int mouseHideTime = 0;
	public MouseButtons mouseButtons = new MouseButtons();
	public Keys keys = new Keys();
	public Keys[] synchedKeys = { new Keys(), new Keys() };
	public Player[] players = new Player[2];
	public Player player;
	public TurnSynchronizer synchronizer;
	private PacketLink packetLink;
	private ServerSocket serverSocket;
	private boolean isMultiplayer;
	private boolean isServer;
	private int localId;
	private Thread hostThread;
	public static SoundPlayer soundPlayer;
	private long nextMusicInterval = 0;
	//private byte sShotCounter = 0;

	private int createServerState = 0;
	private static File mojamDir = null;

	public MojamComponent() {
		locale = new Locale("en");
		texts = new Texts(locale);

		this.setPreferredSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT
				* SCALE));
		this.setMinimumSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT
				* SCALE));
		this.setMaximumSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT
				* SCALE));

		this.addKeyListener(new InputHandler(keys));
		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		TitleMenu menu = new TitleMenu(GAME_WIDTH, GAME_HEIGHT);
		addMenu(menu);
		addKeyListener(this);

		instance = this;
		LevelList.createLevelList();
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
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public void stop() {
		running = false;
		soundPlayer.shutdown();
	}

	private void init() {
		soundPlayer = new SoundPlayer();
		soundPlayer.startTitleMusic();

		try {
			emptyCursor = Toolkit.getDefaultToolkit().createCustomCursor(
					new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
					new Point(0, 0), "empty");
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		setFocusTraversalKeysEnabled(false);
		requestFocus();

		// hide cursor, since we're drawing our own one
		setCursor(emptyCursor);
	}

	public void showError(String s) {
		handleAction(TitleMenu.RETURN_TO_TITLESCREEN);
		addMenu(new GuiError(s));
	}

	private synchronized void createLevel(String levelPath) {
		LevelInformation li = LevelInformation.getInfoForPath(levelPath);
		if (li != null) {
			createLevel(li);
			return;
		} else if (!isMultiplayer) {
			showError("Missing map.");
		}
		showError("Missing map - Multiplayer");
	}

	private synchronized void createLevel(LevelInformation li) {
		try {
			level = Level.fromFile(li);
		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Unable to load map.");
			return;
		}
		initLevel();
		paused = false;
	}

	private synchronized void initLevel() {
		if (level == null)
			return;
		level.init();

		players[0] = new Player(synchedKeys[0], mouseButtons, level.width
				* Tile.WIDTH / 2 - 16, (level.height - 5 - 1) * Tile.HEIGHT
				- 16, Team.Team1);
		players[0].setFacing(4);
		level.addEntity(players[0]);
		level.addEntity(new Base(34 * Tile.WIDTH, 7 * Tile.WIDTH, Team.Team1));
		if (isMultiplayer) {
			players[1] = new Player(synchedKeys[1], mouseButtons, level.width
					* Tile.WIDTH / 2 - 16, 7 * Tile.HEIGHT - 16, Team.Team2);
			// players[1] = new Player(synchedKeys[1], 10, 10);
			level.addEntity(players[1]);
			level.addEntity(new Base(32 * Tile.WIDTH - 20,
					32 * Tile.WIDTH - 20, Team.Team2));
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
			soundPlayer.setListenerPosition((float) player.pos.x,
					(float) player.pos.y);
			level.render(screen, xScroll, yScroll);
		}
		if (!menuStack.isEmpty()) {
			menuStack.peek().render(screen);
		}

		boolean drawFPS = Options.get("drawFPS") != null
				&& Options.get("drawFPS").equals("true");
		if (drawFPS) {
			Font.draw(screen, texts.FPS(fps), 10, 10);
		}

		if (player != null && menuStack.size() == 0) {
			Font.draw(screen, texts.health(player.health, player.maxHealth),
					340, screen.h - 16);
			Font.draw(screen, texts.money(player.score), 340, screen.h - 27);
			Font.draw(screen, texts.nextLevel((int) player.getNextLevel()),
					340, screen.h - 38);
			Font.draw(screen, texts.playerExp((int) player.pexp), 340,
					screen.h - 49);
			Font.draw(screen, texts.playerLevel(player.plevel), 340,
					screen.h - 60);
		}

		g.setColor(Color.BLACK);

		g.fillRect(0, 0, getWidth(), getHeight());
		g.translate((getWidth() - GAME_WIDTH * SCALE) / 2,
				(getHeight() - GAME_HEIGHT * SCALE) / 2);
		g.clipRect(0, 0, GAME_WIDTH * SCALE, GAME_HEIGHT * SCALE);

		if (!menuStack.isEmpty() || level != null) {

			// render mouse
			renderMouse(screen, mouseButtons);

			g.drawImage(screen.image, 0, 0, GAME_WIDTH * SCALE, GAME_HEIGHT
					* SCALE, null);
		}

	}

	private void renderMouse(Screen screen, MouseButtons mouseButtons) {

		if (mouseHidden)
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

		screen.blit(marker,
				mouseButtons.getX() / SCALE - crosshairSizeHalf - 2,
				mouseButtons.getY() / SCALE - crosshairSizeHalf - 2);
	}

	private void tick() {

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
			if (mouseHidden) {
				mouseHidden = false;
			}
		}
		if (mouseHideTime < 60) {
			mouseHideTime++;
			if (mouseHideTime == 60) {
				mouseHidden = true;
			}
		}
		mouseButtons.tick();

		if (level != null) {
			if (synchronizer.preTurn()) {
				synchronizer.postTurn();

				if (!paused) {
					for (int index = 0; index < keys.getAll().size(); index++) {
						Keys.Key key = keys.getAll().get(index);
						boolean nextState = key.nextState;
						if (key.isDown != nextState) {
							synchronizer.addCommand(new ChangeKeyCommand(index,
									nextState));
						}
					}

					keys.tick();
					for (Keys skeys : synchedKeys) {
						skeys.tick();
					}

					if (keys.pause.wasPressed()) {
						keys.release();
						synchronizer.addCommand(new PauseCommand(true));
					}

					// if mouse is in use, update player orientation before
					// level tick
					if (!mouseHidden) {

						// update player mouse, in world pixels relative to
						// player
						player.setAimByMouse(
								((mouseButtons.getX() / SCALE) - (screen.w / 2)),
								(((mouseButtons.getY() / SCALE) + 24) - (screen.h / 2)));
					} else {
						player.setAimByKeyboard();
					}

					level.tick();
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

			synchronizer = new TurnSynchronizer(MojamComponent.this,
					packetLink, localId, 2);

			clearMenus();
			createLevel(TitleMenu.level);

			synchronizer.setStarted(true);
			if (TitleMenu.level.vanilla) {
				packetLink.sendPacket(new StartGamePacket(
						TurnSynchronizer.synchedSeed, TitleMenu.level.getUniversalPath(),DifficultyList.getDifficultyID(TitleMenu.difficulty)));
			} else {
				packetLink.sendPacket(new StartGamePacketCustom(
						TurnSynchronizer.synchedSeed, level, DifficultyList.getDifficultyID(TitleMenu.difficulty)));
			}
			packetLink.setPacketListener(MojamComponent.this);

		}
	}

	public static void main(String[] args) {
		MojamComponent mc = new MojamComponent();
		guiFrame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mc);
		guiFrame.setContentPane(panel);
		guiFrame.pack();
		guiFrame.setResizable(false);
		guiFrame.setLocationRelativeTo(null);
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setVisible(true);
		Options.loadProperties();
		setFullscreen(Boolean.parseBoolean(Options.get("fullscreen")));
		mc.start();
	}

	public static void setFullscreen(boolean fs) {
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

	}

	@Override
	public void handle(int playerId, NetworkCommand packet) {

		if (packet instanceof ChangeKeyCommand) {
			ChangeKeyCommand ckc = (ChangeKeyCommand) packet;
			synchedKeys[playerId].getAll().get(ckc.getKey()).nextState = ckc
					.getNextState();
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
				StartGamePacket sgPacker = (StartGamePacket) packet;
				synchronizer.onStartGamePacket(sgPacker.getGameSeed());
				TitleMenu.difficulty = DifficultyList.getDifficulties().get(sgPacker.getDifficulty());
				createLevel(sgPacker.getLevelFile());
			}
		} else if (packet instanceof TurnPacket) {
			synchronizer.onTurnPacket((TurnPacket) packet);
		} else if (packet instanceof StartGamePacketCustom) {
			if (!isServer) {
				StartGamePacketCustom sgPacker = (StartGamePacketCustom) packet;
				synchronizer.onStartGamePacket(sgPacker.getGameSeed());
				TitleMenu.difficulty = DifficultyList.getDifficulties().get(sgPacker.getDifficulty());
				level = sgPacker.getLevel();
				paused = false;
				initLevel();
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

	public void handleAction(int id) {
		if (id == TitleMenu.RETURN_TO_TITLESCREEN) {
			clearMenus();
			TitleMenu menu = new TitleMenu(GAME_WIDTH, GAME_HEIGHT);
			addMenu(menu);

		} else if (id == TitleMenu.START_GAME_ID) {
			clearMenus();
			isMultiplayer = false;

			localId = 0;
			synchronizer = new TurnSynchronizer(this, null, 0, 1);
			synchronizer.setStarted(true);

			createLevel(TitleMenu.level);
			soundPlayer.stopBackgroundMusic();
		} else if (id == TitleMenu.SELECT_LEVEL_ID) {
			addMenu(new LevelSelect(false));
		} else if (id == TitleMenu.SELECT_HOST_LEVEL_ID) {
			addMenu(new LevelSelect(true));
		} else if (id == TitleMenu.UPDATE_LEVELS) {
			GuiMenu menu = menuStack.pop();
			if (menu instanceof LevelSelect) {
				addMenu(new LevelSelect(((LevelSelect) menu).bHosting));
			} else {
				addMenu(new LevelSelect(false));
			}
		} else if (id == TitleMenu.HOST_GAME_ID) {
			addMenu(new HostingWaitMenu());
			isMultiplayer = true;
			isServer = true;
			try {
				if (isServer) {
					localId = 0;
					serverSocket = new ServerSocket(3000);
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
										System.out
												.println("Waiting for player to connect");
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
		} else if (id == TitleMenu.JOIN_GAME_ID) {
			addMenu(new JoinGameMenu());
		} else if (id == TitleMenu.CANCEL_JOIN_ID) {
			popMenu();
			if (hostThread != null) {
				hostThread.interrupt();
				hostThread = null;
			}
		} else if (id == TitleMenu.PERFORM_JOIN_ID) {
			menuStack.clear();
			isMultiplayer = true;
			isServer = false;

			try {
				localId = 1;
				packetLink = new ClientSidePacketLink(TitleMenu.ip, 3000);
				synchronizer = new TurnSynchronizer(this, packetLink, localId,2);
				packetLink.setPacketListener(this);
			} catch (Exception e) {
				e.printStackTrace();
				// System.exit(1);
				addMenu(new TitleMenu(GAME_WIDTH, GAME_HEIGHT));
			}
		} else if (id == TitleMenu.HOW_TO_PLAY) {
			addMenu(new HowToPlay());
		} else if (id == TitleMenu.SELECT_DIFFICULTY_ID) {
			addMenu(new DifficultySelect(false));
		} else if (id == TitleMenu.SELECT_DIFFICULTY_HOSTING_ID) {
			addMenu(new DifficultySelect(true));
		} else if (id == TitleMenu.EXIT_GAME_ID) {
			System.exit(0);
		} else if (id == TitleMenu.RETURN_ID) {
			synchronizer.addCommand(new PauseCommand(false));
			keys.tick();
		} else if (id == TitleMenu.BACK_ID) {
			popMenu();
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
			file = new File(s1, (new StringBuilder()).append('.').append(s)
					.append('/').toString());
			break;

		case 3: // '\003'
			String s2 = System.getenv("APPDATA");
			if (s2 != null) {
				file = new File(s2, (new StringBuilder()).append(".").append(s)
						.append('/').toString());
			} else {
				file = new File(s1, (new StringBuilder()).append('.').append(s)
						.append('/').toString());
			}
			break;

		case 4: // '\004'
			file = new File(s1, (new StringBuilder())
					.append("Library/Application Support/").append(s)
					.toString());
			break;

		default:
			file = new File(s1, (new StringBuilder()).append(s).append('/')
					.toString());
			break;
		}
		if (!file.exists() && !file.mkdirs()) {
			throw new RuntimeException((new StringBuilder())
					.append("The working directory could not be created: ")
					.append(file).toString());
		} else {
			return file;
		}
	}

	public void takeScreenShot() {
		BufferedImage screencapture;

		try {
			screencapture = new Robot().createScreenCapture(guiFrame
					.getBounds());

			//sShotCounter++;

			File file = new File("screenShot" + new Timestamp(new java.util.Date().getTime()) + ".jpg");
			ImageIO.write(screencapture, "jpg", file);
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}