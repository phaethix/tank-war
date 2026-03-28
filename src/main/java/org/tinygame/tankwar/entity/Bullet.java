package org.tinygame.tankwar.entity;

import lombok.Getter;
import org.tinygame.tankwar.TankFrame;
import org.tinygame.tankwar.config.GameConfig;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.Group;
import org.tinygame.tankwar.util.Audio;
import org.tinygame.tankwar.util.ResourceManager;

import java.awt.*;

/**
 * 子弹类
 */
@Getter
public class Bullet {
    private static final int SPEED = GameConfig.CFG.bullet.speed();
    public static final int WIDTH = ResourceManager.bulletU.getWidth();
    public static final int HEIGHT = ResourceManager.bulletU.getHeight();

    private final Dir dir;
    private final Group group;
    private final TankFrame frame;

    private int x, y;
    private boolean inactive;

    private final Rectangle rect = new Rectangle();

    public Bullet(int x, int y, Dir dir, Group group, TankFrame frame) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.group = group;
        this.frame = frame;
        rect.x = this.x;
        rect.y = this.y;
        rect.width = WIDTH;
        rect.height = HEIGHT;
    }

    // 默认按正常游戏帧处理：绘制子弹并推进位置。
    // 暂停时会改走 paint(g, false)，只绘制当前帧，不继续移动。
    public void paint(Graphics g) {
        paint(g, true);
    }

    public void paint(Graphics g, boolean advancing) {
        if (inactive) return;

        var image = switch (group) {
            case GOOD -> switch (dir) {
                case LEFT   -> ResourceManager.bulletL;
                case UP     -> ResourceManager.bulletU;
                case RIGHT  -> ResourceManager.bulletR;
                case DOWN   -> ResourceManager.bulletD;
            };
            case BAD -> switch (dir) {
                case LEFT   -> ResourceManager.badBulletL;
                case UP     -> ResourceManager.badBulletU;
                case RIGHT  -> ResourceManager.badBulletR;
                case DOWN   -> ResourceManager.badBulletD;
            };
        };
        g.drawImage(image, x, y, null);

        if (advancing) {
            move();
        }
    }

    private void move() {
        switch (dir) {
            case LEFT   -> x -= SPEED;
            case UP     -> y -= SPEED;
            case RIGHT  -> x += SPEED;
            case DOWN   -> y += SPEED;
        }

        // 更新碰撞矩形位置
        rect.x = this.x;
        rect.y = this.y;

        if (x < 0 || y < 0 || x > TankFrame.GAME_WIDTH || y > TankFrame.GAME_HEIGHT) {
            inactive = true;
        }
    }

    public void collideWith(Tank tank) {
        // 忽略无效物体（非活跃子弹或坦克）及友军之间的碰撞
        if (this.inactive || tank.isInactive() || this.group == tank.getGroup()) {
            return;
        }

        if (rect.intersects(tank.getRect())) {
            // 在坦克中心位置产生爆炸效果
            int ex = tank.getX() + Tank.WIDTH / 2 - Explode.WIDTH / 2;
            int ey = tank.getY() + Tank.HEIGHT / 2 - Explode.HEIGHT / 2;
            frame.explodes.add(new Explode(ex, ey));
            // 播放爆炸音效
            Audio.play(Audio.EXPLODE);
            tank.destroy();
            this.destroy();
        }
    }

    private void destroy() {
        this.inactive = true;
    }
}
