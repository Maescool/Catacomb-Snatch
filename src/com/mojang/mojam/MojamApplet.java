package com.mojang.mojam;

import java.applet.Applet;
import java.awt.BorderLayout;

public class MojamApplet extends Applet {
    private static final long serialVersionUID = 1L;

    private MojamComponent game;

    public void init() {
        game = new MojamComponent();
        setLayout(new BorderLayout());
        add(game, BorderLayout.CENTER);
    }

    public void start() {
        game.start();
    }

    public void stop() {
        game.stop();
    }
}