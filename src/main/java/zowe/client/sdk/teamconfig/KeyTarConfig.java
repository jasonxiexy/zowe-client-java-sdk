package zowe.client.sdk.teamconfig;
import org.json.*;
import org.json.JSONObject;

public class KeyTarConfig {

    private Ikey key;

    public String configLocation;
    public String userName;
    public String passWord;

    public KeyTarConfig(Ikey key){
        this.key = key;
    }

    public void parseKey(String keyValue){
        JSONObject obj = new JSONObject(keyValue);
        configLocation = obj.toString();

        JSONObject profiles = obj.getJSONObject("profiles");

//        JSONObject zosmf = profiles.getJSONObject(String.valueOf(ProfileType.ZOSMF).toLowerCase());
//        String port = zosmf.getJSONObject("properties").getString("port");

        JSONObject base = profiles.getJSONObject(String.valueOf(ProfileType.BASE).toLowerCase());
        userName = base.getJSONObject("secure").getString("user");
        passWord = base.getJSONObject("secure").getString("password");
    }
}
