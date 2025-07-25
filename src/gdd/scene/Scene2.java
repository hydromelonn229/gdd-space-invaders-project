package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
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
    private List<gdd.sprite.Alien1.Bomb> bombs;
    private Player player;

    final int BLOCKHEIGHT = 40;  // Smaller blocks for different visual
    final int BLOCKWIDTH = 40;

    private int direction = -1;
    private int deaths = 0;
    private int requiredKills = 200; // 5-minute target (Scene 2)

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    // Scene 2 specific background pattern - asteroid field
    private final int[][] MAP = {
        {1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0},
        {0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1},
        {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0},
        {0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1},
        {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0},
        {0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0},
        {0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1},
        {1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0},
        {0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1},
        {1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1},
        {1, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0},
        {0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0}
    };

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;

    // More aggressive spawning for Scene 2
    private int nextAlienSpawnFrame = 0;
    private final int minSpawnInterval = 20; // 0.33 seconds - faster spawning for Scene 2
    private final int maxSpawnInterval = 75; // 1.25 seconds - more frequent than Scene 1
    private final int spawnChance = 5; // Balanced chance (1 in 5) for 5-minute gameplay

    public Scene2(Game game) {
        this.game = game;
        loadSpawnDetails();
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav"; // Reuse audio for now
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        // More power-ups for harder stage
        spawnMap.put(100, new SpawnDetails("PowerUp-SpeedUp", 150, 0));
        spawnMap.put(300, new SpawnDetails("PowerUp-SpeedUp", 250, 0));
        spawnMap.put(500, new SpawnDetails("PowerUp-SpeedUp", 350, 0));
        
        // Set initial next spawn frame for random aliens
        nextAlienSpawnFrame = randomizer.nextInt(maxSpawnInterval - minSpawnInterval) + minSpawnInterval;
    }

    private void spawnRandomAlien() {
        // Higher chance of groups in Scene 2 (40% chance for group)
        int alienCount = (randomizer.nextInt(5) < 2) ? randomizer.nextInt(4) + 2 : 1;
        
        for (int i = 0; i < alienCount; i++) {
            int randomX = randomizer.nextInt(BOARD_WIDTH - ALIEN_WIDTH);
            
            if (alienCount > 1) {
                int spacing = Math.min(100, BOARD_WIDTH / alienCount);
                randomX = (i * spacing) + randomizer.nextInt(Math.max(15, spacing - ALIEN_WIDTH));
                randomX = Math.max(0, Math.min(randomX, BOARD_WIDTH - ALIEN_WIDTH));
            }
            
            int spawnY = -ALIEN_HEIGHT - (i * 20);
            
            Enemy enemy = new Alien1(randomX, spawnY);
            enemies.add(enemy);
        }
        
        int baseInterval = (alienCount > 1) ? 
            (int)(maxSpawnInterval * 1.2) : 
            randomizer.nextInt(maxSpawnInterval - minSpawnInterval) + minSpawnInterval;
        nextAlienSpawnFrame = frame + baseInterval;
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
        for (gdd.sprite.Alien1.Bomb bomb : bombs) {
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

        g.setColor(Color.white);
        g.drawString("SCENE 2 - FRAME: " + frame, 10, 10);
        g.drawString("KILLS: " + deaths + "/" + requiredKills, 10, 25);

        if (inGame) {
            drawMap(g);
            drawExplosions(g);
            drawPowerUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
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
        // Random alien spawning
        if (frame >= nextAlienSpawnFrame) {
            if (randomizer.nextInt(spawnChance) == 0) {
                spawnRandomAlien();
            } else {
                nextAlienSpawnFrame = frame + randomizer.nextInt(10) + 5;
            }
        }

        // Predefined spawns
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y);
                    enemies.add(enemy);
                    break;
                case "PowerUp-SpeedUp":
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                default:
                    System.out.println("Unknown spawn type: " + sd.type);
                    break;
            }
        }

        // Check win condition
        if (deaths >= requiredKills) {
            inGame = false;
            timer.stop();
            message = "Scene 2 Complete! Loading Scene 3...";
            // Transition to Scene 3 after a delay
            Timer transitionTimer = new Timer(3000, e -> {
                ((Timer)e.getSource()).stop();
                game.loadScene3();
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
                if (enemy.getY() > BOARD_HEIGHT) {
                    inGame = false;
                    timer.stop();
                    message = "Invasion! Enemies escaped!";
                    break;
                }
            }
            if (!enemy.isVisible() || enemy.isDying()) {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);

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
            if (enemy instanceof gdd.sprite.Alien1) {
                gdd.sprite.Alien1 alien = (gdd.sprite.Alien1) enemy;
                gdd.sprite.Alien1.Bomb bomb = alien.getBomb();
                
                if (bomb == null) continue;

                int chance = randomizer.nextInt(12); // More frequent bombing

                if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {
                    bomb.setDestroyed(false);
                    bomb.setX(enemy.getX() + (ALIEN_WIDTH / 2) - 3);
                    bomb.setY(enemy.getY() + ALIEN_HEIGHT);
                    bombs.add(bomb);
                }
            }
        }
        
        List<gdd.sprite.Alien1.Bomb> bombsToRemove = new ArrayList<>();
        for (gdd.sprite.Alien1.Bomb bomb : bombs) {
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
                if (shots.size() < 4) {
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                }
            }
        }
    }
}
