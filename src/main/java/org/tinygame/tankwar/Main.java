package org.tinygame.tankwar;

import java.util.concurrent.TimeUnit;

public class Main {
    static void main() {
        TankFrame tf = new TankFrame();

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
