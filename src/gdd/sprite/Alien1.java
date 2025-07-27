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
        this.y++; // Move alien downward
        
        // Decrease cooldown
        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }

    // Implementation of abstract act() method from Sprite
    @Override
    public void act() {
        act(0); // Use the parameterized version with no horizontal movement
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
