package com.mojang.mojam;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.swing.*;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.building.Base;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gui.*;
import com.mojang.mojam.gui.Button;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.network.*;
import com.mojang.mojam.network.packet.*;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.sound.SoundPlayer;

public class MojamComponent extends Canvas implements Runnable,
		MouseMotionListener, CommandListener, PacketListener, MouseListener,
		ButtonListener, KeyListener {

	public static Locale locale;
	public static Texts texts;
	private static final long serialVersionUID = 1L;
	public static final int GAME_WIDTH = 512;
	public static final int GAME_HEIGHT = GAME_WIDTH * 3 / 4;
	public static final int SCALE = 2;
	private boolean running = true;
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

	private int createServerState = 0;

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
	}

	public void mouseDragged(MouseEvent arg0) {
		mouseMoved = true;
	}

	public void mouseMoved(MouseEvent arg0) {
		mouseMoved = true;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
		mouseButtons.releaseAll();
	}

	public void mousePressed(MouseEvent e) {
		mouseButtons.setNextState(e.getButton(), true);
	}

	public void mouseReleased(MouseEvent e) {
		mouseButtons.setNextState(e.getButton(), false);
	}

	public void paint(Graphics g) {
	}

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
		soundPlayer.startBackgroundMusic();

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

	private synchronized void createLevel(String levelFile) {
		try {
			level = Level.fromFile(levelFile);
		} catch (Exception ex) {
			throw new RuntimeException("Unable to load level", ex);
		}

		level.init();

		players[0] = new Player(synchedKeys[0], level.width * Tile.WIDTH / 2
				- 16, (level.height - 5 - 1) * Tile.HEIGHT - 16, Team.Team1);
		players[0].setFacing(4);
		level.addEntity(players[0]);
		level.addEntity(new Base(34 * Tile.WIDTH, 7 * Tile.WIDTH, Team.Team1));
		if (isMultiplayer) {
			players[1] = new Player(synchedKeys[1], level.width * Tile.WIDTH
					/ 2 - 16, 7 * Tile.HEIGHT - 16, Team.Team2);
			// players[1] = new Player(synchedKeys[1], 10, 10);
			level.addEntity(players[1]);
			level.addEntity(new Base(32 * Tile.WIDTH - 20,
					32 * Tile.WIDTH - 20, Team.Team2));
		}
		player = players[localId];
		player.setCanSee(true);
	}

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

		Font.draw(screen, texts.FPS(fps), 10, 10);
		// for (int p = 0; p < players.length; p++) {
		// if (players[p] != null) {
		// String msg = "P" + (p + 1) + ": " + players[p].getScore();
		// Font.draw(screen, msg, 320, screen.h - 24 + p * 8);
		// }
		// }
		if (player != null && menuStack.size() == 0) {
			Font.draw(screen, texts.health(player.health, 10), 340, screen.h - 19);
			Font.draw(screen, texts.money(player.score), 340, screen.h - 33);
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

		// String msg = "FPS: " + fps;
		// g.setColor(Color.LIGHT_GRAY);
		// g.drawString(msg, 11, 11);
		// g.setColor(Color.WHITE);
		// g.drawString(msg, 10, 10);

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
		if (level != null) {
			if (level.player1Score >= Level.TARGET_SCORE) {
				addMenu(new WinMenu(GAME_WIDTH, GAME_HEIGHT, 1));
				level = null;
				return;
			}
			if (level.player2Score >= Level.TARGET_SCORE) {
				addMenu(new WinMenu(GAME_WIDTH, GAME_HEIGHT, 2));
				level = null;
				return;
			}
			if (keys.escape.wasPressed()) {
				clearMenus();
				addMenu(new TitleMenu(GAME_WIDTH, GAME_HEIGHT));
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

				// if mouse is in use, update player orientation before level
				// tick
				if (!mouseHidden) {

					// update player mouse, in world pixels relative to player
					player.setAimByMouse(
							((mouseButtons.getX() / SCALE) - (screen.w / 2)),
							(((mouseButtons.getY() / SCALE) + 24) - (screen.h / 2)));
				} else {
					player.setAimByKeyboard();
				}

				level.tick();
			}
		}

		if (createServerState == 1) {
			createServerState = 2;

			synchronizer = new TurnSynchronizer(MojamComponent.this,
					packetLink, localId, 2);

			clearMenus();
			createLevel(TitleMenu.level);

			synchronizer.setStarted(true);
			packetLink.sendPacket(new StartGamePacket(
					TurnSynchronizer.synchedSeed, TitleMenu.level));
			packetLink.setPacketListener(MojamComponent.this);

		}
	}

	public static void main(String[] args) {
		MojamComponent mc = new MojamComponent();
		JFrame frame = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mc);
		frame.setContentPane(panel);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		mc.start();
	}

	public void handle(int playerId, NetworkCommand packet) {

		if (packet instanceof ChangeKeyCommand) {
			ChangeKeyCommand ckc = (ChangeKeyCommand) packet;
			synchedKeys[playerId].getAll().get(ckc.getKey()).nextState = ckc
					.getNextState();
		}
	}

	public void handle(Packet packet) {
		if (packet instanceof StartGamePacket) {
			if (!isServer) {
				StartGamePacket sgPacker = (StartGamePacket) packet;
				synchronizer.onStartGamePacket(sgPacker);
				createLevel(sgPacker.getLevelFile());
			}
		} else if (packet instanceof TurnPacket) {
			synchronizer.onTurnPacket((TurnPacket) packet);
		}
	}

	public void buttonPressed(Button button) {
		if (button.getId() == TitleMenu.RESTART_GAME_ID) {
			clearMenus();
			TitleMenu menu = new TitleMenu(GAME_WIDTH, GAME_HEIGHT);
			addMenu(menu);

		} else if (button.getId() == TitleMenu.START_GAME_ID) {
			clearMenus();
			isMultiplayer = false;

			localId = 0;
			synchronizer = new TurnSynchronizer(this, null, 0, 1);
			synchronizer.setStarted(true);

			createLevel(TitleMenu.level);
		} else if (button.getId() == TitleMenu.SELECT_LEVEL_ID) {
			addMenu(new LevelSelect(false));
		} else if (button.getId() == TitleMenu.SELECT_HOST_LEVEL_ID) {
			addMenu(new LevelSelect(true));
		} else if (button.getId() == TitleMenu.HOST_GAME_ID) {
			addMenu(new HostingWaitMenu());
			isMultiplayer = true;
			isServer = true;
			try {
				if (isServer) {
					localId = 0;
					serverSocket = new ServerSocket(3000);
					serverSocket.setSoTimeout(1000);

					hostThread = new Thread() {

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
										System.out.println("asdf");
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
		} else if (button.getId() == TitleMenu.JOIN_GAME_ID) {
			addMenu(new JoinGameMenu());
		} else if (button.getId() == TitleMenu.CANCEL_JOIN_ID) {
			popMenu();
			if (hostThread != null) {
				hostThread.interrupt();
				hostThread = null;
			}
		} else if (button.getId() == TitleMenu.PERFORM_JOIN_ID) {
			menuStack.clear();
			isMultiplayer = true;
			isServer = false;

			try {
				localId = 1;
				packetLink = new ClientSidePacketLink(TitleMenu.ip, 3000);
				synchronizer = new TurnSynchronizer(this, packetLink, localId,
						2);
				packetLink.setPacketListener(this);
			} catch (Exception e) {
				e.printStackTrace();
				// System.exit(1);
				addMenu(new TitleMenu(GAME_WIDTH, GAME_HEIGHT));
			}
		} else if (button.getId() == TitleMenu.EXIT_GAME_ID) {
			System.exit(0);
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

	public void keyPressed(KeyEvent e) {
		if (!menuStack.isEmpty()) {
			menuStack.peek().keyPressed(e);
		}
	}

	public void keyReleased(KeyEvent e) {
		if (!menuStack.isEmpty()) {
			menuStack.peek().keyReleased(e);
		}
	}

	public void keyTyped(KeyEvent e) {
		if (!menuStack.isEmpty()) {
			menuStack.peek().keyTyped(e);
		}
	}

}