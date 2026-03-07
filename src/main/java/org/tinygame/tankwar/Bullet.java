package org.tinygame.tankwar;

import java.awt.*;

/**
 * 子弹类
 */
public class Bullet {
    private static final int SPEED = 10;
    private static final int WIDTH = 30, HEIGHT = 30;

    private int x, y;
    private final Dir dir;
    private boolean inactive;
    private TankFrame frame;

    public Bullet(int x, int y, Dir dir, TankFrame frame) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.frame = frame;
    }

    public void paint(Graphics g) {
        if (inactive) {
            frame.bullets.remove(this);
        }
        Color c = g.getColor();
        g.setColor(Color.RED);
        g.fillOval(x, y, WIDTH, HEIGHT);
        g.setColor(c);

        move();
    }

    private void move() {
        switch (dir) {
            case LEFT -> x -= SPEED;
            case UP -> y -= SPEED;
            case RIGHT -> x += SPEED;
            case DOWN -> y += SPEED;
            default -> {
            }
        }

        if (x < 0 || y < 0 || x > TankFrame.GAME_WIDTH || y > TankFrame.GAME_HEIGHT) {
            inactive = true;
        }
    }
}
