package org.tinygame.tankwar.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * 图像处理工具类
 */
public final class ImageProcessor {

    private ImageProcessor() {
    }

    /**
     * 将给定图像按中心顺时针旋转指定角度。
     *
     * @param source 原始图像
     * @param degree 旋转角度，单位：度（顺时针为正）
     * @return 旋转后的图像
     */
    public static BufferedImage rotateImage(final BufferedImage source, final int degree) {
        var image = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                source.getColorModel().getTransparency());
        var g2d = image.createGraphics();

        try {
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.rotate(
                    Math.toRadians(degree),
                    source.getWidth() / 2.0,
                    source.getHeight() / 2.0);
            g2d.drawImage(source, 0, 0, null);
        } finally {
            g2d.dispose();
        }
        return image;
    }

    /**
     * 从类路径加载图像资源。
     *
     * @param path 资源路径
     * @return 加载后的图像
     * @throws IOException 资源不存在或读取失败时抛出
     */
    public static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(
                ImageProcessor.class.getClassLoader().getResourceAsStream(path),
                "Image resource not found: " + path));
    }
}
