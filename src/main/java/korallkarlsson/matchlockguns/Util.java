package korallkarlsson.matchlockguns;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Util {

    public static Map<String, Map<String, String>> parseNotation(List<String> lines) {
        Map<String, Map<String, String>> res = new HashMap<>();

        String itemName = null;

        for(String line : lines) {
            if(line.isBlank() || line.strip().startsWith("#"))
                continue;

            if(!line.startsWith("\t") && !line.startsWith(" ")) {
                itemName = line.strip();
                res.put(itemName, new HashMap<>());
            } else {
                String[] parts = line.split(":");
                if(parts.length == 2) {
                    String key = parts[0].strip();
                    String value = parts[1].strip();
                    if(itemName != null) {
                        res.get(itemName).put(key, value);
                    }
                }
            }
        }

        return res;
    }

    public static int getIntEntry(Map<String, String> map, String key, int defaultVal) {
        String val = map.get(key);
        if(val == null)
            return defaultVal;

        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static float getFloatEntry(Map<String, String> map, String key, float defaultVal) {
        String val = map.get(key);
        if(val == null)
            return defaultVal;

        try {
            return Float.parseFloat(val);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static boolean getBoolEntry(Map<String, String> map, String key, boolean defaultVal) {
        String val = map.get(key);
        if(val == null)
            return defaultVal;

        switch (val.toLowerCase()) {
            case "true", "t" -> {
                return true;
            }
            case "false", "f" -> {
                return false;
            }

            default -> {
                return defaultVal;
            }
        }
    }

    public static String getStringEntry(Map<String, String> map, String key, String defaultVal) {
        String val = map.get(key);
        if(val == null)
            return defaultVal;

        return val;
    }
}
