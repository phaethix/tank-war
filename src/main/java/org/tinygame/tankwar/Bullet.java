package org.tinygame.tankwar;

import java.awt.*;

/**
 * 子弹类
 */
public class Bullet {
    private static final int SPEED = 10;
    public static final int WIDTH = ResourceManager.bulletU.getWidth();
    public static final int HEIGHT = ResourceManager.bulletU.getHeight();

    private int x, y;
    private final Dir dir;
    private boolean inactive;
    private final Group group;

    public Bullet(int x, int y, Dir dir, Group group) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.group = group;
    }

    public void paint(Graphics g) {
        if (inactive) return;

        switch (dir) {
            case LEFT -> g.drawImage(ResourceManager.bulletL, x, y, null);
            case UP -> g.drawImage(ResourceManager.bulletU, x, y, null);
            case RIGHT -> g.drawImage(ResourceManager.bulletR, x, y, null);
            case DOWN -> g.drawImage(ResourceManager.bulletD, x, y, null);
            default -> {
            }
        }

        move();
    }

    public boolean isInactive() {
        return inactive;
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

    public void collideWith(Tank tank) {
        // 忽略无效物体（非活跃子弹或坦克）及友军之间的碰撞
        if (this.inactive || tank.isInactive() || this.group == tank.getGroup()) {
            return;
        }

        Rectangle r1 = new Rectangle(this.x, this.y, WIDTH, HEIGHT);
        Rectangle r2 = new Rectangle(tank.getX(), tank.getY(), Tank.WIDTH, Tank.HEIGHT);
        if (r1.intersects(r2)) {
            tank.destroy();
            this.destroy();
        }
    }

    private void destroy() {
        this.inactive = true;
    }
}
