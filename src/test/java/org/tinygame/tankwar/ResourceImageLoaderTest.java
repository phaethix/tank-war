package org.tinygame.tankwar;

import org.junit.Test;

import static org.junit.Assert.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

/**
 * 测试从资源目录加载图片和音频文件
 */
public class ResourceImageLoaderTest {

    /**
     * 测试从classpath读取图片文件
     */
    @Test
    public void testLoadImageFromResources() throws IOException {
        String imagePath = "/images/GoodTank1.png";
        URL imageUrl = getClass().getResource(imagePath);

        assertNotNull(imageUrl);

        BufferedImage image = ImageIO.read(imageUrl);
        assertNotNull(image);
        assertTrue(image.getWidth() > 0);
        assertTrue(image.getHeight() > 0);
    }

    /**
     * 测试从classpath读取音频文件
     */
    @Test
    public void testLoadAudioFromResources() throws IOException {
        String audioPath = "/audio/bgm.wav";
        URL audioUrl = getClass().getResource(audioPath);

        assertNotNull(audioUrl);

        InputStream inputStream = audioUrl.openStream();
        assertNotNull(inputStream);

        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        assertTrue(bytesRead > 0);

        inputStream.close();
    }
}
