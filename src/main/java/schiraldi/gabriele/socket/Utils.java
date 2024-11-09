package schiraldi.gabriele.socket;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class Utils {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("strings", Locale.getDefault());

    public static Map<String, String> getServerConfig() {
        Properties properties = new Properties();
        try {
            properties.load(Utils.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento del file di configurazione");
        }
        Map<String, String> configMap = new HashMap<>();
        for (String key :properties.stringPropertyNames()) {
            configMap.put(key, properties.getProperty(key));
        }
        return configMap;
    }

    public static String getString(String key, Object... args) {
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, args);
    }

    public static String keyString(String key, String value) {
        return key + ":" + value;
    }
    public static boolean wordInvalid(String word) {
        return word.isEmpty() && word.isBlank();
    }
}

