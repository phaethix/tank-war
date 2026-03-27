package org.tinygame.tankwar;

import java.util.concurrent.TimeUnit;

import org.tinygame.tankwar.config.GameConfig;

public class Main {
    void main() {
        var tf = new TankFrame();

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
