package gdd.sprite;

import javax.swing.ImageIcon;

public class Bomb extends Sprite {

    private boolean destroyed;

    public Bomb(int x, int y) {
        initBomb(x, y);
    }

    private void initBomb(int x, int y) {
        setDestroyed(true);

        // Position bomb at the specified location
        this.x = x;
        this.y = y;

        var bombImg = "src/images/bomb.png";
        var ii = new ImageIcon(bombImg);
        int SCALE_FACTOR = 1; // Use global scaling factor for bombs
        // Scale the bomb image to match the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(
            ii.getIconWidth() * SCALE_FACTOR,
            ii.getIconHeight() * SCALE_FACTOR,
            java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    // Method to move the bomb downward (faster than alien movement)
    @Override
    public void act() {
        if (!destroyed) {
            this.y += 2; // Bomb moves faster than alien (alien moves 1 pixel per frame)
        }
    }
}
