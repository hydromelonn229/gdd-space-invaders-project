package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {

    public Alien2(int x, int y) {
        super(x, y);
        initAlien2(x, y);
    }
    
    private void initAlien2(int x, int y) {
        this.x = x;
        this.y = y;

        // Use a different image or color for Alien2 to distinguish it
        var ii = new ImageIcon(IMG_ENEMY); // For now, use same image but we can change this
        
        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(
            ii.getIconWidth() * SCALE_FACTOR,
            ii.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act(int direction) {
        // Alien2 moves downward like Alien1 but slightly faster to be more aggressive
        this.y += 2; // Moves 2 pixels per frame (faster than Alien1's 1 pixel)
    }

    // Implementation of abstract act() method from Sprite
    @Override
    public void act() {
        act(0); // Use the parameterized version with no horizontal movement
    }

    // Alien2 never shoots - always returns null
    @Override
    public Object getBomb() {
        return null;
    }
    
    // Method to check if this alien collides with the player
    public boolean collidesWithPlayer(Player player) {
        if (!this.isVisible() || !player.isVisible()) {
            return false;
        }
        
        int alienX = this.getX();
        int alienY = this.getY();
        int playerX = player.getX();
        int playerY = player.getY();
        
        // Check collision using bounding boxes
        return (alienX < playerX + PLAYER_WIDTH &&
                alienX + ALIEN_WIDTH > playerX &&
                alienY < playerY + PLAYER_HEIGHT &&
                alienY + ALIEN_HEIGHT > playerY);
    }
}
