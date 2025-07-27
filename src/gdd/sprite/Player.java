package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 450; // Moved up from 540 to bring player higher on screen
    private int currentSpeed = 2;
    
    // Shot limit properties
    private int maxShots = 1; // Start with maximum 1 shot
    private int shotUpgrades = 0; // Track number of shot upgrades taken
    
    // Permanent speed boost properties
    private int speedUpgrades = 0; // Track number of speed upgrades taken (max 4)

    // Animation properties
    private int currentFrame = 0; // 0, 1, or 2 for the three frames
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 15; // frames between animation changes

    private Rectangle bounds = new Rectangle(175,100,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        // Start with the first frame of animation
        updateImage();

        setX(START_X);
        setY(START_Y);
    }

    private void updateImage() {
        String imagePath;
        switch (currentFrame) {
            case 0: imagePath = IMG_PLAYER_1; break;
            case 1: imagePath = IMG_PLAYER_2; break;
            case 2: imagePath = IMG_PLAYER_3; break;
            default: imagePath = IMG_PLAYER_1; break;
        }
        
        var ii = new ImageIcon(imagePath);
        
        System.out.println("Original image: " + imagePath);
        System.out.println("Original dimensions: " + ii.getIconWidth() + "x" + ii.getIconHeight());
        
        // Check if the animation image loaded successfully
        if (ii.getIconWidth() <= 0 || ii.getIconHeight() <= 0) {
            System.out.println("Animation image failed, using fallback IMG_PLAYER");
            ii = new ImageIcon(IMG_PLAYER);
        }

        // Use fixed dimensions for player sprites to ensure visibility
        int playerWidth = 100; // Increased from 30 to make player bigger
        int playerHeight = 100; // Increased from 30 to make player bigger
        
        System.out.println("Scaling to: " + playerWidth + "x" + playerHeight);

        // Scale the image to fixed dimensions
        var scaledImage = ii.getImage().getScaledInstance(
            playerWidth, playerHeight,
            java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
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

    @Override
    public void act() {
        // Handle animation
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            currentFrame = (currentFrame + 1) % 3; // Cycle through 0, 1, 2
            updateImage();
            animationCounter = 0;
        }
        
        // Handle movement
        x += dx;

        if (x <= 2) {
            x = 2;
        } else if (x >= BOARD_WIDTH - (PLAYER_WIDTH * SCALE_FACTOR) + 30) {
            x = BOARD_WIDTH - (PLAYER_WIDTH * SCALE_FACTOR) + 30;
        }
    }

    // Get maximum number of shots allowed
    public int getMaxShots() {
        return maxShots;
    }
    
    // Increase shot limit by 1 (up to maximum of 5)
    public void increaseShotLimit() {
        if (shotUpgrades < 4) { // Can take up to 4 upgrades (1 + 4 = 5 max shots)
            shotUpgrades++;
            maxShots++;
        }
    }
    
    // Get number of shot upgrades taken
    public int getShotUpgrades() {
        return shotUpgrades;
    }
    
    // Permanently increase speed by +2 for each upgrade (up to 4 upgrades max)
    public void increaseSpeed() {
        if (speedUpgrades < 4) { // Cap at 4 speed upgrades
            speedUpgrades++;
            currentSpeed += 2; // Permanent +2 speed increase
        }
    }
    
    // Get number of speed upgrades taken
    public int getSpeedUpgrades() {
        return speedUpgrades;
    }
    
    // Check if player has any speed upgrades (for display purposes)
    public boolean hasSpeedUpgrades() {
        return speedUpgrades > 0;
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
