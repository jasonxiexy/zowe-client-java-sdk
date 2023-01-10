package zowe.client.sdk.teamconfig;

import com.starxg.keytar.KeytarException;

public interface IKey {

    void processKey() throws KeytarException;

    String getKeyValue() throws KeytarException;

}
