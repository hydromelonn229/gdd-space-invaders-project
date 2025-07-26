package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Boss extends Enemy {

    private int health = 20; // Boss needs 20 shots to kill
    private int shootCooldown = 0; // Frames until boss can shoot again
    private static final int SHOOT_COOLDOWN_TIME = 300; // 5 seconds at 60 FPS
    private int direction = 1; // 1 for right, -1 for left
    private static final int MOVE_SPEED = 2; // Pixels per frame for zig-zag movement
    
    // Boss dimensions (bigger than regular aliens)
    private static final int BOSS_WIDTH = 80;
    private static final int BOSS_HEIGHT = 60;

    public Boss(int x, int y) {
        super(x, y);
        initBoss(x, y);
        // Set initial cooldown so boss doesn't shoot immediately
        this.shootCooldown = SHOOT_COOLDOWN_TIME / 2;
    }
    
    private void initBoss(int x, int y) {
        this.x = x;
        this.y = y;

        // Use alien image for now, but scaled larger for boss
        var ii = new ImageIcon(IMG_ENEMY);
        
        // Scale the boss to be much larger
        var scaledImage = ii.getImage().getScaledInstance(
            BOSS_WIDTH,
            BOSS_HEIGHT,
            java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act(int direction) {
        // Boss moves in zig-zag pattern (left and right only)
        this.x += this.direction * MOVE_SPEED;
        
        // Change direction when hitting screen edges
        if (this.x <= 0) {
            this.direction = 1; // Move right
        } else if (this.x >= BOARD_WIDTH - BOSS_WIDTH) {
            this.direction = -1; // Move left
        }
        
        // Decrease shoot cooldown
        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }

    // Implementation of abstract act() method from Sprite
    @Override
    public void act() {
        act(0); // Use the parameterized version
    }

    // Check if boss can shoot
    public boolean canShoot() {
        return shootCooldown <= 0;
    }
    
    // Reset cooldown after shooting
    public void resetShootCooldown() {
        shootCooldown = SHOOT_COOLDOWN_TIME;
    }
    
    // Get current health
    public int getHealth() {
        return health;
    }
    
    // Take damage when hit by player shot
    public void takeDamage() {
        health--;
        if (health <= 0) {
            setDying(true);
        }
    }
    
    // Check if boss is dead
    public boolean isDead() {
        return health <= 0;
    }
    
    // Get boss dimensions for collision detection
    public int getBossWidth() {
        return BOSS_WIDTH;
    }
    
    public int getBossHeight() {
        return BOSS_HEIGHT;
    }

    // Boss doesn't use regular bomb system
    @Override
    public Object getBomb() {
        return null;
    }
}
