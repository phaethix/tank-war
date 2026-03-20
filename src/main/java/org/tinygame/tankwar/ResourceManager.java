package org.tinygame.tankwar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * 资源管理
 */
public class ResourceManager {
    public static BufferedImage tankL, tankU, tankR, tankD;
    public static BufferedImage bulletL, bulletU, bulletR, bulletD;
    public static BufferedImage[] explodes = new BufferedImage[16];

    static {
        try {
            // 主坦克
            tankU = ImageIO.read(Objects.requireNonNull(
                    ResourceManager.class.getClassLoader().getResourceAsStream("images/GoodTank1.png"))
            );
            tankL = rotateImage(tankU, -90);
            tankR = rotateImage(tankU, 90);
            tankD = rotateImage(tankU, 180);

            // 子弹
            bulletU = ImageIO.read(Objects.requireNonNull(
                    ResourceManager.class.getClassLoader().getResourceAsStream("images/bulletU.gif"))
            );
            bulletL = rotateImage(bulletU, -90);
            bulletR = rotateImage(bulletU, 90);
            bulletD = rotateImage(bulletU, 180);

            // 爆炸序列帧
            for (int i = 0; i < 16; i++) {
                explodes[i] = ImageIO.read(Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResourceAsStream("images/e" + (i + 1) + ".gif"))
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static BufferedImage rotateImage(final BufferedImage bufferedimage, final int degree) {
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type))
                .createGraphics()
        ).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(degree), (double) w / 2, (double) h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();

        return img;
    }
}
