package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Enemy extends Sprite {

    // private Bomb bomb;
    protected boolean useFirstFrame = true;
    protected int animationCounter = 0;
    protected static final int ANIMATION_SPEED = 10; // frames between animation changes - reduced for faster animation

    public Enemy(int x, int y) {

        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        // Store the intended center position
        this.x = x;
        this.y = y;

        // bomb = new Bomb(x, y);

        // Start with the first frame of animation
        updateImage();
    }

    protected void updateImage() {
        String imagePath = useFirstFrame ? IMG_ALIEN1_1 : IMG_ALIEN1_2;
        var ii = new ImageIcon(imagePath);

        // Scale the image to a smaller size than the global scaling factor
        int enemyScaleFactor = 1; // Reduced from SCALE_FACTOR (3) to make enemies smaller
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * enemyScaleFactor,
                ii.getIconHeight() * enemyScaleFactor,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act(int direction) {

        this.x += direction;
    }

    // Implementation of abstract act() method from Sprite
    @Override
    public void act() {
        // Handle animation
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            useFirstFrame = !useFirstFrame;
            updateImage();
            animationCounter = 0;
        }
        
        // No recursive call - let animation work properly
    }

    // Override getX and getY to return adjusted coordinates for better positioning
    @Override
    public int getX() {
        // Adjust position to move sprites slightly left and center them
        int offsetX = -3; // Move 5 pixels to the left to correct positioning
        return x - (image != null ? image.getWidth(null) / 2 : 0) + offsetX;
    }

    @Override
    public int getY() {
        // Center vertically
        return y - (image != null ? image.getHeight(null) / 2 : 0);
    }

    // Methods to get the actual center position
    public int getCenterX() {
        return x;
    }

    public int getCenterY() {
        return y;
    }

    // Override collision detection to use consistent hit box regardless of animation frame
    @Override
    public boolean collidesWith(Sprite other) {
        if (other == null || !this.isVisible() || !other.isVisible()) {
            return false;
        }
        
        // Use fixed dimensions for consistent collision detection during animation
        int enemyWidth = 32; // Fixed width for all enemy sprites
        int enemyHeight = 32; // Fixed height for all enemy sprites
        int hitBoxPadding = 5; // Additional padding for easier hits
        
        // Enemy boundaries (centered and consistent)
        int enemyLeft = this.getCenterX() - (enemyWidth / 2) - hitBoxPadding;
        int enemyRight = this.getCenterX() + (enemyWidth / 2) + hitBoxPadding;
        int enemyTop = this.getCenterY() - (enemyHeight / 2) - hitBoxPadding;
        int enemyBottom = this.getCenterY() + (enemyHeight / 2) + hitBoxPadding;
        
        // Other sprite boundaries
        int otherLeft = other.getX();
        int otherRight = other.getX() + (other.getImage() != null ? other.getImage().getWidth(null) : 0);
        int otherTop = other.getY();
        int otherBottom = other.getY() + (other.getImage() != null ? other.getImage().getHeight(null) : 0);
        
        return enemyLeft < otherRight && enemyRight > otherLeft
                && enemyTop < otherBottom && enemyBottom > otherTop;
    }

    // Default implementation returns null, can be overridden by subclasses
    public Object getBomb() {
        return null;
    }
/* 
    public Bomb getBomb() {

        return bomb;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {

            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
        }
    }
*/
}
