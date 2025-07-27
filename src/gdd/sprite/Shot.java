package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 40;
    private static final int V_SPACE = 10;

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {

        var ii = new ImageIcon(IMG_SHOT);
        int SCALE_FACTOR = 1; // Reduced scale factor to make shots smaller
        // Scale the image to a smaller size
        var scaledImage = ii.getImage().getScaledInstance(
            (int)(ii.getIconWidth() * SCALE_FACTOR * 0.5), // 50% of original size
            (int)(ii.getIconHeight() * SCALE_FACTOR * 0.5), // 50% of original size
            java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }

    @Override
    public void act() {
        // Shot movement is handled by the scene classes
        // This method is required but not used for shots
    }
}
