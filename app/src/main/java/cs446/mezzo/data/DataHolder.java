package cs446.mezzo.data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author curtiskroetsch
 */
public final class DataHolder {

    private static Map<String, Object> sImpl = new HashMap<>();

    private DataHolder() {

    }

    public static void save(String key, Object data) {
        sImpl.put(key, data);
    }

    public static Object retrieve(String key) {
        return sImpl.get(key);
    }
}
