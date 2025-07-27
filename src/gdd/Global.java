package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites

    public static final int BOARD_WIDTH = 716; // Doubled from 358
    public static final int BOARD_HEIGHT = 700; // Doubled from 350
    public static final int BORDER_RIGHT = 60; // Doubled from 30
    public static final int BORDER_LEFT = 5; // Doubled from 5

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 10; // Doubled from 5

    public static final int ALIEN_HEIGHT = 24; // Doubled from 12
    public static final int ALIEN_WIDTH = 30; // Doubled from 12
    public static final int ALIEN_INIT_X = 400; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 10; // 5-minute target (Scene 1)
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 100; // Updated to match actual rendered size
    public static final int PLAYER_HEIGHT = 100; // Updated to match actual rendered size

    // Images
    public static final String IMG_ENEMY = "src/images/alien.png";
    public static final String IMG_PLAYER = "src/images/player.png";
    public static final String IMG_SHOT = "src/images/shot.png";
    public static final String IMG_EXPLOSION = "src/images/explosion.png";
    public static final String IMG_TITLE = "src/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "src/images/powerup-s.png";

    public static final String IMG_BOSS_1 = "src/images/boss1.png";
    public static final String IMG_BOSS_2 = "src/images/boss2.png";


    public static final String IMG_ALIEN1_1 = "src/images/alien1.png";
    public static final String IMG_ALIEN1_2 = "src/images/alien2.png";
    public static final String IMG_ALIEN2_1 = "src/images/alien3.png";
    public static final String IMG_ALIEN2_2 = "src/images/alien4.png";
    
    public static final String IMG_PLAYER_1 = "src/images/player1.png";
    public static final String IMG_PLAYER_2 = "src/images/player2.png";
    public static final String IMG_PLAYER_3 = "src/images/player3.png";

    
}
