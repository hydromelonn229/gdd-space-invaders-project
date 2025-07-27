package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int currentSpeed = 2;
    
    // Shot limit properties
    private int maxShots = 1; // Start with maximum 1 shot
    private int shotUpgrades = 0; // Track number of shot upgrades taken
    
    // Permanent speed boost properties
    private int speedUpgrades = 0; // Track number of speed upgrades taken (max 4)

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
