package com.mojang.mojam;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.screen.Screen;
import java.util.HashMap;
import java.util.Map;

public class Console implements KeyListener {

    private static class KeyStatus {

        boolean status = true;
    }
    private static final byte max = 4;
    private static final int xLoc = 5;
    private static final int yLoc = 312;
    private Map<Integer, KeyStatus> keyssss = new HashMap<Integer, KeyStatus>();
    private boolean active;
    private boolean closing;
    private ArrayList<String> memory;
    private ArrayList<ConsoleListener> listeners;
    private String now = "";
    private int alpha;
    private long ticks;

    public Console() {
        memory = new ArrayList<String>(max);
        listeners = new ArrayList<ConsoleListener>();
        active = false;
        closing = false;
    }

    /**
     * Add a listener
     * 
     * @param consoleListener Listener to add
     */
    public void addListener(ConsoleListener consoleListener) {
        if (consoleListener != null) {
            listeners.add(consoleListener);
        }
    }

    /**
     * Remove a listener
     * 
     * @param consoleListener Listener to remove
     */
    public void removeListener(ConsoleListener consoleListener) {
        if (consoleListener != null) {
            listeners.remove(consoleListener);
        }
    }

    /**
     * Check if the String gives is blank, is a real command or chat. 
     * A real command will be send to all Listeners.
     * If no suitable Listener is found, will be sent to memory,
     * 
     * @param command Full line the user write
     */
    protected void command(String command) {
        if (command.length() == 0) {
            return;
        }
        if (command.startsWith("/")) {
            for (ConsoleListener cl : listeners) {
                if (cl.command(command)) {
                    closing = true;
                    return;
                }
            }
            addMemory("[COMMAND NOT FOUND]: " + command);
            return;
        }
        closing = true;
        addMemory(command);
    }

    /**
     * Save a String into the memory (FIFO) and remove another String if needed
     * 
     * @param command String to save
     */
    protected void addMemory(String command) {

        if (memory.size() == max - 1) {
            memory.remove(0);
        }
        memory.add(command);
    }

    /**
     * Set Active status to true
     * Closing proccess will be stoped.
     */
    public void active() {
        active = true;
        closing = false;
        ticks = 0;
        alpha = 0;
    }

    /**
     * Console start the closing proccess. It will take 100 ticks to fully close.
     * 
     */
    void close() {
        if (!closing) {
            closing = true;
            ticks = 0;
        }
    }

    /**
     * Ask for Active status.
     * Return true while closing.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Ask for closing status.
     * Console is active, but closing. It takes 100 ticks to auto-desactivate.
     */
    public boolean isClosing() {
        return closing;
    }

    public void tick() {
        if (closing) {
            if (ticks < 100) {
                ticks++;
                if (ticks > 50) {
                    alpha = (int) Math.floor(ticks * 256 / 50.0);
                }
            } else {
                alpha = 0;
                closing = false;
                active = false;
            }
        }
    }

    public void render(Screen screen) {
        if (active) {
            int xOffset = xLoc;
            int yOffset = yLoc;
            if (!closing) {
                Font.defaultFont().drawAlpha(screen, now, xOffset, yOffset, alpha);
            }
            yOffset -= 8 * memory.size() + 8;
            for (int i = 0; i < memory.size(); i++) {
                Font.defaultFont().drawAlpha(screen, memory.get(i), xOffset, (yOffset += 8), alpha);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (closing || !active) {
            return;
        }

        int key = e.getKeyCode();
        KeyStatus k = keyssss.get(key);
        if (k != null) {
            if (k.status) {
                return;
            }
        } else {
            k = new KeyStatus();
            keyssss.put(key, k);
        }
        k.status = true;

        if (key == KeyEvent.VK_BACK_SPACE) {
            if (now.length() > 0) {
                now = now.substring(0, now.length() - 1);
            }
        } else if (key == KeyEvent.VK_ENTER) {
            command(now);
            now = "";
        } else if (e.getKeyLocation() != 1) {
            //avoid control keys
        } else if (!e.isActionKey()) {
            if ((now + e.getKeyChar()).length() < 40) {
                now += e.getKeyChar();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        KeyStatus k = keyssss.get(key);
        if (k != null) {
            k.status = false;
        }
    }
}
