package com.mojang.mojam.sound;

import paulscode.sound.SoundSystem;

public interface ISoundPlayer {

	public static final String BACKGROUND_TRACK = "background";

	public abstract void startTitleMusic();

	public abstract void startEndMusic();

	public abstract void startBackgroundMusic();

	public abstract void stopBackgroundMusic();

	public abstract void setListenerPosition(float x, float y);

	public abstract boolean playSound(String sourceName, float x, float y);

	public abstract boolean playSound(String sourceName, float x, float y, boolean blocking);

	public abstract void shutdown();

	public abstract boolean isMuted();

	public abstract void setMuted(boolean muted);
	
	public abstract void setSoundSystem(SoundSystem soundSystem);

	public abstract SoundSystem getSoundSystem();

}