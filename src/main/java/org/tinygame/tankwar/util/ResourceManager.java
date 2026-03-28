package org.tinygame.tankwar.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.tinygame.tankwar.config.GameConfig;

/**
 * 资源管理器
 */
public final class ResourceManager {
    public static final BufferedImage tankL, tankU, tankR, tankD;
    public static final BufferedImage badTankL, badTankU, badTankR, badTankD;
    public static final BufferedImage bulletL, bulletU, bulletR, bulletD;
    public static final BufferedImage badBulletL, badBulletU, badBulletR, badBulletD;
    public static final BufferedImage[] explodes = new BufferedImage[GameConfig.CFG.image.explodeCount()];

    private ResourceManager() {
    }

    static {
        try {
            // 玩家坦克
            tankU = ImageProcessor.loadImage(GameConfig.CFG.image.playerTank());
            tankL = ImageProcessor.rotateImage(tankU, -90);
            tankR = ImageProcessor.rotateImage(tankU, 90);
            tankD = ImageProcessor.rotateImage(tankU, 180);
            // 敌方坦克
            badTankU = ImageProcessor.loadImage(GameConfig.CFG.image.enemyTank());
            badTankL = ImageProcessor.rotateImage(badTankU, -90);
            badTankR = ImageProcessor.rotateImage(badTankU, 90);
            badTankD = ImageProcessor.rotateImage(badTankU, 180);
            // 子弹
            bulletU = ImageProcessor.loadImage(GameConfig.CFG.image.bullet());
            bulletL = ImageProcessor.rotateImage(bulletU, -90);
            bulletR = ImageProcessor.rotateImage(bulletU, 90);
            bulletD = ImageProcessor.rotateImage(bulletU, 180);
            badBulletU = ImageProcessor.loadImage(GameConfig.CFG.image.enemyBullet());
            badBulletL = ImageProcessor.rotateImage(badBulletU, -90);
            badBulletR = ImageProcessor.rotateImage(badBulletU, 90);
            badBulletD = ImageProcessor.rotateImage(badBulletU, 180);
            // 爆炸帧
            for (int i = 0; i < GameConfig.CFG.image.explodeCount(); i++) {
                explodes[i] = ImageProcessor.loadImage(
                        GameConfig.CFG.image.explodePrefix() + (i + 1) + GameConfig.CFG.image.explodeSuffix());
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
