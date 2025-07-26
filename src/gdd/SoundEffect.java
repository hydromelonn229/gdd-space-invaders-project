package gdd;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Lightweight sound effect player that pre-loads audio clips for efficient playback.
 * Avoids the expensive file I/O operations that cause lag in rapid sound playback.
 */
public class SoundEffect {
    private Clip clip;
    private String filePath;
    private boolean isLoaded = false;
    
    public SoundEffect(String filePath) {
        this.filePath = filePath;
        preloadClip();
    }
    
    /**
     * Pre-loads the audio clip into memory for instant playback
     */
    private void preloadClip() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                new File(filePath).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
            // Set volume to 50%
            setVolume(0.50f);
            isLoaded = true;
        } catch (Exception e) {
            System.err.println("Error preloading sound effect: " + filePath + " - " + e.getMessage());
            isLoaded = false;
        }
    }
    
    /**
     * Plays the sound effect instantly without file I/O lag
     */
    public void play() {
        if (!isLoaded || clip == null) {
            return;
        }
        
        try {
            // Stop if already playing and rewind to start
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }
    
    /**
     * Sets the volume of the sound effect (0.0f to 1.0f)
     */
    public void setVolume(float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = volumeControl.getMaximum() - volumeControl.getMinimum();
                float gain = (range * volume) + volumeControl.getMinimum();
                volumeControl.setValue(gain);
            } catch (Exception e) {
                System.err.println("Volume control not supported for this audio format.");
            }
        }
    }
    
    /**
     * Stops the sound effect
     */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    /**
     * Releases resources
     */
    public void dispose() {
        if (clip != null) {
            clip.close();
        }
    }
    
    /**
     * Checks if the sound effect is loaded and ready to play
     */
    public boolean isReady() {
        return isLoaded && clip != null;
    }
}
