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
    private int x = 200, y = 200;

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
        @Override
        public void keyPressed(KeyEvent e) {
            x += 20;
            y += 20;
            repaint();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            x -= 10;
            y -= 10;
            repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.fillRect(x, y, 50, 50);
    }
}
