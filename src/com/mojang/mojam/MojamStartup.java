package com.mojang.mojam;

import javax.swing.*;

import com.mojang.mojam.gui.DownloadScreen;
import com.mojang.mojam.gui.GuiMenu;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ButtonListener;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.resources.Constants;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;
import com.mojang.mojam.sound.NoSoundPlayer;
import com.mojang.mojam.sound.SoundPlayer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

public class MojamStartup extends Canvas implements Runnable, KeyListener,
	ButtonListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static JFrame guiFrame;

    public static MojamStartup instance;

    public static final int GAME_WIDTH = 512;
    public static final int GAME_HEIGHT = GAME_WIDTH * 3 / 4;
    public static final int SCALE = 1;

    public static Screen screen = new Screen(GAME_WIDTH, GAME_HEIGHT);

    private Stack<GuiMenu> menuStack = new Stack<GuiMenu>();

    private boolean running = true;
    private double framerate = 60;
    private int fps;

    public MouseButtons mouseButtons = new MouseButtons();
    public Keys keys = new Keys();

    private Downloader dl;
    private DownloadScreen ds;

    public MojamStartup() {

	MojamComponent.constants = new Constants();
	MojamComponent.texts = new Texts(new Locale("en"));

	this.setPreferredSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT
		* SCALE));
	this.setMinimumSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT
		* SCALE));
	this.setMaximumSize(new Dimension(GAME_WIDTH * SCALE, GAME_HEIGHT
		* SCALE));

	ds = new DownloadScreen();
	addMenu(ds);

	instance = this;

	// dl = new Downloader();
	// dl.CheckFiles();
	// MojamComponent.startgame();
	// guiFrame.disable();
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
		render(g);

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

    private void tick() {
	if (!menuStack.isEmpty()) {
	    menuStack.peek().tick(mouseButtons);
	}
    }

    private synchronized void render(Graphics g) {
	if (!menuStack.isEmpty()) {
	    menuStack.peek().render(screen);
	}
	g.setColor(Color.BLACK);

	g.fillRect(0, 0, getWidth(), getHeight());
	g.translate((getWidth() - GAME_WIDTH * SCALE) / 2,
		(getHeight() - GAME_HEIGHT * SCALE) / 2);
	g.clipRect(0, 0, GAME_WIDTH * SCALE, GAME_HEIGHT * SCALE);

	if (!menuStack.isEmpty()) {
	    g.drawImage(screen.image, 0, 0, GAME_WIDTH * SCALE, GAME_HEIGHT
		    * SCALE, null);
	}
    }

    public void startgame() {
	running = false;
	// not sure if this also will stop thread..
	guiFrame.removeAll();
	guiFrame.setVisible(false);
	MojamComponent.startgame();
    }

    public void start() {
	running = true;
	Thread thread = new Thread(this);
	thread.start();
    }

    public void stop() {
	running = false;
	System.exit(0);
    }

    private void init() {
	setFocusTraversalKeysEnabled(false);
	requestFocus();
    }

    public static void main(String[] args) {
	//
	MojamStartup sl = new MojamStartup();
	guiFrame = new JFrame(MojamComponent.GAME_TITLE);
	JPanel panel = new JPanel(new BorderLayout());
	panel.add(sl);
	guiFrame.setContentPane(panel);
	guiFrame.pack();
	guiFrame.setResizable(false);
	guiFrame.setLocationRelativeTo(null);
	guiFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	guiFrame.addWindowListener(newWindowClosinglistener());
	ArrayList<BufferedImage> icoList = new ArrayList<BufferedImage>();
	icoList.add(Art.icon32);
	icoList.add(Art.icon64);
	guiFrame.setIconImages(icoList);
	guiFrame.setVisible(true);
	sl.start();
	Downloader dl = new Downloader();
	dl.CheckFiles();
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

    @Override
    public void buttonPressed(ClickableComponent component) {
	if (component instanceof Button) {
	    final Button button = (Button) component;
	    // handleAction(button.getId());
	}
    }

    @Override
    public void buttonHovered(ClickableComponent clickableComponent) {
    }

    private static WindowListener newWindowClosinglistener() {
	return new WindowAdapter() {
	    public void windowClosing(WindowEvent winEvt) {
		MojamStartup.instance.stop();
	    }
	};
    }
}