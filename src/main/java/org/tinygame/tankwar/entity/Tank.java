package org.tinygame.tankwar.entity;

import lombok.Getter;
import lombok.Setter;
import org.tinygame.tankwar.TankFrame;
import org.tinygame.tankwar.config.GameConfig;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.Group;
import org.tinygame.tankwar.util.Audio;
import org.tinygame.tankwar.util.ResourceManager;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 坦克类
 */
@Getter
public class Tank {
    public static final int WIDTH = ResourceManager.tankD.getWidth();
    public static final int HEIGHT = ResourceManager.tankD.getHeight();

    private final TankFrame frame;
    private final Group group;
    private final Random random = new Random();
    private final Rectangle rect = new Rectangle();
    private int speed;

    private static final long PLAYER_FIRE_INTERVAL_NANOS =
            TimeUnit.MILLISECONDS.toNanos(GameConfig.CFG.playerFireIntervalMs());

    private int x, y;
    private int prevX, prevY;
    @Setter private Dir dir;
    @Setter private boolean moving;
    private boolean inactive;
    private long lastFireAtNanos;

    public Tank(int x, int y, Dir dir, Group group, TankFrame frame) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
        this.dir = dir;
        this.group = group;
        this.frame = frame;
        this.speed = GameConfig.CFG.playerTank.speed();
        rect.x = this.x;
        rect.y = this.y;
        rect.width = WIDTH;
        rect.height = HEIGHT;
    }

    // 默认按正常游戏帧处理：绘制坦克并推进移动/AI。
    // 暂停时会改走 paint(g, false)，只保留当前画面，不更新状态。
    public void paint(Graphics g) {
        paint(g, true);
    }

    public void paint(Graphics g, boolean advancing) {
        if (inactive) return;

        var isGood = group == Group.GOOD;
        var image = switch (dir) {
            case LEFT  -> isGood ? ResourceManager.tankL : ResourceManager.badTankL;
            case UP    -> isGood ? ResourceManager.tankU : ResourceManager.badTankU;
            case RIGHT -> isGood ? ResourceManager.tankR : ResourceManager.badTankR;
            case DOWN  -> isGood ? ResourceManager.tankD : ResourceManager.badTankD;
        };
        g.drawImage(image, x, y, null);
        if (advancing) {
            move();
        }
    }

    private void move() {
        if (!moving) return;

        if (group == Group.GOOD) {
            Audio.play(Audio.TANK_MOVE);
        } else {
            // 随机化敌方坦克每次移动的速度，使其运动更难预判
            speed = random.nextInt(1, GameConfig.CFG.enemyTank.speed() + 1);
        }

        // 保存移动前的位置，碰撞时可回退
        prevX = x;
        prevY = y;

        switch (dir) {
            case LEFT   -> x -= speed;
            case UP     -> y -= speed;
            case RIGHT  -> x += speed;
            case DOWN   -> y += speed;
        }

        boundsCheck();

        // 更新碰撞矩形位置
        rect.x = this.x;
        rect.y = this.y;

        if (group == Group.BAD && random.nextInt(100) > GameConfig.CFG.enemyTank.fireThreshold()) {
            this.fire();
        }

        if (group == Group.BAD && random.nextInt(100) > GameConfig.CFG.enemyTank.turnThreshold()) {
            this.dir = Dir.random();
        }
    }

    // 坦克间碰撞检测：碰撞后双方回退到移动前的位置
    public void collideWith(Tank other) {
        if (this == other || this.inactive || other.inactive)
            return;

        if (this.getRect().intersects(other.getRect())) {
            this.rollback();
            other.rollback();
        }
    }

    private void rollback() {
        x = prevX;
        y = prevY;
    }

    private void boundsCheck() {
        int X = TankFrame.GAME_WIDTH - WIDTH - GameConfig.CFG.boundary.margin();
        int Y = TankFrame.GAME_HEIGHT - HEIGHT - GameConfig.CFG.boundary.margin();

        x = Math.clamp(x, GameConfig.CFG.boundary.margin(), X);
        y = Math.clamp(y, GameConfig.CFG.boundary.topOffset(), Y);
    }

    public void fire() {
        if (group == Group.GOOD && !canPlayerFireNow()) {
            return;
        }

        int bx = this.x + Tank.WIDTH / 2 - Bullet.WIDTH / 2;
        int by = this.y + Tank.HEIGHT / 2 - Bullet.HEIGHT / 2;
        frame.bullets.add(new Bullet(bx, by, dir, group, frame));
        if (group == Group.GOOD) {
            lastFireAtNanos = System.nanoTime();
            Audio.play(Audio.TANK_FIRE);
        }
    }

    private boolean canPlayerFireNow() {
        long now = System.nanoTime();
        return lastFireAtNanos == 0L || now - lastFireAtNanos >= PLAYER_FIRE_INTERVAL_NANOS;
    }

    public void destroy() {
        this.inactive = true;
    }
}
