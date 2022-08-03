package zowe.client.sdk.teamconfig;

import com.starxg.keytar.Keytar;
import com.starxg.keytar.KeytarException;

import java.security.Key;
import java.util.Base64;

public class KeyTar implements Ikey{

    private String serviceName;
    private String accountName;
    private String keyString;

    public KeyTar(String serviceName, String accountName){
        this.serviceName = serviceName;
        this.accountName = accountName;
    }

    @Override
    public void processKey() throws KeytarException {
        Keytar instance = Keytar.getInstance();

        String encodedString = instance.getPassword("Zowe-Plugin", "secure_config_props");

        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        this.keyString = new String(decodedBytes);
    }

    @Override
    public String getKeyValue() throws KeytarException {
        if (keyString.isEmpty() || keyString.equals(null)){
            throw new KeytarException("Cannot find the global team config file");
        }
        return keyString;
    }
}
