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

public class Scene3 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<gdd.sprite.Alien1.Bomb> bombs;
    private Player player;

    final int BLOCKHEIGHT = 60;  // Larger blocks for final stage
    final int BLOCKWIDTH = 60;

    private int direction = -1;
    private int deaths = 0;
    private int requiredKills = 250; // 5-minute target (Final Scene)

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    // Scene 3 specific background pattern - nebula/space clouds
    private final int[][] MAP = {
        {1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1},
        {1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1},
        {0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0},
        {1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1},
        {1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1},
        {0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0},
        {1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1},
        {1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1},
        {0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0},
        {1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1},
        {1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1},
        {0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0}
    };

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;

    // Most aggressive spawning for final scene
    private int nextAlienSpawnFrame = 0;
    private final int minSpawnInterval = 15; // 0.25 seconds - very fast spawning for final scene
    private final int maxSpawnInterval = 60; // 1 second - intense action for 5-minute finale
    private final int spawnChance = 4; // High chance (1 in 4) for challenging 5-minute finale

    public Scene3(Game game) {
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
        // Many power-ups for final challenging stage
        spawnMap.put(80, new SpawnDetails("PowerUp-SpeedUp", 100, 0));
        spawnMap.put(200, new SpawnDetails("PowerUp-SpeedUp", 200, 0));
        spawnMap.put(350, new SpawnDetails("PowerUp-SpeedUp", 300, 0));
        spawnMap.put(500, new SpawnDetails("PowerUp-SpeedUp", 400, 0));
        spawnMap.put(650, new SpawnDetails("PowerUp-SpeedUp", 500, 0));
        
        nextAlienSpawnFrame = randomizer.nextInt(maxSpawnInterval - minSpawnInterval) + minSpawnInterval;
    }

    private void spawnRandomAlien() {
        // Very high chance of large groups in final scene (60% chance for group)
        int alienCount = (randomizer.nextInt(5) < 3) ? randomizer.nextInt(5) + 3 : 1;
        
        for (int i = 0; i < alienCount; i++) {
            int randomX = randomizer.nextInt(BOARD_WIDTH - ALIEN_WIDTH);
            
            if (alienCount > 1) {
                int spacing = Math.min(80, BOARD_WIDTH / alienCount);
                randomX = (i * spacing) + randomizer.nextInt(Math.max(10, spacing - ALIEN_WIDTH));
                randomX = Math.max(0, Math.min(randomX, BOARD_WIDTH - ALIEN_WIDTH));
            }
            
            int spawnY = -ALIEN_HEIGHT - (i * 15); // Closer spacing
            
            Enemy enemy = new Alien1(randomX, spawnY);
            enemies.add(enemy);
        }
        
        int baseInterval = (alienCount > 1) ? 
            maxSpawnInterval : 
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
        deaths = 0;
    }

    private void drawMap(Graphics g) {
        // Nebula/cloud background
        int scrollOffset = (frame * 3) % BLOCKHEIGHT; // Even faster scrolling
        int baseRow = (frame * 3) / BLOCKHEIGHT;
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
                    drawNebula(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }
    }

    private void drawNebula(Graphics g, int x, int y, int width, int height) {
        // Draw colorful nebula clouds
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        // Multiple colored cloud layers
        g.setColor(new Color(100, 50, 150, 120)); // Purple
        g.fillOval(centerX - 15, centerY - 10, 30, 20);
        
        g.setColor(new Color(50, 100, 200, 100)); // Blue
        g.fillOval(centerX - 10, centerY - 8, 20, 16);
        
        g.setColor(new Color(200, 100, 150, 80)); // Pink
        g.fillOval(centerX - 12, centerY - 6, 24, 12);
        
        // Bright star points
        g.setColor(Color.WHITE);
        g.fillOval(centerX - 1, centerY - 1, 2, 2);
        
        // Smaller nebula particles
        g.setColor(new Color(150, 100, 200, 60));
        g.fillOval(centerX - 20, centerY + 5, 8, 6);
        g.fillOval(centerX + 15, centerY - 12, 6, 8);
        g.fillOval(centerX - 5, centerY + 12, 10, 4);
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
        g.drawString("FINAL SCENE - FRAME: " + frame, 10, 10);
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
        // Very aggressive random alien spawning
        if (frame >= nextAlienSpawnFrame) {
            if (randomizer.nextInt(spawnChance) == 0) {
                spawnRandomAlien();
            } else {
                nextAlienSpawnFrame = frame + randomizer.nextInt(8) + 3;
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
            message = "VICTORY! All scenes completed!";
            // Could transition back to title or show credits
            Timer transitionTimer = new Timer(5000, e -> {
                ((Timer)e.getSource()).stop();
                game.loadTitle(); // Return to title
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

        // Very aggressive bombing
        for (Enemy enemy : enemies) {
            if (enemy instanceof gdd.sprite.Alien1) {
                gdd.sprite.Alien1 alien = (gdd.sprite.Alien1) enemy;
                gdd.sprite.Alien1.Bomb bomb = alien.getBomb();
                
                if (bomb == null) continue;

                int chance = randomizer.nextInt(8); // Very frequent bombing

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
