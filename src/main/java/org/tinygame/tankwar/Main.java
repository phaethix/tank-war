package org.tinygame.tankwar;

import org.tinygame.tankwar.config.GameConfig;
import org.tinygame.tankwar.entity.Tank;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.Group;

import java.util.concurrent.TimeUnit;

public class Main {
    void main() {
        var tf = new TankFrame();

        // 初始化敌军坦克
        for (int i = 0; i < GameConfig.CFG.enemyTank.count(); i++) {
            int tx = GameConfig.CFG.enemyTank.startX() + (i % 10) * GameConfig.CFG.enemyTank.spacingX();
            int ty = GameConfig.CFG.enemyTank.startY() + (i / 10) * 60;
            tf.tanks.add(new Tank(tx, ty, Dir.random(), Group.BAD, tf));
        }

        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(GameConfig.CFG.gameLoop.frameIntervalMs());
                } catch (InterruptedException _) {
                    break;
                }
                tf.repaint();
            }
        });
    }
}
