package org.tinygame.tankwar.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * 游戏配置类
 */
public final class GameConfig {
    public record Window(String title, int width, int height) {}
    public record GameLoop(int frameIntervalMs) {}
    public record PlayerTank(int initX, int initY, int speed, int fireIntervalMs) {}
    public record EnemyTank(int count, int startX, int spacingX, int startY, int spawnMaxY, int speed, int fireThreshold, int turnThreshold) {}
    public record Bullet(int speed) {}
    public record Boundary(int margin, int topOffset) {}
    public record Image(String playerTank, String enemyTank, String bullet, String enemyBullet, String explodePrefix, String explodeSuffix, int explodeCount) {}
    public record Audio(String fire, String explode, String move, String bgm) {}

    private static final String CONFIG_FILE = "game.properties";
    private static final Properties PROPS = loadProperties();

    public static final GameConfig CFG = new GameConfig();

    public final Window window = bind(Window.class);
    public final GameLoop gameLoop = bind(GameLoop.class);
    public final PlayerTank playerTank = bind(PlayerTank.class);
    public final EnemyTank enemyTank = bind(EnemyTank.class);
    public final Bullet bullet = bind(Bullet.class);
    public final Boundary boundary = bind(Boundary.class);
    public final Image image = bind(Image.class);
    public final Audio audio = bind(Audio.class);

    private GameConfig() {
    }

    public int playerFireIntervalMs() {
        int configured = Math.max(1, playerTank.fireIntervalMs());
        int enemyEquivalent = enemyFireIntervalMs();
        if (enemyEquivalent == Integer.MAX_VALUE) {
            return configured;
        }
        return Math.min(configured, enemyEquivalent);
    }

    public int enemyFireIntervalMs() {
        int fireChancePerHundredFrames = Math.max(0, Math.min(100, 99 - enemyTank.fireThreshold()));
        if (fireChancePerHundredFrames == 0) {
            return Integer.MAX_VALUE;
        }
        return Math.max(1, gameLoop.frameIntervalMs() * 100 / fireChancePerHundredFrames);
    }

    private static <T extends Record> T bind(Class<T> type) {
        return ConfigLoader.load(PROPS, type);
    }

    private static Properties loadProperties() {
        try (InputStream is = GameConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            Objects.requireNonNull(is, "Configuration file not found: " + CONFIG_FILE);
            var props = new Properties();
            props.load(is);
            return props;
        } catch (IOException _) {
            throw new ExceptionInInitializerError("Failed to load " + CONFIG_FILE);
        }
    }
}
