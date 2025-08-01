package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SoundEffect;
import gdd.powerup.MultiBullet;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Bomb;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<Bomb> bombs; // Independent bomb tracking
    private Player player;
    // private Shot shot;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int score = 0; // Changed from deaths to score
    private int highScore = 0; // Track high score

    private boolean inGame = true;
    private String message = "Game Over";

    // Scene objectives - survive for 5 minutes (5 * 60 seconds * 60 FPS = 18000 frames)
    private final int SCENE_DURATION = 18000; // 5 minutes at 60 FPS

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    // TODO load this map from a file

    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };
    
    private AudioPlayer audioPlayer;
    private SoundEffect explosionSound;
    private SoundEffect laserSound;
    
    // Random spawning variables - adjusted for 5-minute gameplay
    private int nextAlienSpawnFrame = 0;
    private final int minSpawnInterval = 30; // 0.5 seconds minimum
    private final int maxSpawnInterval = 90; // 1.5 seconds maximum  
    private final int spawnChance = 6; // 1 in 6 chance per frame when eligible
    
    // Random power-up spawning variables
    private int nextPowerUpSpawnFrame = 0;
    private final int minPowerUpInterval = 300; // 5 seconds minimum
    private final int maxPowerUpInterval = 500; // 8 seconds maximum
    private final int powerUpSpawnChance = 4; // 1 in 4 chance when eligible

    public Scene1(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        loadSpawnDetails();
        initAudio(); // Initialize audio and sound effects
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
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
        // Randomly decide if we spawn 1 alien or a small group (20% chance for group)
        int alienCount = (randomizer.nextInt(5) == 0) ? randomizer.nextInt(3) + 2 : 1;
        
        for (int i = 0; i < alienCount; i++) {
            // Generate random X position within screen bounds
            int randomX = randomizer.nextInt(BOARD_WIDTH - ALIEN_WIDTH);
            
            // If spawning multiple aliens, spread them out
            if (alienCount > 1) {
                int spacing = Math.min(120, BOARD_WIDTH / alienCount);
                randomX = (i * spacing) + randomizer.nextInt(Math.max(20, spacing - ALIEN_WIDTH));
                randomX = Math.max(0, Math.min(randomX, BOARD_WIDTH - ALIEN_WIDTH));
            }
            
            // Spawn alien slightly above screen (stagger if multiple)
            int spawnY = -ALIEN_HEIGHT - (i * 25);
            
            // Randomly choose between Alien1 (shooting) and Alien2 (kamikaze) - 30% chance for Alien2
            Enemy enemy;
            if (randomizer.nextInt(10) < 3) {
                enemy = new Alien2(randomX, spawnY); // 30% chance - kamikaze alien
            } else {
                enemy = new Alien1(randomX, spawnY); // 70% chance - shooting alien
            }
            enemies.add(enemy);
        }
        
        // Set next spawn time - longer interval if we spawned multiple aliens
        int baseInterval = (alienCount > 1) ? 
            (int)(maxSpawnInterval * 1.5) : 
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
        bombs = new ArrayList<>(); // Initialize independent bombs list
        
        // Reset game state
        score = 0; // Reset score instead of deaths
        frame = 0;
        inGame = true;
        message = "Game Over";

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }s
        // }
        player = new Player();
        // shot = new Shot();
        
        // Reset spawning
        nextAlienSpawnFrame = randomizer.nextInt(maxSpawnInterval - minSpawnInterval) + minSpawnInterval;
        nextPowerUpSpawnFrame = randomizer.nextInt(maxPowerUpInterval - minPowerUpInterval) + minPowerUpInterval;
    }

    private void drawMap(Graphics g) {
        // Draw scrolling starfield background

        // Calculate smooth scrolling offset (1 pixel per frame)
        int scrollOffset = (frame) % BLOCKHEIGHT;

        // Calculate which rows to draw based on screen position
        int baseRow = (frame) / BLOCKHEIGHT;
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2; // +2 for smooth scrolling

        // Loop through rows that should be visible on screen
        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            // Calculate which MAP row to use (with wrapping)
            int mapRow = (baseRow + screenRow) % MAP.length;

            // Calculate Y position for this row
            // int y = (screenRow * BLOCKHEIGHT) - scrollOffset;
            int y = BOARD_HEIGHT - ( (screenRow * BLOCKHEIGHT) - scrollOffset );

            // Skip if row is completely off-screen
            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) {
                continue;
            }

            // Draw each column in this row
            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    // Calculate X position
                    int x = col * BLOCKWIDTH;

                    // Draw a cluster of stars
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }

    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        // Set star color to white
        g.setColor(Color.WHITE);

        // Draw multiple stars in a cluster pattern
        // Main star (larger)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        // Smaller surrounding stars
        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
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

    private void drawPowreUps(Graphics g) {

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
        // Draw all active bombs from the independent bombs list
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

        g.setColor(Color.green);

        if (inGame) {
            // Translate all game elements down to make room for dashboard
            g.translate(0, 80);
            
            drawMap(g);  // Draw background stars first
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
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
        
        // Calculate time elapsed and remaining (assuming 60 FPS)
        int seconds = frame / 60;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        
        // Calculate time remaining for the scene
        int remainingFrames = SCENE_DURATION - frame;
        int remainingSeconds = Math.max(0, remainingFrames / 60);
        int remainingMinutes = remainingSeconds / 60;
        remainingSeconds = remainingSeconds % 60;
        String remainingTimeStr = String.format("%02d:%02d", remainingMinutes, remainingSeconds);
        
        // Dashboard content
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(12f));
        
        // Line 1: Scene info and Timer
        g.drawString("STAGE 1 - SURVIVE 5 MINUTES", 10, 15);
        g.drawString("TIME: " + timeStr, 180, 15);
        g.drawString("REMAINING: " + remainingTimeStr, 260, 15);
        
        // Line 2: Score
        g.setColor(Color.YELLOW);
        g.drawString("SCORE: " + score, 10, 35);
        
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

        // Random alien spawning system
        if (frame >= nextAlienSpawnFrame) {
            // Additional random chance to make spawning less predictable
            if (randomizer.nextInt(spawnChance) == 0) {
                spawnRandomAlien();
            } else {
                // If we don't spawn this time, set a shorter next check interval
                nextAlienSpawnFrame = frame + randomizer.nextInt(15) + 5;
            }
        }

        // Random power-up spawning system
        if (frame >= nextPowerUpSpawnFrame) {
            // Additional random chance to make spawning less predictable
            if (randomizer.nextInt(powerUpSpawnChance) == 0) {
                spawnRandomPowerUp();
            } else {
                // If we don't spawn this time, set a shorter next check interval
                nextPowerUpSpawnFrame = frame + randomizer.nextInt(120) + 30;
            }
        }

        // Check if player survived for 1 minute
        if (frame >= SCENE_DURATION) {
            inGame = false;
            timer.stop();
            // Update high score if current score is higher
            if (score > highScore) {
                highScore = score;
            }
            message = "Scene 1 Complete! Proceeding to Scene 2...";
            // Transition to Scene 2 after a delay
            Timer transitionTimer = new Timer(3000, e -> {
                ((Timer)e.getSource()).stop();
                game.loadScene2();
            });
            transitionTimer.setRepeats(false);
            transitionTimer.start();
        }

        // player
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
                    if (alien2.collidesWith(player)) {
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
            
            // Remove enemies that are no longer visible or have died
            if (!enemy.isVisible() || enemy.isDying()) {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy using hitbox system
                    if (enemy.isVisible() && shot.isVisible()) {
                        Rectangle shotBounds = new Rectangle(shot.getX(), shot.getY(),
                            shot.getImage() != null ? shot.getImage().getWidth(null) : 8,
                            shot.getImage() != null ? shot.getImage().getHeight(null) : 8);
                        Rectangle enemyBounds = enemy.getBounds();
                        
                        if (shotBounds.intersects(enemyBounds)) {
                            var ii = new ImageIcon(IMG_EXPLOSION);
                            enemy.setImage(ii.getImage());
                            enemy.setDying(true);
                            explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                            
                            // Add points based on enemy type
                            if (enemy instanceof Alien1) {
                                score += 10; // Alien1 gives 10 points
                            } else if (enemy instanceof Alien2) {
                                score += 20; // Alien2 (kamikaze) gives 20 points
                            }
                            
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
                }

                int y = shot.getY();
                // y -= 4;
                y -= 10; // Reduced from 20 to 10 for slower movement

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // enemies
        // for (Enemy enemy : enemies) {
        //     int x = enemy.getX();
        //     if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
        //         direction = -1;
        //         for (Enemy e2 : enemies) {
        //             e2.setY(e2.getY() + GO_DOWN);
        //         }
        //     }
        //     if (x <= BORDER_LEFT && direction != 1) {
        //         direction = 1;
        //         for (Enemy e : enemies) {
        //             e.setY(e.getY() + GO_DOWN);
        //         }
        //     }
        // }
        // for (Enemy enemy : enemies) {
        //     if (enemy.isVisible()) {
        //         int y = enemy.getY();
        //         if (y > GROUND - ALIEN_HEIGHT) {
        //             inGame = false;
        //             message = "Invasion!";
        //         }
        //         enemy.act(direction);
        //     }
        // }
        // bombs - collision detection and management
        // Check if any Alien1s should fire new bombs
        for (Enemy enemy : enemies) {
            if (enemy instanceof Alien1) {
                Alien1 alien1 = (Alien1) enemy;
                int chance = randomizer.nextInt(15);

                // If alien should fire (random chance + cooldown check)
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
        
        // Now handle all active bombs independently
        List<Bomb> bombsToRemove = new ArrayList<>();
        for (Bomb bomb : bombs) {
            if (bomb == null || bomb.isDestroyed()) {
                bombsToRemove.add(bomb);
                continue;
            }
            
            // Check collision with player using new hitbox system
            if (player.isVisible() && !bomb.isDestroyed()) {
                Rectangle playerBounds = player.getBounds();
                Rectangle bombBounds = new Rectangle(bomb.getX(), bomb.getY(), 
                    bomb.getImage() != null ? bomb.getImage().getWidth(null) : 8,
                    bomb.getImage() != null ? bomb.getImage().getHeight(null) : 8);
                
                if (playerBounds.intersects(bombBounds)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                    bombsToRemove.add(bomb);
                }
            }
            
            // Move the bomb
            if (!bomb.isDestroyed()) {
                bomb.act(); // Use the bomb's act method for movement
                if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                    bombsToRemove.add(bomb);
                }
            }
        }
        
        // Remove destroyed bombs from the list
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
            System.out.println("Scene1.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                
                // Play laser sound
                try {
                    if (laserSound != null && laserSound.isReady()) {
                        laserSound.play();
                    }
                } catch (Exception ex) {
                    System.err.println("Error playing laser sound: " + ex.getMessage());
                }
                
                if (shots.size() < player.getMaxShots()) {
                    // Create a new shot and add it to the list
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                }
            }

        }
    }
}
