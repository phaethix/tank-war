package org.tinygame.tankwar;

import java.awt.*;

public class Tank {
    private static final int SPEED = 10;

    private int x, y;
    private Dir dir;
    private boolean moving;
    private TankFrame frame;

    public Tank(int x, int y, Dir dir, TankFrame frame) {
        super();
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.frame = frame;
    }


    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public Dir getDir() {
        return dir;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void paint(Graphics g) {
        g.fillRect(x, y, 50, 50);
        move();
    }

    private void move() {
        if (!moving) return;

        switch (dir) {
            case LEFT -> x -= SPEED;
            case UP -> y -= SPEED;
            case RIGHT -> x += SPEED;
            case DOWN -> y += SPEED;
            default -> {
            }
        }
    }

    public void fire() {
        frame.bullet = new Bullet(x, y, dir);
    }
}
