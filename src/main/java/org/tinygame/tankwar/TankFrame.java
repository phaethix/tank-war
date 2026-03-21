package org.tinygame.tankwar;

import org.tinygame.tankwar.entity.Bullet;
import org.tinygame.tankwar.entity.Explode;
import org.tinygame.tankwar.entity.Tank;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.Group;
import org.tinygame.tankwar.util.Audio;

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

    Tank tank = new Tank(210, 400, Dir.UP, Group.GOOD, this);
    public List<Tank> tanks = new ArrayList<>();
    public List<Bullet> bullets = new ArrayList<>();
    public List<Explode> explodes = new ArrayList<>();

    public TankFrame() {
        this.setTitle("Tank War");
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
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT   -> bL = true;
                case KeyEvent.VK_UP     -> bU = true;
                case KeyEvent.VK_RIGHT  -> bR = true;
                case KeyEvent.VK_DOWN   -> bD = true;
                case KeyEvent.VK_SPACE  -> tank.fire();
                case KeyEvent.VK_Q      -> Audio.toggleBgm();
            }
            updateTankDir();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT   -> bL = false;
                case KeyEvent.VK_UP     -> bU = false;
                case KeyEvent.VK_RIGHT  -> bR = false;
                case KeyEvent.VK_DOWN   -> bD = false;
            }
            updateTankDir();
        }

        private void updateTankDir() {
            tank.setMoving(bL || bU || bR || bD);

            if (bL)      tank.setDir(Dir.LEFT);
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
        g.drawString("Enemies: " + tanks.size(), 10, 80);
        g.setColor(color);

        tank.paint(g);
        tanks.forEach(t -> {
            t.setMoving(true);
            t.paint(g);
        });
        bullets.forEach(bullet -> bullet.paint(g));
        bullets.forEach(bullet -> tanks.forEach(bullet::collideWith));
        explodes.forEach(explode -> explode.paint(g));
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

        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }
}
