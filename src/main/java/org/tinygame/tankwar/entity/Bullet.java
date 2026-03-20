package org.tinygame.tankwar.entity;

import org.tinygame.tankwar.TankFrame;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.Group;
import org.tinygame.tankwar.util.ResourceManager;

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
    private final TankFrame frame;

    public Bullet(int x, int y, Dir dir, Group group, TankFrame frame) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.group = group;
        this.frame = frame;
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
            // 在坦克中心位置产生爆炸效果
            int ex = tank.getX() + Tank.WIDTH / 2 - Explode.WIDTH / 2;
            int ey = tank.getY() + Tank.HEIGHT / 2 - Explode.HEIGHT / 2;
            frame.explodes.add(new Explode(ex, ey));
            tank.destroy();
            this.destroy();
        }
    }

    private void destroy() {
        this.inactive = true;
    }
}
