package com.mojang.mojam;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import com.mojang.mojam.gui.components.Font;
import com.mojang.mojam.screen.AbstractScreen;

public class Chat implements KeyListener {

	private static final int MAX_MESSAGES = 10;
	private static final int MAX_MESSAGE_LENGTH = 35;
	private static final int TICKS_PER_MESSAGE = 60 * 4;
	private static final int FADE_TICKS = 60 / 2;

	private ArrayList<String> messages = new ArrayList<String>();
	private int displayedMessage = -1;
	private int displayTicks = 0;
	private int fadingTicks = 0;
	private boolean fading;
	private boolean open = false;
	private String currentMessage = "";
	private String waitingMessage = null;

	public void clear() {
		messages.clear();
		displayedMessage = -1;
	}

	public boolean isOpen() {
		return open;
	}

	public void open() {
		open = true;
	}

	public void addMessage(String message) {
		if (messages.size() == MAX_MESSAGES) {
			messages.remove(MAX_MESSAGES - 1);
		}
		messages.add(0, message);
		if (displayedMessage + 1 < MAX_MESSAGES) {
			displayedMessage += 1;
		}
	}

	public String getWaitingMessage() {
		String msg = waitingMessage;
		waitingMessage = null;
		return msg;
	}

	public void tick() {
		if (displayedMessage > -1) {
			if (!fading) {
				displayTicks++;
				if (displayTicks == TICKS_PER_MESSAGE) {
					displayTicks = 0;
					fading = true;
				}
			} else {
				fadingTicks++;
				if (fadingTicks == FADE_TICKS) {
					fadingTicks = 0;
					fading = false;
					displayedMessage--;
				}
			}
		}
	}

	public void render(AbstractScreen screen) {
		int xOffset = 5;
		int yOffset = 312;
		if (open) {
			Font.defaultFont().draw(screen, currentMessage + "-", xOffset, yOffset);
			for (int i = 0; i < messages.size(); i++) {
				Font.defaultFont().draw(screen, messages.get(i), xOffset, (yOffset -= 8));
			}
		} else {
			for (int i = 0; i <= displayedMessage; i++) {
				if (i == displayedMessage) {
					if (fading) {
						int alpha = 255 - (fadingTicks * 255 / FADE_TICKS);
						Font.defaultFont().drawOpaque(screen, messages.get(i), xOffset,
								(yOffset -= 8), alpha);
						continue;
					}
				}
				Font.defaultFont().draw(screen, messages.get(i), xOffset, (yOffset -= 8));
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (open) {
			if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && currentMessage.length() > 0) {
				currentMessage = currentMessage.substring(0, currentMessage.length() - 1);
			} else {
				if (currentMessage.length() < MAX_MESSAGE_LENGTH) {
					currentMessage += e.getKeyChar();
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		if (open) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				open = false;
				currentMessage = "";
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				open = false;
				if (!currentMessage.equals("")) {
					waitingMessage = currentMessage;
				}
				currentMessage = "";
			}
		}
	}

}
