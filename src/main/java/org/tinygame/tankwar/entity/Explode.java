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

    // 默认按正常游戏帧处理：绘制爆炸并推进动画帧。
    // 暂停时会改走 paint(g, false)，让爆炸停在当前帧上。
    public void paint(Graphics g) {
        paint(g, true);
    }

    public void paint(Graphics g, boolean advancing) {
        if (inactive) return;

        g.drawImage(ResourceManager.explodes[step], x, y, null);

        if (advancing) {
            step++;
            if (step >= ResourceManager.explodes.length) {
                inactive = true;
            }
        }
    }
}
