package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class SpeedUp extends PowerUp {

    public SpeedUp(int x, int y) {
        super(x, y);
        // Set image with consistent size for all power-ups (30x30 pixels)
        ImageIcon ii = new ImageIcon(IMG_POWERUP_SPEEDUP);
        int SCALE_FACTOR = 1; // Use global scaling factor for power-ups
        var scaledImage = ii.getImage().getScaledInstance(45 * SCALE_FACTOR, 45 * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act() {
        // SpeedUp specific behavior can be added here
        // For now, it just moves down the screen
        this.y += 2; // Move down by 2 pixel each frame
    }

    public void upgrade(Player player) {
        // Permanently increase the player's speed by +2 (up to 4 upgrades)
        if (isVisible()) {
            player.increaseSpeed();
            this.die(); // Remove the power-up after use
        }
    }

}
