package org.tinygame.tankwar;

import java.awt.*;

/**
 * 爆炸效果类
 */
public class Explode {
    public static final int WIDTH = ResourceManager.explodes[0].getWidth();
    public static final int HEIGHT = ResourceManager.explodes[0].getHeight();

    private int x, y;
    private boolean living = true;
    private int step = 0;

    public Explode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void paint(Graphics g) {
        if (!living) return;

        g.drawImage(ResourceManager.explodes[step++], x, y, null);

        if (step >= ResourceManager.explodes.length) {
            living = false;
        }
    }

    public boolean isLiving() {
        return living;
    }
}
