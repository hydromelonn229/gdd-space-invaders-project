package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import gdd.SoundEffect;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.powerup.MultiBullet;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Bomb;
import gdd.sprite.Boss;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene2 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<Bomb> bombs;
    private Player player;
    private Boss boss; // Boss enemy for final fight
    private boolean bossSpawned = false;
    private boolean bossPhase = false;

    final int BLOCKHEIGHT = 40;  // Smaller blocks for different visual
    final int BLOCKWIDTH = 40;

    private int direction = -1;
    private int deaths = 0;
    private int requiredKills = 5; // Regular enemies before boss spawn

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    // Scene 2 specific background pattern - sparse asteroid field
    private final int[][] MAP = {
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}
    };

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private SoundEffect explosionSound;
    private SoundEffect laserSound;

    // More aggressive spawning for Scene 2 (but toned down)
    private int nextAlienSpawnFrame = 0;

    private final int minSpawnInterval = 20; // 0.33 seconds - faster spawning for Scene 2
    private final int maxSpawnInterval = 75; // 1.25 seconds - more frequent than Scene 1
    private final int spawnChance = 5; // Balanced chance (1 in 5) for 5-minute gameplay
    
    // Random power-up spawning variables for Scene 2
    private int nextPowerUpSpawnFrame = 0;
    private final int minPowerUpInterval = 240; // 4 seconds minimum (faster than Scene 1)
    private final int maxPowerUpInterval = 480; // 8 seconds maximum (faster than Scene 1)
    private final int powerUpSpawnChance = 3; // 1 in 3 chance when eligible (more frequent)


    public Scene2(Game game) {
        this.game = game;
        loadSpawnDetails();
        initAudio(); // Initialize audio and sound effects
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/beatbox.wav"; // Reuse audio for now
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
            
            // Initialize sound effects using the new lightweight SoundEffect system
            explosionSound = new SoundEffect("src/audio/explosion.wav");
            laserSound = new SoundEffect("src/audio/laser.wav");
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        // Removed fixed power-up spawns - now using random spawning system
        
        // Set initial next spawn frame for random aliens
        nextAlienSpawnFrame = randomizer.nextInt(maxSpawnInterval - minSpawnInterval) + minSpawnInterval;
        
        // Set initial next spawn frame for random power-ups
        nextPowerUpSpawnFrame = randomizer.nextInt(maxPowerUpInterval - minPowerUpInterval) + minPowerUpInterval;
    }

    private void spawnRandomAlien() {
        // Reduced chance of groups in Scene 2 (25% chance for group, was 40%)
        int alienCount = (randomizer.nextInt(8) < 2) ? randomizer.nextInt(3) + 2 : 1; // Max 4 aliens instead of 6
        
        for (int i = 0; i < alienCount; i++) {
            int randomX = randomizer.nextInt(BOARD_WIDTH - ALIEN_WIDTH);
            
            if (alienCount > 1) {
                int spacing = Math.min(100, BOARD_WIDTH / alienCount);
                randomX = (i * spacing) + randomizer.nextInt(Math.max(15, spacing - ALIEN_WIDTH));
                randomX = Math.max(0, Math.min(randomX, BOARD_WIDTH - ALIEN_WIDTH));
            }
            
            int spawnY = -ALIEN_HEIGHT - (i * 20);
            
            // In Scene 2 (final scene), higher chance for Alien2 - 40% chance for kamikaze aliens
            Enemy enemy;
            if (randomizer.nextInt(10) < 4) {
                enemy = new Alien2(randomX, spawnY); // 40% chance - kamikaze alien
            } else {
                enemy = new Alien1(randomX, spawnY); // 60% chance - shooting alien
            }
            enemies.add(enemy);
        }
        
        int baseInterval = (alienCount > 1) ? 
            (int)(maxSpawnInterval * 1.5) : // Longer delay after group spawns (was 1.2)
            randomizer.nextInt(maxSpawnInterval - minSpawnInterval) + minSpawnInterval;
        nextAlienSpawnFrame = frame + baseInterval;
    }
    
    private void spawnRandomPowerUp() {
        // Generate random X position within screen bounds (with margin for power-up size)
        int randomX = randomizer.nextInt(BOARD_WIDTH - 30); // 30 is power-up width
        
        // Spawn power-up above screen
        int spawnY = -30; // 30 is power-up height
        
        // Randomly choose between SpeedUp and MultiBullet (50/50 chance)
        PowerUp powerUp;
        if (randomizer.nextBoolean()) {
            powerUp = new SpeedUp(randomX, spawnY);
        } else {
            powerUp = new MultiBullet(randomX, spawnY);
        }
        
        powerups.add(powerUp);
        
        // Set next power-up spawn time
        int nextInterval = randomizer.nextInt(maxPowerUpInterval - minPowerUpInterval) + minPowerUpInterval;
        nextPowerUpSpawnFrame = frame + nextInterval;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
            if (explosionSound != null) {
                explosionSound.dispose();
            }
            if (laserSound != null) {
                laserSound.dispose();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        bombs = new ArrayList<>();
        player = new Player();
        deaths = 0; // Reset death count for new scene
        
        // Reset power-up spawning
        nextPowerUpSpawnFrame = randomizer.nextInt(maxPowerUpInterval - minPowerUpInterval) + minPowerUpInterval;
    }

    private void drawMap(Graphics g) {
        // Asteroid field background
        int scrollOffset = (frame * 2) % BLOCKHEIGHT; // Faster scrolling
        int baseRow = (frame * 2) / BLOCKHEIGHT;
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2;

        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            int mapRow = (baseRow + screenRow) % MAP.length;
            int y = BOARD_HEIGHT - ((screenRow * BLOCKHEIGHT) - scrollOffset);

            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) {
                continue;
            }

            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    int x = col * BLOCKWIDTH;
                    drawAsteroid(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }
    }

    private void drawAsteroid(Graphics g, int x, int y, int width, int height) {
        // Draw asteroid-like objects
        g.setColor(new Color(150, 150, 150)); // Gray asteroids
        
        // Main asteroid body
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 8, centerY - 6, 16, 12);
        
        // Smaller debris
        g.setColor(new Color(100, 100, 100));
        g.fillOval(centerX - 15, centerY - 8, 6, 4);
        g.fillOval(centerX + 10, centerY - 5, 4, 6);
        g.fillOval(centerX - 5, centerY + 8, 8, 4);
        
        // Tiny particles
        g.setColor(new Color(180, 180, 180));
        g.fillOval(centerX - 18, centerY + 2, 2, 2);
        g.fillOval(centerX + 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 2, centerY - 15, 2, 2);
    }

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
            if (enemy.isDying()) {
                enemy.die();
            }
        }
    }

    private void drawBoss(Graphics g) {
        if (boss != null && boss.isVisible()) {
            g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);
            
            // Draw boss health bar
            int healthBarWidth = 200;
            int healthBarHeight = 20;
            int healthBarX = (BOARD_WIDTH - healthBarWidth) / 2;
            int healthBarY = 90; // Below dashboard
            
            // Background
            g.setColor(Color.RED);
            g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            
            // Health
            g.setColor(Color.GREEN);
            int currentHealthWidth = (healthBarWidth * boss.getHealth()) / 20; // 20 is max health
            g.fillRect(healthBarX, healthBarY, currentHealthWidth, healthBarHeight);
            
            // Border
            g.setColor(Color.WHITE);
            g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            
            // Boss name
            g.setColor(Color.YELLOW);
            g.setFont(g.getFont().deriveFont(14f));
            g.drawString("BOSS", healthBarX + healthBarWidth/2 - 20, healthBarY - 5);
        }
        
        if (boss != null && boss.isDying()) {
            boss.die();
        }
    }

    private void drawPowerUps(Graphics g) {
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }
            if (p.isDying()) {
                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {
        for (Bomb bomb : bombs) {
            if (bomb != null && !bomb.isDestroyed()) {
                g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }
        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        if (inGame) {
            // Translate all game elements down to make room for dashboard
            g.translate(0, 80);
            
            drawMap(g);
            drawExplosions(g);
            drawPowerUps(g);
            drawAliens(g);
            drawBoss(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
            
            // Reset translation
            g.translate(0, -80);
        } else {
            // Translate game over screen down as well
            g.translate(0, 80);
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
            g.translate(0, -80);
        }

        // Draw Dashboard last (on top, at original position)
        drawDashboard(g);

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawDashboard(Graphics g) {
        // Dashboard positioned at top of screen
        g.setColor(Color.BLACK); // Solid black background
        g.fillRect(0, 0, BOARD_WIDTH, 80);
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, BOARD_WIDTH - 1, 80 - 1);
        
        // Calculate time elapsed (assuming 60 FPS)
        int seconds = frame / 60;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        
        // Dashboard content
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(12f));
        
        // Line 1: Scene info and Timer
        if (bossPhase) {
            g.drawString("SCENE 2 - BOSS FIGHT", 10, 15);
        } else {
            g.drawString("SCENE 2 - FINAL", 10, 15);
        }
        g.drawString("TIME: " + timeStr, 140, 15);
        g.drawString("FRAME: " + frame, 240, 15);
        
        // Line 2: Score
        g.setColor(Color.YELLOW);
        if (bossPhase && boss != null) {
            g.drawString("BOSS HP: " + boss.getHealth() + "/20", 10, 35);
        } else {
            g.drawString("SCORE: " + deaths + "/" + requiredKills, 10, 35);
        }
        
        // Line 3: Player Status
        g.setColor(Color.CYAN);
        g.drawString("SPEED: " + player.getSpeed() + " (+" + (player.getSpeedUpgrades() * 2) + ")", 10, 55);
        g.drawString("Speed Upgrades: " + player.getSpeedUpgrades() + "/4", 250, 55);
        
        // Line 4: Shot Status
        g.setColor(Color.ORANGE);
        g.drawString("MAX SHOTS: " + player.getMaxShots(), 10, 70);
        g.drawString("Shot Upgrades: " + player.getShotUpgrades() + "/4", 250, 70);
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {
        // Random alien spawning (only if boss hasn't spawned)
        if (!bossSpawned && frame >= nextAlienSpawnFrame) {
            if (randomizer.nextInt(spawnChance) == 0) {
                spawnRandomAlien();
            } else {
                nextAlienSpawnFrame = frame + randomizer.nextInt(10) + 5;
            }
        }

        // Random power-up spawning system
        if (frame >= nextPowerUpSpawnFrame) {
            // Additional random chance to make spawning less predictable
            if (randomizer.nextInt(powerUpSpawnChance) == 0) {
                spawnRandomPowerUp();
            } else {
                // If we don't spawn this time, set a shorter next check interval
                nextPowerUpSpawnFrame = frame + randomizer.nextInt(90) + 20;
            }
        }

        // Boss spawning logic
        if (deaths >= requiredKills && !bossSpawned) {
            // Spawn boss
            boss = new Boss(BOARD_WIDTH / 2 - 40, 120); // Center horizontally, below dashboard
            bossSpawned = true;
            bossPhase = true;
            message = "BOSS FIGHT!";
        }

        // Check win condition - boss must be defeated
        if (bossSpawned && boss != null && boss.isDead()) {
            inGame = false;
            timer.stop();
            message = "VICTORY! All scenes completed!";
            // Return to title screen after a delay
            Timer transitionTimer = new Timer(5000, e -> {
                ((Timer)e.getSource()).stop();
                game.loadTitle();
            });
            transitionTimer.setRepeats(false);
            transitionTimer.start();
        }

        player.act();

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
            }
        }

        // Enemies
        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
                
                // Check for Alien2 collision with player (kamikaze attack)
                if (enemy instanceof Alien2) {
                    Alien2 alien2 = (Alien2) enemy;
                    if (alien2.collidesWithPlayer(player)) {
                        // Player dies on collision with Alien2
                        var ii = new ImageIcon(IMG_EXPLOSION);
                        player.setImage(ii.getImage());
                        player.setDying(true);
                        
                        // Alien2 also explodes
                        alien2.setImage(ii.getImage());
                        alien2.setDying(true);
                        explosions.add(new Explosion(alien2.getX(), alien2.getY()));
                        
                        // Play explosion sound
                        try {
                            if (explosionSound != null && explosionSound.isReady()) {
                                explosionSound.play();
                            }
                        } catch (Exception e) {
                            System.err.println("Error playing explosion sound: " + e.getMessage());
                        }
                    }
                }
                
                // Remove enemies that have passed beyond the bottom of the screen
                if (enemy.getY() > BOARD_HEIGHT) {
                    enemy.die(); // Mark enemy for removal
                }
            }
            if (!enemy.isVisible() || enemy.isDying()) {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);

        // Boss handling
        if (boss != null && boss.isVisible()) {
            boss.act(0); // Move boss in zig-zag pattern
            
            // Boss shooting - 5 bullets straight down every 5 seconds
            if (boss.canShoot()) {
                // Create 5 bombs in a spread pattern straight down
                int bossX = boss.getX() + boss.getBossWidth() / 2;
                int bossY = boss.getY() + boss.getBossHeight();
                
                // Create 5 bullets with spread
                for (int i = 0; i < 5; i++) {
                    int offsetX = (i - 2) * 15; // Spread bullets: -30, -15, 0, 15, 30 pixels from center
                    Bomb bossBomb = new Bomb(bossX + offsetX, bossY);
                    bossBomb.setDestroyed(false);
                    bombs.add(bossBomb);
                }
                boss.resetShootCooldown();
            }
        }

        // Shots
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + ALIEN_WIDTH)
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + ALIEN_HEIGHT)) {

                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        deaths++;
                        shot.die();
                        shotsToRemove.add(shot);
                        
                        // Play explosion sound
                        try {
                            if (explosionSound != null && explosionSound.isReady()) {
                                explosionSound.play();
                            }
                        } catch (Exception e) {
                            System.err.println("Error playing explosion sound: " + e.getMessage());
                        }
                    }
                }

                // Check collision with boss
                if (boss != null && boss.isVisible() && shot.isVisible()) {
                    int bossX = boss.getX();
                    int bossY = boss.getY();
                    
                    if (shotX >= bossX && shotX <= (bossX + boss.getBossWidth())
                            && shotY >= bossY && shotY <= (bossY + boss.getBossHeight())) {
                        
                        // Boss takes damage
                        boss.takeDamage();
                        explosions.add(new Explosion(shotX, shotY));
                        shot.die();
                        shotsToRemove.add(shot);
                        
                        // Play explosion sound
                        try {
                            if (explosionSound != null && explosionSound.isReady()) {
                                explosionSound.play();
                            }
                        } catch (Exception e) {
                            System.err.println("Error playing explosion sound: " + e.getMessage());
                        }
                    }
                }

                int y = shot.getY();
                y -= 20;

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // Bombs
        for (Enemy enemy : enemies) {
            if (enemy instanceof Alien1) {
                Alien1 alien1 = (Alien1) enemy;
                int chance = randomizer.nextInt(12); // More frequent bombing chance

                if (chance == CHANCE && enemy.isVisible() && alien1.canShoot()) {
                    // Create new bomb at alien position
                    int bombX = enemy.getX() + (ALIEN_WIDTH / 2) - 3;
                    int bombY = enemy.getY() + ALIEN_HEIGHT;
                    Bomb newBomb = new Bomb(bombX, bombY);
                    newBomb.setDestroyed(false);
                    bombs.add(newBomb);
                    
                    // Reset alien's shooting cooldown
                    alien1.resetShootCooldown();
                }
            }
        }
        
        List<Bomb> bombsToRemove = new ArrayList<>();
        for (Bomb bomb : bombs) {
            if (bomb == null || bomb.isDestroyed()) {
                bombsToRemove.add(bomb);
                continue;
            }
            
            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()
                    && bombX >= (playerX)
                    && bombX <= (playerX + PLAYER_WIDTH)
                    && bombY >= (playerY)
                    && bombY <= (playerY + PLAYER_HEIGHT)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
                bombsToRemove.add(bomb);
            }
            
            if (!bomb.isDestroyed()) {
                bomb.act();
                if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                    bombsToRemove.add(bomb);
                }
            }
        }
        bombs.removeAll(bombsToRemove);
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                // Play laser sound
                try {
                    if (laserSound != null && laserSound.isReady()) {
                        laserSound.play();
                    }
                } catch (Exception ex) {
                    System.err.println("Error playing laser sound: " + ex.getMessage());
                }
                
                if (shots.size() < player.getMaxShots()) {
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                }
            }
        }
    }
}
