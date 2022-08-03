package zowe.client.sdk.teamconfig;

import com.starxg.keytar.KeytarException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import zowe.client.sdk.core.ZOSConnection;

import java.util.ArrayList;
import java.util.List;

public class TeamConfig {

    private String host;
    private String port;
    private String userName;
    private String passWord;

    private List<List> partitionList;
    private String configLocation;

    public TeamConfig(String configLocation){
        this.configLocation = configLocation;
    }

    public void parseJson(String configLocation) throws ParseException {

        partitionList = new ArrayList<List>();

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(configLocation);
        JSONObject embedded_profiles = object.getJSONObject("profiles");

        // reading through the entire json file
        for (int a=0; a<embedded_profiles.length(); a++){
            // go through the number of partitions in this file
            int partition_num = a+1;
            String partition = "lpar" + partition_num;
            if (embedded_profiles.has(partition)){
                JSONObject parObject = embedded_profiles.getJSONObject(partition);
                // for each partition, find the profiles inside it
                for (int b=0; b<parObject.length(); b++){
                    if (parObject.has("profiles")){
                        JSONArray proArray = parObject.getJSONArray("profiles");
                        List profileList = new ArrayList();
                        for (int c=0; c<proArray.length(); c++){
                            JSONObject profile = proArray.getJSONObject(c);
                            profileList.add(profile);
                        }
                        partitionList.add(profileList);
                    }
                }
            }
            else if (embedded_profiles.has("base")){
                JSONArray baseArray = embedded_profiles.getJSONArray("base");
                List baseList = new ArrayList();
                for (int d=0; d<baseArray.length(); d++){
                    JSONObject base = baseArray.getJSONObject(d);
                    baseList.add(base);
                }
                partitionList.add(baseList);
            }
        }
    }

    public void getDefaultTeamConfiguration(ProfileType type) throws KeytarException, ParseException {
        //String profileinfo = new KeyTar().processKey();

        KeyTar keyTar = new KeyTar("Zowe-Plugin", "secure_config_props");
        String keyValue = keyTar.getKeyValue();
        KeyTarConfig keyConfig = new KeyTarConfig(keyTar);
        keyConfig.parseKey(keyValue);

        //ConfigParser parser = new ConfigParser(keyConfig.configLocation);
        this.configLocation = keyConfig.configLocation;
        this.userName = keyConfig.userName;
        this.passWord = keyConfig.passWord;
        TeamConfig teamParser = new TeamConfig(configLocation);
        // the teamConfig object parse the JSON file and store each profile into this 2D arrayList
        teamParser.parseJson(configLocation);

        // the partitionList is a 2D arrayList, we need to traverse through each partition and each profiles
        // inside the partition to find the certain profile
        for (int n=0; n<partitionList.size(); n++){
            for (int m=0; m<partitionList.get(n).size(); m++){
                if (partitionList.get(n).get(m).equals(type)){
                    JSONObject tempProfile = new JSONObject(partitionList.get(n).get(m));
                    this.host = tempProfile.getString("host");
                    JSONObject tempProperties = tempProfile.getJSONObject("properties");
                    this.port = tempProperties.getString("port");
                }
            }
        }

        // if the specific profile is missing, use the info in the "base" file instead.
        int parLength = partitionList.size();
        if (port.equals("")){
            JSONObject tempProfile1 = new JSONObject(partitionList.get(parLength-1));
            this.port = tempProfile1.getJSONObject("properties").getJSONObject("port").getString("port");
        }
        if (host.equals("")){
            JSONObject tempProfile2 = new JSONObject(partitionList.get(parLength-1));
            this.host = tempProfile2.getJSONObject("host").getString("port");
        }

        ZOSConnection connection = new ZOSConnection(host, port, userName, passWord);
        connection.toString();
    }

}
