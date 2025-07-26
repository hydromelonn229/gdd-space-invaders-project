package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien1 extends Enemy {

    private Bomb bomb;

    public Alien1(int x, int y) {
        super(x, y);
        bomb = new Bomb(x, y);
    }

    public void act(int direction) {
        this.y++;
        // Update bomb position to follow the alien when not active
        if (bomb != null) {
            bomb.updatePosition(this.x, this.y);
        }
    }

    // Implementation of abstract act() method from Sprite
    @Override
    public void act() {
        act(0); // Use the parameterized version with no horizontal movement
    }

    @Override
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

            // Position bomb at the bottom center of the alien
            this.x = x + (ALIEN_WIDTH / 2) - 3; // Center horizontally, adjust for bomb width
            this.y = y + ALIEN_HEIGHT; // Position at bottom of alien

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            
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
        public void act() {
            if (!destroyed) {
                this.y += 2; // Bomb moves faster than alien (alien moves 1 pixel per frame)
            }
        }

        // Method to update bomb position when alien moves
        public void updatePosition(int alienX, int alienY) {
            if (destroyed) {
                // Keep bomb positioned relative to alien when not active
                this.x = alienX + (ALIEN_WIDTH / 2) - 3;
                this.y = alienY + ALIEN_HEIGHT;
            }
        }
    }
}
