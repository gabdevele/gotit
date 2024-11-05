package schiraldi.gabriele.socket;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Strings {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("strings_en");

    public static String get(String key, Object... args) {
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, args);
    }
}