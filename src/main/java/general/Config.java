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
    private Map<String, String> hibernateSettings;

    private Config() {
        this.settings = new HashMap<String, Object>();
        this.hibernateSettings = new HashMap<String, String>();
        this.load();
    }

    private void load() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(Utils.readResourceAsString("config"));

            for (Object object : root.keySet()) {
                if (!(object instanceof String) || "hibernate".equals(object)) continue;
                this.settings.put((String)object,root.get(object));
            }

            JSONObject hibernateRoot = (JSONObject) root.get("hibernate");
            if (hibernateRoot != null) {
                for (Object object : hibernateRoot.keySet()) {
                    if (!(object instanceof String)) continue;
                    this.hibernateSettings.put((String)object,String.valueOf(hibernateRoot.get(object)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object get(String key) {
        return this.settings.get(key);
    }

    public Map<String, String> getHibernateConfig() {
        return this.hibernateSettings;
    }

}
