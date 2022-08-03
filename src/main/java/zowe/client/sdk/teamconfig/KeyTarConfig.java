package zowe.client.sdk.teamconfig;

import com.starxg.keytar.KeytarException;
import org.json.JSONObject;

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

        // you need to perform obj.get on the field values from the JSON string of keyValue
        // and assign them to configLocation, userName, and password

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
