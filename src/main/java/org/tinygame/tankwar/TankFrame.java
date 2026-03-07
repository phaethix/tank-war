package org.tinygame.tankwar;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 坦克大战游戏主窗口类
 */
public class TankFrame extends Frame {
    public static final int GAME_WIDTH = 800, GAME_HEIGHT = 600;

    Tank tank = new Tank(200, 200, Dir.DOWN, this);
    List<Bullet> bullets = new ArrayList<>();

    public TankFrame() {
        this.setTitle("Tank War");
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(GAME_WIDTH, GAME_HEIGHT);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.addKeyListener(new KeyListener());
    }

    class KeyListener extends KeyAdapter {
        boolean bL, bU, bR, bD;

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> bL = true;
                case KeyEvent.VK_UP -> bU = true;
                case KeyEvent.VK_RIGHT -> bR = true;
                case KeyEvent.VK_DOWN -> bD = true;
                case KeyEvent.VK_CONTROL -> tank.fire();
                default -> {
                }
            }
            updateTankDir();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> bL = false;
                case KeyEvent.VK_UP -> bU = false;
                case KeyEvent.VK_RIGHT -> bR = false;
                case KeyEvent.VK_DOWN -> bD = false;
                default -> {
                }
            }
            updateTankDir();
        }

        private void updateTankDir() {
            tank.setMoving((bL || bU || bR || bD));

            if (bL) tank.setDir(Dir.LEFT);
            else if (bU) tank.setDir(Dir.UP);
            else if (bR) tank.setDir(Dir.RIGHT);
            else if (bD) tank.setDir(Dir.DOWN);
        }
    }

    @Override
    public void paint(Graphics g) {
        Color color = g.getColor();
        g.setColor(Color.WHITE);
        g.drawString("Bullets: " + bullets.size(), 10, 60);
        g.setColor(color);

        tank.paint(g);
        bullets.forEach(bullet -> bullet.paint(g));
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

        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }
}
