package gdd.sprite;

public class Alien1 extends Enemy {

    private int shootCooldown = 0; // Frames until alien can shoot again
    private static final int SHOOT_COOLDOWN_TIME = 180; // 3 seconds at 60 FPS

    public Alien1(int x, int y) {
        super(x, y);
        // Set random initial cooldown so aliens don't all shoot at once
        this.shootCooldown = (int)(Math.random() * SHOOT_COOLDOWN_TIME);
    }

    public void act(int direction) {
        // Handle animation first
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            useFirstFrame = !useFirstFrame;
            updateImage();
            animationCounter = 0;
        }
        
        // Then handle movement
        this.y++; // Move alien downward
        
        // Decrease cooldown
        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }

    // Implementation of abstract act() method from Sprite
    @Override
    public void act() {
        // This is rarely called since the game uses act(direction)
        // Just call the parameterized version with no movement
        act(0);
    }

    // Check if alien can shoot
    public boolean canShoot() {
        return shootCooldown <= 0;
    }
    
    // Reset cooldown after shooting
    public void resetShootCooldown() {
        shootCooldown = SHOOT_COOLDOWN_TIME;
    }

    // Alien1 no longer manages its own bomb - bombs are now managed separately
    @Override
    public Object getBomb() {
        return null; // No longer has integrated bomb
    }
}
