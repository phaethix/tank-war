package org.tinygame.tankwar;

import java.util.concurrent.TimeUnit;

public class Main {
    static void main() {
        TankFrame tf = new TankFrame();

        // 初始化敌军坦克
        for (int i = 0; i < 5; i++) {
            tf.tanks.add(new Tank(50 + i * 80, 200, Dir.DOWN, tf));
        }

        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
                tf.repaint();
            }
        });
    }
}
