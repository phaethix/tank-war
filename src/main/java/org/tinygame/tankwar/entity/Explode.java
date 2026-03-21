package org.tinygame.tankwar.entity;

import lombok.Getter;
import org.tinygame.tankwar.util.ResourceManager;

import java.awt.*;

/**
 * 爆炸效果类
 */
@Getter
public class Explode {
    public static final int WIDTH = ResourceManager.explodes[0].getWidth();
    public static final int HEIGHT = ResourceManager.explodes[0].getHeight();

    private final int x, y;

    private boolean inactive;
    private int step;

    public Explode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void paint(Graphics g) {
        if (inactive) return;

        g.drawImage(ResourceManager.explodes[step++], x, y, null);

        if (step >= ResourceManager.explodes.length) {
            inactive = true;
        }
    }
}
