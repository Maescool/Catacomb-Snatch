package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.MojamStartup;
import com.mojang.mojam.downloader.Downloader;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class DownloadScreen extends GuiMenu {

    private Downloader dl = null;

    private static boolean downloading = false;
    private static boolean unpacking = false;
    private static String fileName = "";
    private static int downloaded = 0;
    private static int total = 100;

    public DownloadScreen() {
	super();
	dl = new Downloader();
	// dl.CheckFiles();
	// Startup.instance.startgame();
    }

    public static void downLoadStarted(String fName) {
	downloading = true;
	fileName = fName;
    }

    public static void downloadEnd() {
	downloading = false;
	fileName = "";
    }

    public static void unpackStart(String fName) {
	unpacking = true;
	fileName = fName;
    }

    public static void unpackStop() {
	unpacking = false;
	fileName = "";
    }

    public static void drawGraph(int d, int t) {
	downloaded = d;
	total = t;
    }

    @Override
    public void render(Screen screen) {
	screen.clear(0);
	screen.blit(Art.downloadScreen, 0, 0);
	super.render(screen);

	if (downloading) {
	    screen.alphaFill(125, 150, 300, 100, 0xff000000, 0x90);

	    Font.defaultFont().draw(screen,
		    MojamComponent.texts.getStatic("download.downloading"),
		    215, 170);
	    Font.defaultFont().draw(screen, fileName, 210, 180);

	    int maxIndex = Art.panel_xpBar[0].length - 1;
	    int index = maxIndex - Math.round(downloaded * maxIndex / total);
	    if (index < 0)
		index = 0;
	    else if (index > maxIndex)
		index = maxIndex;

	    screen.blit(Art.panel_xpBar[0][index], (screen.w / 2) - 35,
		    screen.h / 2);

	    int dlp = Math.round(downloaded * 100 / total);
	    Font.defaultFont().draw(screen, dlp + "%", (screen.w / 2),
		    (screen.h / 2) + 15);
	}
	if (unpacking) {
	    screen.alphaFill(125, 150, 300, 50, 0xff000000, 0x90);

	    Font.defaultFont().draw(screen,
		    MojamComponent.texts.getStatic("download.unpacking"), 215,
		    170);
	    Font.defaultFont().draw(screen, fileName, 210, 180);
	}
    }

    @Override
    public void buttonPressed(ClickableComponent button) {
	// nothing

    }

    @Override
    public void keyTyped(KeyEvent e) {
	// nothing

    }

    @Override
    public void keyReleased(KeyEvent e) {
	// nothing

    }

}
