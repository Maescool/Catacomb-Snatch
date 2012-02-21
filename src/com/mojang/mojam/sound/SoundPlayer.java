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

import com.mojang.mojam.network.TurnSynchronizer;

public class SoundPlayer {

    private final Class<? extends Library> libraryType;
    private SoundSystem soundSystem;
    private boolean oggPlaybackSupport = true;
    private boolean wavPlaybackSupport = true;
    private boolean muted = false;

    private static final String BACKGROUND_TRACK = "Background music";
    private static final int MAX_SOURCES_PER_SOUND = 5;

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

    public void startBackgroundMusic() {
        String backgroundTrack = "/sound/Background " + (TurnSynchronizer.synchedRandom.nextInt(4) + 1) + ".ogg";
        if (!isMuted() && hasOggPlaybackSupport() && !isPlaying(BACKGROUND_TRACK)) {
            soundSystem.backgroundMusic(BACKGROUND_TRACK, SoundPlayer.class.getResource(backgroundTrack), backgroundTrack, true);
        }
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
                soundSystem.newSource(false, indexedSourceName, SoundPlayer.class.getResource(sourceName), sourceName, false, x, y, 0, SoundSystemConfig.ATTENUATION_ROLLOFF,
                        SoundSystemConfig.getDefaultRolloff());
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