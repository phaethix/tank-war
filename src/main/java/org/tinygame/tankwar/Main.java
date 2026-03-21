package org.tinygame.tankwar;

import org.tinygame.tankwar.entity.Tank;
import org.tinygame.tankwar.enums.Dir;
import org.tinygame.tankwar.enums.Group;

import java.util.concurrent.TimeUnit;

public class Main {
    void main() {
        var tf = new TankFrame();

        // 初始化敌军坦克
        for (int i = 0; i < 5; i++) {
            tf.tanks.add(new Tank(50 + i * 80, 200, Dir.DOWN, Group.BAD, tf));
        }

        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException _) {
                    break;
                }
                tf.repaint();
            }
        });
    }
}
