package config;

import lombok.Getter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Getter
public class BotConfig {
    private final String name;
    private final String token;
    public BotConfig(String config_file) throws FileNotFoundException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(config_file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        name = properties.getProperty("NAME");
        token = properties.getProperty("TOKEN");
    }
}
