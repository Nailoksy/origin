package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    public static final String ADMIN_LOGIN = "adminLogin";
    public static final String ADMIN_PASSWORD = "adminPassword";

    private Config(){
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")){
            if (input == null) {throw new RuntimeException("config.properties not found in resources");
            }
            properties.load(input);
        }
        catch (IOException e){
            throw new RuntimeException("Fail to load config.properties", e);
        }
    }

    public static String getProperty (String key){
        return INSTANCE.properties.getProperty(key);
    }
}
