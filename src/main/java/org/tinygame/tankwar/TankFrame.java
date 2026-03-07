package org.tinygame.tankwar;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 坦克大战游戏主窗口类
 */
public class TankFrame extends Frame {
    Tank tank = new Tank(200, 200, Dir.DOWN);

    public TankFrame() {
        this.setTitle("Tank War");
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(800, 600);

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
        tank.paint(g);
    }
}
