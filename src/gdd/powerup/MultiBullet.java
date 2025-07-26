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
        
        // Set consistent size for all power-ups (30x30 pixels)
        var scaledImage = ii.getImage().getScaledInstance(
            30, 30,
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
            // Enable multi-bullet for 15 seconds (900 frames at 60fps)
            player.enableMultiBullet(900);
            this.die(); // Remove the power-up after collection
        }
    }
}
