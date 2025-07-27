package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Enemy extends Sprite {

    // private Bomb bomb;
    protected boolean useFirstFrame = true;
    protected int animationCounter = 0;
    protected static final int ANIMATION_SPEED = 10; // frames between animation changes - reduced for faster animation

    // Hitbox dimensions for enemy sprites
    protected static final int ENEMY_HITBOX_WIDTH = 28;  // Slightly smaller than sprite for fairer gameplay
    protected static final int ENEMY_HITBOX_HEIGHT = 28; // Square hitbox for aliens
    protected static final int ENEMY_HITBOX_OFFSET_X = 0; // No additional offset needed since we center around x,y
    protected static final int ENEMY_HITBOX_OFFSET_Y = 0; // No additional offset needed since we center around x,y

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

    // Get the hitbox bounds for collision detection
    public Rectangle getBounds() {
        // Use raw coordinates (x, y) instead of adjusted getX(), getY() to avoid double offset
        int hitboxX = x - (ENEMY_HITBOX_WIDTH / 2) + ENEMY_HITBOX_OFFSET_X;
        int hitboxY = y - (ENEMY_HITBOX_HEIGHT / 2) + ENEMY_HITBOX_OFFSET_Y;
        
        return new Rectangle(
            hitboxX, 
            hitboxY, 
            ENEMY_HITBOX_WIDTH, 
            ENEMY_HITBOX_HEIGHT
        );
    }

    // Override collision detection to use custom hitbox
    @Override
    public boolean collidesWith(Sprite other) {
        if (other == null || !this.isVisible() || !other.isVisible()) {
            return false;
        }
        
        Rectangle thisBounds = this.getBounds();
        
        // Check if other sprite has getBounds method (like Player or Enemy)
        Rectangle otherBounds;
        if (other instanceof Player) {
            otherBounds = ((Player) other).getBounds();
        } else if (other instanceof Enemy) {
            otherBounds = ((Enemy) other).getBounds();
        } else {
            // For other sprites, use image dimensions
            int otherWidth = (other.getImage() != null) ? other.getImage().getWidth(null) : 16;
            int otherHeight = (other.getImage() != null) ? other.getImage().getHeight(null) : 16;
            otherWidth = Math.max(otherWidth, 16);
            otherHeight = Math.max(otherHeight, 16);
            otherBounds = new Rectangle(other.getX(), other.getY(), otherWidth, otherHeight);
        }
        
        return thisBounds.intersects(otherBounds);
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
