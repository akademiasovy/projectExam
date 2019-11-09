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
    private Map<String, Object> pbkdf2Settings;

    private Config() {
        this.settings = new HashMap<String, Object>();
        this.hibernateSettings = new HashMap<String, String>();
        this.pbkdf2Settings = new HashMap<String, Object>();
        this.load();
    }

    private void load() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(Utils.readResourceAsString("config"));

            for (Object key : root.keySet()) {
                if (!(key instanceof String) || "hibernate".equals(key) || "pbkdf2".equals(key)) continue;
                this.settings.put((String)key,root.get(key));
            }

            JSONObject hibernateRoot = (JSONObject) root.get("hibernate");
            if (hibernateRoot != null) {
                for (Object key : hibernateRoot.keySet()) {
                    if (!(key instanceof String)) continue;
                    this.hibernateSettings.put((String)key,String.valueOf(hibernateRoot.get(key)));
                }
            }

            JSONObject pbkdf2Root = (JSONObject) root.get("PBKDF2");
            if (pbkdf2Root != null) {
                for (Object key : pbkdf2Root.keySet()) {
                    if (!(key instanceof String)) continue;
                    this.pbkdf2Settings.put((String)key,String.valueOf(pbkdf2Root.get(key)));
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

    public Map<String, Object> getPBKDF2Config() {
        return this.pbkdf2Settings;
    }

}
