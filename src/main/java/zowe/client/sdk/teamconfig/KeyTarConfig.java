package zowe.client.sdk.teamconfig;

import com.starxg.keytar.KeytarException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class KeyTarConfig {

    private IKey key;
    private String configLocation;
    private String userName;
    private String password;

    public KeyTarConfig(IKey key) {
        this.key = key;
    }

    public void parseKey() throws KeytarException {
        key.processKey();
        JSONObject obj = new JSONObject(key.getKeyValue());

        // FRANK G - you need to perform obj.get on the field values from the JSON string of keyValue
        // and assign them to configLocation, userName, and password
        // the following JSON string is what the getKeyValue will return an example...
//        {
//            "C:\\Users\\fg892105\\IdeaProjects\\ZoweCCSSVCSymptomsReport\\zowe.config.json": {
//            "profiles.base.properties.user": "fg892105",
//            "profiles.base.properties.password": "javasdk1"
//            }
//        }
        // "C:\\Users\\fg892105\\IdeaProjects\\ZoweCCSSVCSymptomsReport\\zowe.config.json" is tricky as it is not
        // a static key this can be different value.. so think how to get this value.. 
        // parse the JSON String above and populate configLocation, userName, and password

        Iterator x = obj.keys();
        while (x.hasNext()){
            String key = (String) x.next();
            if (key.contains("zowe.config.json")){
                this.configLocation = key;
            }
        }

        this.userName = obj.getString("profiles.base.properties.user");
        this.password = obj.getString("profiles.base.properties.password");
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}
