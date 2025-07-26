package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int currentSpeed = 2;
    private int baseSpeed = 2; // Store the original speed
    
    // Multi-bullet power-up properties
    private boolean multiBulletActive = false;
    private int multiBulletFramesLeft = 0;
    
    // Speed boost power-up properties
    private boolean speedBoostActive = false;
    private int speedBoostFramesLeft = 0;
    private int speedBoostAmount = 0;

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public void act() {
        x += dx;

        if (x <= 2) {
            x = 2;
        } else if (x >= BOARD_WIDTH - (PLAYER_WIDTH * SCALE_FACTOR) + 30) {
            x = BOARD_WIDTH - (PLAYER_WIDTH * SCALE_FACTOR) + 30;
        }
        
        // Update multi-bullet timer
        if (multiBulletActive && multiBulletFramesLeft > 0) {
            multiBulletFramesLeft--;
            if (multiBulletFramesLeft <= 0) {
                multiBulletActive = false;
            }
        }
        
        // Update speed boost timer
        if (speedBoostActive && speedBoostFramesLeft > 0) {
            speedBoostFramesLeft--;
            if (speedBoostFramesLeft <= 0) {
                speedBoostActive = false;
                currentSpeed = baseSpeed; // Reset to original speed
            }
        }
    }

    // Enable multi-bullet mode for specified number of frames
    public void enableMultiBullet(int frames) {
        multiBulletActive = true;
        multiBulletFramesLeft = frames;
    }
    
    // Check if multi-bullet mode is active
    public boolean isMultiBulletActive() {
        return multiBulletActive;
    }
    
    // Get remaining frames for multi-bullet effect
    public int getMultiBulletFramesLeft() {
        return multiBulletFramesLeft;
    }
    
    // Enable speed boost for specified number of frames
    public void enableSpeedBoost(int frames, int boostAmount) {
        speedBoostActive = true;
        speedBoostFramesLeft = frames;
        speedBoostAmount = boostAmount;
        currentSpeed = baseSpeed + boostAmount;
    }
    
    // Check if speed boost is active
    public boolean isSpeedBoostActive() {
        return speedBoostActive;
    }
    
    // Get remaining frames for speed boost effect
    public int getSpeedBoostFramesLeft() {
        return speedBoostFramesLeft;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }
}
