package schiraldi.gabriele.socket;

public class Utils {
    public static String keyString(String key, String value) {
        return key + ":" + value;
    }
    public static boolean wordInvalid(String word) {
        return word.isEmpty() && word.isBlank();
    }
}
