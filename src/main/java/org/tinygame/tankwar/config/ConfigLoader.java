package org.tinygame.tankwar.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Properties;
import java.util.regex.Pattern;

public final class ConfigLoader {

    private static final Pattern CAMEL_SPLIT = Pattern.compile("(?<=[a-z0-9])(?=[A-Z])");

    private ConfigLoader() {}

    public static <T extends Record> T load(Properties props, Class<T> recordClass) {
        String prefix = toKebab(recordClass.getSimpleName());
        RecordComponent[] components = recordClass.getRecordComponents();
        Class<?>[] types = new Class<?>[components.length];
        Object[] args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            types[i] = components[i].getType();
            String key = prefix + "." + toKebab(components[i].getName());
            String raw = props.getProperty(key);
            if (raw == null || raw.isBlank()) {
                throw new IllegalStateException("Missing required config key: " + key);
            }
            args[i] = convert(raw.trim(), types[i], key);
        }

        try {
            Constructor<T> ctor = recordClass.getDeclaredConstructor(types);
            return ctor.newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate " + recordClass.getSimpleName(), e);
        }
    }

    private static Object convert(String value, Class<?> type, String key) {
        try {
            return switch (type) {
                case Class<?> t when t == int.class || t == Integer.class -> Integer.parseInt(value);
                case Class<?> t when t == String.class -> value;
                case Class<?> t when t == boolean.class || t == Boolean.class -> Boolean.parseBoolean(value);
                case Class<?> t when t == double.class || t == Double.class -> Double.parseDouble(value);
                default -> throw new IllegalArgumentException("Unsupported config type: " + type);
            };
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid value for key '" + key + "': " + value, e);
        }
    }

    private static String toKebab(String camel) {
        return CAMEL_SPLIT.matcher(camel).replaceAll("-").toLowerCase();
    }
}