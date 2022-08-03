package zowe.client.sdk.teamconfig;

import java.util.ArrayList;
import java.util.List;

public class Partition {

    private List<Profile> profiles;

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profileList) {
        this.profiles = profileList;
    }

}
