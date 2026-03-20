package org.tinygame.tankwar.entity;

import org.tinygame.tankwar.TankFrame;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.Group;
import org.tinygame.tankwar.util.ResourceManager;

import java.awt.*;
import java.util.Random;

/**
 * 坦克类
 */
public class Tank {
    private static final int SPEED = 3;
    private static final int BORDER_MARGIN = 2;
    private static final int TOP_BOUNDARY_OFFSET = 28;
    public static final int WIDTH = ResourceManager.tankD.getWidth();
    public static final int HEIGHT = ResourceManager.tankD.getHeight();

    private int x, y;
    private Dir dir;
    private boolean moving;
    private final TankFrame frame;
    private boolean inactive;
    private Group group;

    private final Random random = new Random();

    public Tank(int x, int y, Dir dir, Group group, TankFrame frame) {
        super();
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.group = group;
        this.frame = frame;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public boolean isMoving() {
        return moving;
    }

    public void Moving() {
        this.moving = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Group getGroup() {
        return group;
    }

    public void paint(Graphics g) {
        switch (dir) {
            case LEFT -> g.drawImage(ResourceManager.tankL, x, y, null);
            case UP -> g.drawImage(ResourceManager.tankU, x, y, null);
            case RIGHT -> g.drawImage(ResourceManager.tankR, x, y, null);
            case DOWN -> g.drawImage(ResourceManager.tankD, x, y, null);
            default -> {
            }
        }
        move();
    }

    public boolean isInactive() {
        return inactive;
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

        boundsCheck();

        if (group == Group.BAD && random.nextInt(100) > 80) {
            this.fire();
        }

        if (group == Group.BAD && random.nextInt(100) > 95) {
            this.dir = Dir.values()[random.nextInt(Dir.values().length)];
        }
    }

    private void boundsCheck() {
        int X = TankFrame.GAME_WIDTH - WIDTH - BORDER_MARGIN;
        int Y = TankFrame.GAME_HEIGHT - HEIGHT - BORDER_MARGIN;

        x = Math.max(BORDER_MARGIN, Math.min(x, X));
        y = Math.max(TOP_BOUNDARY_OFFSET, Math.min(y, Y));
    }

    public void fire() {
        int bx = this.x + Tank.WIDTH / 2 - Bullet.WIDTH / 2;
        int by = this.y + Tank.HEIGHT / 2 - Bullet.HEIGHT / 2;
        frame.bullets.add(new Bullet(bx, by, dir, group, frame));
    }

    public void destroy() {
        this.inactive = true;
    }
}
