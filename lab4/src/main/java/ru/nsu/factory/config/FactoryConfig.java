package ru.nsu.factory.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FactoryConfig {
    private final Properties props = new Properties();

    public FactoryConfig(String resourcePath) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) throw new IOException("Config not found: " + resourcePath);
            props.load(in);
        }
    }

    public int getInt(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    public boolean getBool(String key) {
        return Boolean.parseBoolean(props.getProperty(key));
    }
}
