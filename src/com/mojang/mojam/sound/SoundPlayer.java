package com.mojang.mojam.sound;

import com.mojang.mojam.Options;
import com.mojang.mojam.network.TurnSynchronizer;
import java.util.*;

import paulscode.sound.*;
import paulscode.sound.codecs.*;
import paulscode.sound.libraries.LibraryJavaSound;

public class SoundPlayer {

	private final Class<? extends Library> libraryType;
	public SoundSystem soundSystem;
	private boolean oggPlaybackSupport = true;
	private boolean wavPlaybackSupport = true;
	private boolean muted = false;

    private float volume = Options.getAsFloat(Options.VOLUME, "1.0f");
	private float musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
	private float soundVolume = Options.getAsFloat(Options.SOUND, "1.0f");

	public static final String BACKGROUND_TRACK = "background";
	private static final int MAX_SOURCES_PER_SOUND = 5;
	private int nextSong = 0;

	public SoundPlayer() {
		libraryType = LibraryJavaSound.class;

		try {
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		} catch (SoundSystemException ex) {
			oggPlaybackSupport = false;
		}

		try {
			SoundSystemConfig.setCodec("wav", CodecWav.class);
		} catch (SoundSystemException ex) {
			wavPlaybackSupport = false;
		}

		try {
			soundSystem = new SoundSystem(libraryType);
		} catch (SoundSystemException ex) {
			soundSystem = null;
		}
		
		soundSystem.setMasterVolume(volume);
        soundSystem.setVolume(BACKGROUND_TRACK, musicVolume);
	}

	private boolean hasOggPlaybackSupport() {
		return oggPlaybackSupport && soundSystem != null;
	}

	private boolean hasWavPlaybackSupport() {
		return wavPlaybackSupport && soundSystem != null;
	}

	private boolean isPlaying(String sourceName) {
		if (hasOggPlaybackSupport()) {
			return soundSystem.playing(sourceName);
		}
		return false;
	}

	public void startTitleMusic() {
		if (!isMuted() && hasOggPlaybackSupport()) {
			if (isPlaying(BACKGROUND_TRACK))
				stopBackgroundMusic();

			String backgroundTrack = "/sound/ThemeTitle.ogg";
			soundSystem.backgroundMusic(BACKGROUND_TRACK, SoundPlayer.class.getResource(backgroundTrack), backgroundTrack, false);
		}

        soundSystem.setVolume(BACKGROUND_TRACK, musicVolume);
	}

	public void startEndMusic() {
		if (!isMuted() && hasOggPlaybackSupport()) {
		    if (isPlaying(BACKGROUND_TRACK))
                stopBackgroundMusic();

			String backgroundTrack = "/sound/ThemeEnd.ogg";
            soundSystem.backgroundMusic(BACKGROUND_TRACK, SoundPlayer.class.getResource(backgroundTrack), backgroundTrack, false);
		}

        soundSystem.setVolume(BACKGROUND_TRACK, musicVolume);
	}

	public void startBackgroundMusic() {
	    System.out.println("*** startBackgroundMusic ***");
        if (!isMuted() && hasOggPlaybackSupport()) {
            if (isPlaying(BACKGROUND_TRACK))
                stopBackgroundMusic();

            nextSong++;
            if (nextSong>4) nextSong = 1;
            //nextSong = TurnSynchronizer.synchedRandom.nextInt(4)+1;
            String backgroundTrack = "/sound/Background " + nextSong + ".ogg";
            System.out.println("next song: " + backgroundTrack);
            
            soundSystem.backgroundMusic(BACKGROUND_TRACK, SoundPlayer.class.getResource(backgroundTrack), backgroundTrack, false);
		}

        soundSystem.setVolume(BACKGROUND_TRACK, musicVolume);
	}

	public void stopBackgroundMusic() {
		if (hasOggPlaybackSupport()) {
			soundSystem.stop(BACKGROUND_TRACK);
		}
	}

	private Set<String> loaded = new TreeSet<String>();

	public void setListenerPosition(float x, float y) {
		soundSystem.setListenerPosition(x, y, 50);
	}

	public boolean playSound(String sourceName, float x, float y) {
		return playSound(sourceName, x, y, false);
	}

	public boolean playSound(String sourceName, float x, float y, boolean blocking) {
		return playSound(sourceName, x, y, blocking, 0);
	}

	private boolean playSound(String sourceName, float x, float y, boolean blocking, int index) {
		if (index < MAX_SOURCES_PER_SOUND && !isMuted() && hasWavPlaybackSupport()) {
			String indexedSourceName = sourceName + index;
			if (!loaded.contains(indexedSourceName)) {
				soundSystem.newSource(false, indexedSourceName, SoundPlayer.class.getResource(sourceName), sourceName, false, x, y, 0, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
				loaded.add(indexedSourceName);
			} else if (isPlaying(indexedSourceName)) {
				if (blocking) {
					return false;
				}

				// Source already playing, create new source for same sound
				// effect.
				return playSound(sourceName, x, y, false, index + 1);
			}
			soundSystem.stop(indexedSourceName);
			soundSystem.setPriority(indexedSourceName, false);
			soundSystem.setPosition(indexedSourceName, x, y, 0);
			soundSystem.setAttenuation(indexedSourceName, SoundSystemConfig.ATTENUATION_ROLLOFF);
			soundSystem.setDistOrRoll(indexedSourceName, SoundSystemConfig.getDefaultRolloff());
			soundSystem.setPitch(indexedSourceName, 1.0f);
			soundVolume = Options.getAsFloat(Options.SOUND, "1.0f");
			soundSystem.setVolume(indexedSourceName, soundVolume);
			soundSystem.play(indexedSourceName);
			return true;
		}
		return false;
	}

	public void shutdown() {
		if (soundSystem != null) {
			soundSystem.cleanup();
		}
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}
}