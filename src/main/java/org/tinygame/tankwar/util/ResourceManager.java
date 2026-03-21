package org.tinygame.tankwar.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 资源管理器
 */
public final class ResourceManager {
    public static final BufferedImage tankL, tankU, tankR, tankD;
    public static final BufferedImage badTankL, badTankU, badTankR, badTankD;
    public static final BufferedImage bulletL, bulletU, bulletR, bulletD;
    public static final BufferedImage[] explodes = new BufferedImage[16];

    private ResourceManager() {
    }

    static {
        try {
            // 玩家坦克
            tankU = ImageProcessor.loadImage("images/GoodTank1.png");
            tankL = ImageProcessor.rotateImage(tankU, -90);
            tankR = ImageProcessor.rotateImage(tankU, 90);
            tankD = ImageProcessor.rotateImage(tankU, 180);
            // 敌方坦克
            badTankU = ImageProcessor.loadImage("images/BadTank1.png");
            badTankL = ImageProcessor.rotateImage(badTankU, -90);
            badTankR = ImageProcessor.rotateImage(badTankU, 90);
            badTankD = ImageProcessor.rotateImage(badTankU, 180);
            // 子弹
            bulletU = ImageProcessor.loadImage("images/bulletU.png");
            bulletL = ImageProcessor.rotateImage(bulletU, -90);
            bulletR = ImageProcessor.rotateImage(bulletU, 90);
            bulletD = ImageProcessor.rotateImage(bulletU, 180);
            // 爆炸帧
            for (int i = 0; i < 16; i++) {
                explodes[i] = ImageProcessor.loadImage("images/e" + (i + 1) + ".gif");
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
