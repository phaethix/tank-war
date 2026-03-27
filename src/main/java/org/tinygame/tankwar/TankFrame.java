package org.tinygame.tankwar;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import org.tinygame.tankwar.config.GameConfig;
import org.tinygame.tankwar.entity.Bullet;
import org.tinygame.tankwar.entity.Explode;
import org.tinygame.tankwar.entity.Tank;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.GameState;
import org.tinygame.tankwar.enums.Group;
import org.tinygame.tankwar.util.Audio;

/**
 * 坦克大战游戏主窗口类
 */
public class TankFrame extends Frame {
    public static final int GAME_WIDTH = GameConfig.CFG.window.width();
    public static final int GAME_HEIGHT = GameConfig.CFG.window.height();

    Tank tank = new Tank(
            GameConfig.CFG.playerTank.initX(),
            GameConfig.CFG.playerTank.initY(),
            Dir.UP, Group.GOOD, this);
    public List<Tank> tanks = new ArrayList<>();
    public List<Bullet> bullets = new ArrayList<>();
    public List<Explode> explodes = new ArrayList<>();
    private GameState gameState = GameState.PLAYING;

    public TankFrame() {
        this.setTitle(GameConfig.CFG.window.title());
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(GAME_WIDTH, GAME_HEIGHT);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 窗口关闭时停止背景音乐，释放音频资源
                Audio.stopBgm();
                System.exit(0);
            }
        });

        this.addKeyListener(new KeyListener());
    }

    class KeyListener extends KeyAdapter {
        boolean bL, bU, bR, bD;

        @Override
        public void keyPressed(KeyEvent e) {
            if (gameState != GameState.PLAYING)
                return;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> bL = true;
                case KeyEvent.VK_UP -> bU = true;
                case KeyEvent.VK_RIGHT -> bR = true;
                case KeyEvent.VK_DOWN -> bD = true;
                case KeyEvent.VK_SPACE -> tank.fire();
                case KeyEvent.VK_Q -> Audio.toggleBgm();
            }
            updateTankDir();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (gameState != GameState.PLAYING)
                return;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> bL = false;
                case KeyEvent.VK_UP -> bU = false;
                case KeyEvent.VK_RIGHT -> bR = false;
                case KeyEvent.VK_DOWN -> bD = false;
            }
            updateTankDir();
        }

        private void updateTankDir() {
            tank.setMoving(bL || bU || bR || bD);

            if (bL)
                tank.setDir(Dir.LEFT);
            else if (bU)
                tank.setDir(Dir.UP);
            else if (bR)
                tank.setDir(Dir.RIGHT);
            else if (bD)
                tank.setDir(Dir.DOWN);
        }
    }

    @Override
    public void paint(Graphics g) {
        Color color = g.getColor();
        g.setColor(Color.WHITE);
        g.drawString("Bullets: " + bullets.size(), 10, 60);
        g.drawString("Enemies: " + tanks.size(), 10, 80);
        g.setColor(color);

        tank.paint(g);
        tanks.forEach(t -> {
            t.setMoving(gameState == GameState.PLAYING);
            t.paint(g);
        });
        bullets.forEach(bullet -> bullet.paint(g));
        bullets.forEach(bullet -> {
            tanks.forEach(bullet::collideWith);
            bullet.collideWith(tank);
        });
        explodes.forEach(explode -> explode.paint(g));

        // 坦克间碰撞检测（敌军互撞 + 玩家与敌军）
        for (int i = 0; i < tanks.size(); i++) {
            for (int j = i + 1; j < tanks.size(); j++) {
                tanks.get(i).collideWith(tanks.get(j));
            }
            tank.collideWith(tanks.get(i));
        }

        drawGameState(g);
    }

    Image offScreenImage = null;

    @Override
    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.BLACK);
        gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        gOffScreen.setColor(c);

        // 移除不活跃的子弹
        bullets.removeIf(Bullet::isInactive);
        // 移除不活跃的坦克
        tanks.removeIf(Tank::isInactive);
        // 移除已播放完毕的爆炸
        explodes.removeIf(Explode::isInactive);

        updateGameState();

        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    private void updateGameState() {
        if (gameState != GameState.PLAYING)
            return;

        if (tank.isInactive()) {
            gameState = GameState.DEFEAT;
            tank.setMoving(false);
            Audio.stopBgm();
            return;
        }

        if (tanks.isEmpty()) {
            gameState = GameState.VICTORY;
            tank.setMoving(false);
            Audio.stopBgm();
        }
    }

    private void drawGameState(Graphics g) {
        if (gameState == GameState.PLAYING)
            return;

        var graphics = (Graphics2D) g.create();
        graphics.setColor(new Color(0, 0, 0, 170));
        graphics.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        graphics.setFont(new Font("SansSerif", Font.BOLD, 48));
        graphics.setColor(gameState == GameState.VICTORY ? Color.GREEN : Color.RED);

        String message = gameState == GameState.VICTORY ? "Victory!" : "Game Over!";
        FontMetrics metrics = graphics.getFontMetrics();
        int x = (GAME_WIDTH - metrics.stringWidth(message)) / 2;
        int y = GAME_HEIGHT / 2;
        graphics.drawString(message, x, y);
        graphics.dispose();
    }
}
