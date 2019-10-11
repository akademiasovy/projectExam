package general;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class Config {

    private static Config instance = null;

    public static Config getInstance() {
        if (instance == null) instance = new Config();
        return instance;
    }

    private Map<String, Object> settings;

    private Config() {
        this.settings = new HashMap<String, Object>();
        this.load();
    }

    private void load() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(Utils.readResourceAsString("config"));

            for (Object object : root.keySet()) {
                if (!(object instanceof String)) continue;
                this.settings.put((String)object,root.get(object));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object get(String key) {
        return this.settings.get(key);
    }

    public Map<String, String> getHibernateConfig() {
        Map<String, String> config = new HashMap<String, String>();
        for (String s : this.settings.keySet()) {
            if (s.startsWith("hibernate_")) {
                if (!(this.settings.get(s) instanceof String)) continue;
                config.put(s.substring(10),(String)this.settings.get(s));
            }
        }
        return config;
    }

}
