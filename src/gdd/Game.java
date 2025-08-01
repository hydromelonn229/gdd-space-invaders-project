package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import javax.swing.JFrame;

public class Game extends JFrame  {

    TitleScene titleScene;
    Scene1 scene1;
    Scene2 scene2;

    public Game() {
        titleScene = new TitleScene(this);
        scene1 = new Scene1(this);
        scene2 = new Scene2(this);
        initUI();
        loadTitle();
    }

    private void initUI() {

        setTitle("Space Invaders - Multi-Stage Adventure");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

    }

    public void loadTitle() {
        getContentPane().removeAll();
        add(titleScene);
        
        // Stop all other scenes
        if (scene1 != null) scene1.stop();
        if (scene2 != null) scene2.stop();
        
        titleScene.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        getContentPane().removeAll();
        add(scene1);
        
        titleScene.stop();
        if (scene2 != null) scene2.stop();
        
        scene1.start();
        revalidate();
        repaint();
    }

    public void loadScene2() {
        getContentPane().removeAll();
        add(scene2);
        
        if (scene1 != null) scene1.stop();
        titleScene.stop();
        
        scene2.start();
        revalidate();
        repaint();
    }
}