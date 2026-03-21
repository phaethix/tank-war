package org.tinygame.tankwar.entity;

import lombok.Getter;
import lombok.Setter;
import org.tinygame.tankwar.TankFrame;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.Group;
import org.tinygame.tankwar.util.Audio;
import org.tinygame.tankwar.util.ResourceManager;

import java.awt.*;
import java.util.Random;

/**
 * 坦克类
 */
@Getter
public class Tank {
    private static final int SPEED = 3;
    private static final int BORDER_MARGIN = 2;
    private static final int TOP_BOUNDARY_OFFSET = 28;

    public static final int WIDTH = ResourceManager.tankD.getWidth();
    public static final int HEIGHT = ResourceManager.tankD.getHeight();

    private final TankFrame frame;
    private final Group group;
    private final Random random = new Random();

    private int x, y;
    @Setter private Dir dir;
    @Setter private boolean moving;
    private boolean inactive;

    public Tank(int x, int y, Dir dir, Group group, TankFrame frame) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.group = group;
        this.frame = frame;
    }

    public void paint(Graphics g) {
        var isGood = group == Group.GOOD;
        var image = switch (dir) {
            case LEFT  -> isGood ? ResourceManager.tankL : ResourceManager.badTankL;
            case UP    -> isGood ? ResourceManager.tankU : ResourceManager.badTankU;
            case RIGHT -> isGood ? ResourceManager.tankR : ResourceManager.badTankR;
            case DOWN  -> isGood ? ResourceManager.tankD : ResourceManager.badTankD;
        };
        g.drawImage(image, x, y, null);
        move();
    }

    private void move() {
        if (!moving) return;

        if (group == Group.GOOD) {
            Audio.play(Audio.TANK_MOVE);
        }

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

        x = Math.clamp(x, BORDER_MARGIN, X);
        y = Math.clamp(y, TOP_BOUNDARY_OFFSET, Y);
    }

    public void fire() {
        int bx = this.x + Tank.WIDTH / 2 - Bullet.WIDTH / 2;
        int by = this.y + Tank.HEIGHT / 2 - Bullet.HEIGHT / 2;
        frame.bullets.add(new Bullet(bx, by, dir, group, frame));
        if (group == Group.GOOD) {
            Audio.play(Audio.TANK_FIRE);
        }
    }

    public void destroy() {
        this.inactive = true;
    }
}
