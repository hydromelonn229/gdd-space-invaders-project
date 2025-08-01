package gdd.powerup;

import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiBullet extends PowerUp {

    public MultiBullet(int x, int y) {
        super(x, y);
        initPowerUp();
    }

    private void initPowerUp() {
        var ii = new ImageIcon("src/images/bullet.png");
        int SCALE_FACTOR = 1; // Use global scaling factor for power-ups
        // Set consistent size for all power-ups (30x30 pixels)
        var scaledImage = ii.getImage().getScaledInstance(
            45 * SCALE_FACTOR, 45 * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act() {
        // Multi-bullet power-up moves down the screen
        this.y += 2; // Move down by 2 pixels each frame
    }

    @Override
    public void upgrade(Player player) {
        if (isVisible()) {
            // Increase the player's maximum shot limit by 1 (up to 4 total)
            player.increaseShotLimit();
            this.die(); // Remove the power-up after collection
        }
    }
}
