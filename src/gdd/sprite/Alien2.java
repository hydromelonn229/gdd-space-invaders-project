package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {

    private int horizontalDirection = 0; // -1 for left, 0 for straight, 1 for right
    private int directionChangeTimer = 0;
    private static final int DIRECTION_CHANGE_INTERVAL = 60; // Change direction every 60 frames (1 second at 60 FPS)

    public Alien2(int x, int y) {
        super(x, y);
        // Don't call initAlien2 - let parent Enemy handle image setup with animation
        
        // Set initial random direction
        setRandomDirection();
    }
    
    // Override updateImage to use different sprites for Alien2
    @Override
    protected void updateImage() {
        String imagePath = useFirstFrame ? IMG_ALIEN2_1 : IMG_ALIEN2_2;
        var ii = new ImageIcon(imagePath);

        // Scale the image to a smaller size than the global scaling factor
        int enemyScaleFactor = 1; // Reduced from SCALE_FACTOR (3) to make enemies smaller
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * enemyScaleFactor,
                ii.getIconHeight() * enemyScaleFactor,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }
    
    private void setRandomDirection() {
        // 33% chance each for left, straight, right
        int randomChoice = (int)(Math.random() * 3);
        switch (randomChoice) {
            case 0: horizontalDirection = -1; break; // Left
            case 1: horizontalDirection = 0; break;  // Straight
            case 2: horizontalDirection = 1; break;  // Right
        }
        directionChangeTimer = 0; // Reset timer
    }
    
    public void act(int direction) {
        // Handle animation first
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            useFirstFrame = !useFirstFrame;
            updateImage();
            animationCounter = 0;
        }
        
        // Handle direction change timer
        directionChangeTimer++;
        if (directionChangeTimer >= DIRECTION_CHANGE_INTERVAL) {
            setRandomDirection(); // Change direction randomly
        }
        
        // Handle movement - downward (kamikaze) + random horizontal
        this.y += 2; // Moves 2 pixels per frame downward (faster than Alien1's 1 pixel)
        this.x += horizontalDirection; // Move left (-1), straight (0), or right (+1)
        
        // Keep alien within screen bounds
        if (this.x < 0) {
            this.x = 0;
            setRandomDirection(); // Change direction if hitting left edge
        } else if (this.x > BOARD_WIDTH - ALIEN_WIDTH) {
            this.x = BOARD_WIDTH - ALIEN_WIDTH;
            setRandomDirection(); // Change direction if hitting right edge
        }
    }

    // Implementation of abstract act() method from Sprite
    @Override
    public void act() {
        // This is rarely called since the game uses act(direction)
        // Just call the parameterized version with no movement
        act(0);
    }

    // Alien2 never shoots - always returns null
    @Override
    public Object getBomb() {
        return null;
    }
}
