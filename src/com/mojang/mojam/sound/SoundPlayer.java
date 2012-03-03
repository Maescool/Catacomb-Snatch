package com.mojang.mojam.sound;

import java.util.Set;
import java.util.TreeSet;

import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;

import com.mojang.mojam.Options;

public class SoundPlayer implements ISoundPlayer {

	private final Class<? extends Library> libraryType;
	private SoundSystem soundSystem;
	private boolean oggPlaybackSupport = true;
	private boolean wavPlaybackSupport = true;
	private boolean muted = false;

    private float volume = Options.getAsFloat(Options.VOLUME, "1.0f");
	private float musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
	private float soundVolume = Options.getAsFloat(Options.SOUND, "1.0f");

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
			setSoundSystem(new SoundSystem(libraryType));
		} catch (SoundSystemException ex) {
			setSoundSystem(null);
		}
		
		if (getSoundSystem() != null) {
			getSoundSystem().setMasterVolume(volume);
			getSoundSystem().setVolume(BACKGROUND_TRACK, musicVolume);
		}
	}

	private boolean hasOggPlaybackSupport() {
		return oggPlaybackSupport && getSoundSystem() != null;
	}

	private boolean hasWavPlaybackSupport() {
		return wavPlaybackSupport && getSoundSystem() != null;
	}

	private boolean isPlaying(String sourceName) {
		if (hasOggPlaybackSupport()) {
			return getSoundSystem().playing(sourceName);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#startTitleMusic()
	 */
	@Override
	public void startTitleMusic() {
		musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
		if (!isMuted() && hasOggPlaybackSupport()) {
			if (isPlaying(BACKGROUND_TRACK))
				stopBackgroundMusic();

			String backgroundTrack = "/sound/ThemeTitle.ogg";
			getSoundSystem().backgroundMusic(BACKGROUND_TRACK, SoundPlayer.class.getResource(backgroundTrack), backgroundTrack, false);
		}

        getSoundSystem().setVolume(BACKGROUND_TRACK, musicVolume);
	}

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#startEndMusic()
	 */
	@Override
	public void startEndMusic() {
		musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
		if (!isMuted() && hasOggPlaybackSupport()) {
		    if (isPlaying(BACKGROUND_TRACK))
                stopBackgroundMusic();

			String backgroundTrack = "/sound/ThemeEnd.ogg";
            getSoundSystem().backgroundMusic(BACKGROUND_TRACK, SoundPlayer.class.getResource(backgroundTrack), backgroundTrack, false);
		}

        getSoundSystem().setVolume(BACKGROUND_TRACK, musicVolume);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mojang.mojam.sound.ISoundPlayer#startBackgroundMusic()
	 */
	@Override
	public void startBackgroundMusic() {
		System.out.println("*** startBackgroundMusic ***");
		musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
		if (!isMuted() && hasOggPlaybackSupport()) {
			if (isPlaying(BACKGROUND_TRACK))
				stopBackgroundMusic();

            nextSong++;
            if (nextSong>4) nextSong = 1;
            //nextSong = TurnSynchronizer.synchedRandom.nextInt(4)+1;
            String backgroundTrack = "/sound/Background " + nextSong + ".ogg";
            System.out.println("next song: " + backgroundTrack);
            
            getSoundSystem().backgroundMusic(BACKGROUND_TRACK, SoundPlayer.class.getResource(backgroundTrack), backgroundTrack, false);
		}

        getSoundSystem().setVolume(BACKGROUND_TRACK, musicVolume);
	}

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#stopBackgroundMusic()
	 */
	@Override
	public void stopBackgroundMusic() {
		if (hasOggPlaybackSupport()) {
			getSoundSystem().stop(BACKGROUND_TRACK);
		}
	}

	private Set<String> loaded = new TreeSet<String>();

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#setListenerPosition(float, float)
	 */
	@Override
	public void setListenerPosition(float x, float y) {
		getSoundSystem().setListenerPosition(x, y, 50);
	}

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#playSound(java.lang.String, float, float)
	 */
	@Override
	public boolean playSound(String sourceName, float x, float y) {
		return playSound(sourceName, x, y, false);
	}

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#playSound(java.lang.String, float, float, boolean)
	 */
	@Override
	public boolean playSound(String sourceName, float x, float y, boolean blocking) {
		return playSound(sourceName, x, y, blocking, 0);
	}

	private boolean playSound(String sourceName, float x, float y, boolean blocking, int index) {
		if (index < MAX_SOURCES_PER_SOUND && !isMuted() && hasWavPlaybackSupport()) {
			String indexedSourceName = sourceName + index;
			if (!loaded.contains(indexedSourceName)) {
				getSoundSystem().newSource(false, indexedSourceName, SoundPlayer.class.getResource(sourceName), sourceName, false, x, y, 0, SoundSystemConfig.ATTENUATION_ROLLOFF, SoundSystemConfig.getDefaultRolloff());
				loaded.add(indexedSourceName);
			} else if (isPlaying(indexedSourceName)) {
				if (blocking) {
					return false;
				}

				// Source already playing, create new source for same sound
				// effect.
				return playSound(sourceName, x, y, false, index + 1);
			}
			getSoundSystem().stop(indexedSourceName);
			getSoundSystem().setPriority(indexedSourceName, false);
			getSoundSystem().setPosition(indexedSourceName, x, y, 0);
			getSoundSystem().setAttenuation(indexedSourceName, SoundSystemConfig.ATTENUATION_ROLLOFF);
			getSoundSystem().setDistOrRoll(indexedSourceName, SoundSystemConfig.getDefaultRolloff());
			getSoundSystem().setPitch(indexedSourceName, 1.0f);
			soundVolume = Options.getAsFloat(Options.SOUND, "1.0f");
			getSoundSystem().setVolume(indexedSourceName, soundVolume);
			getSoundSystem().activate(indexedSourceName);
			getSoundSystem().play(indexedSourceName);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#shutdown()
	 */
	@Override
	public void shutdown() {
		if (getSoundSystem() != null) {
			getSoundSystem().cleanup();
		}
	}

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#isMuted()
	 */
	@Override
	public boolean isMuted() {
		return muted;
	}

	/* (non-Javadoc)
	 * @see com.mojang.mojam.sound.ISoundPlayer#setMuted(boolean)
	 */
	@Override
	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public void setSoundSystem(SoundSystem soundSystem) {
		this.soundSystem = soundSystem;
	}

	public SoundSystem getSoundSystem() {
		return soundSystem;
	}
}
