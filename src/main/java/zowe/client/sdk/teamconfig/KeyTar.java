package zowe.client.sdk.teamconfig;

import com.starxg.keytar.Keytar;
import com.starxg.keytar.KeytarException;
import zowe.client.sdk.utility.Util;

import java.util.Base64;

public class KeyTar implements IKey {

    private String serviceName;
    private String accountName;
    private String keyString;

    public KeyTar(String serviceName, String accountName) throws KeytarException {
        this.serviceName = serviceName;
        this.accountName = accountName;
    }

    @Override
    public void processKey() throws KeytarException {
        Keytar instance = Keytar.getInstance();
        String encodedString = instance.getPassword(serviceName, accountName);
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        this.keyString = new String(decodedBytes);
    }

    @Override
    public String getKeyValue() {
        Util.checkNullParameter(keyString == null, "keyString is null");
        Util.checkIllegalParameter(keyString.isEmpty(), "keyString not specified");
        return keyString;
    }
}
