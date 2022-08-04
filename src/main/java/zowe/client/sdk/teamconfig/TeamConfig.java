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
    private String password;
    private List<Partition> partitionList;
    private String configLocation;

    public Profile getDefaultTeamConfiguration(ProfileType type) throws KeytarException, ParseException {
        KeyTar keyTar = new KeyTar("Zowe-Plugin", "secure_config_props");
        KeyTarConfig keyTarConfig = new KeyTarConfig(keyTar);
        keyTarConfig.parseKey();

        this.configLocation = keyTarConfig.getConfigLocation();
        this.userName = keyTarConfig.getUserName();
        this.password = keyTarConfig.getPassword();
        this.parseJson();

        // the partitionList is a 2D arrayList, we need to traverse through each partition and each profiles
        // inside the partition to find the certain profile
        for (int n = 0; n < partitionList.size(); n++) {
            for (int m = 0; m < partitionList.get(n).size(); m++) {
                if (partitionList.get(n).get(m).equals(type)) {
                    JSONObject tempProfile = new JSONObject(partitionList.get(n).get(m));
                    this.host = tempProfile.getString("host");
                    JSONObject tempProperties = tempProfile.getJSONObject("properties");
                    this.port = tempProperties.getString("port");
                }
            }
        }

        // if the specific profile is missing, use the info in the "base" file instead.
        int parLength = partitionList.size();
        if (port.equals("")) {
            JSONObject tempProfile1 = new JSONObject(partitionList.get(parLength - 1));
            this.port = tempProfile1.getJSONObject("properties").getJSONObject("port").getString("port");
        }
        if (host.equals("")) {
            JSONObject tempProfile2 = new JSONObject(partitionList.get(parLength - 1));
            this.host = tempProfile2.getJSONObject("host").getString("port");
        }

        // FRANK G - might be better to return profile object instead of connection
        // have the use crete the connection based on the information within profile returned
        // is better as the method naming same get default team configuration by profile type.
        ZOSConnection connection = new ZOSConnection(host, port, userName, password);
        connection.toString();

        return null;
    }

    private void parseJson() throws ParseException {

        // FRANK G - use this.configLocation to read the zowe.config.json from the disk..
        // write java code to read the file from configLocation which as an example will be a string value
        // of "C:\\Users\\fg892105\\IdeaProjects\\ZoweCCSSVCSymptomsReport\\zowe.config.json"
        // read the file as one string - this will be in JSON format - convert the String to a
        // JSONOBJECT for example:
        // String str = "{\"name\": \"Sam Smith\", \"technology\": \"Python\"}";
        // JSONObject json = new JSONObject(str);
        // or
        // JSONObject object = (JSONObject) parser.parse(str);


        partitionList = new ArrayList<>();

        JSONParser parser = new JSONParser();
        // FRANK G - using  parser.parse(this.configLocation); wont work see above..
        JSONObject object = (JSONObject) parser.parse(this.configLocation);
        JSONObject embedded_profiles = object.getJSONObject("profiles");

        // reading through the entire json file
        for (int a = 0; a < embedded_profiles.length(); a++) {
            // go through the number of partitions in this file
            int partition_num = a + 1;
            String partitionStr = "lpar" + partition_num;
            if (embedded_profiles.has(partitionStr)) {
                JSONObject parObject = embedded_profiles.getJSONObject(partitionStr);
                // for each partition, find the profiles inside it
                Partition partition = new Partition();
                for (int b = 0; b < parObject.length(); b++) {
                    if (parObject.has("profiles")) {
                        JSONArray proArray = parObject.getJSONArray("profiles");
                        List<Profile> profileList = new ArrayList();
                        for (int c = 0; c < proArray.length(); c++) {
                            JSONObject obj = proArray.getJSONObject(c);
                            Profile profile = new Profile();
                            // populate profile here
                            profileList.add(profile);
                        }
                        partition.setProfiles(profileList);
                        partitionList.add(partition);
                    }
                }
            } else if (embedded_profiles.has("base")) {
                JSONArray baseArray = embedded_profiles.getJSONArray("base");
                List baseList = new ArrayList();
                for (int d = 0; d < baseArray.length(); d++) {
                    JSONObject base = baseArray.getJSONObject(d);
                    baseList.add(base);
                }
                partitionList.add(baseList);
            }
        }
    }

}
