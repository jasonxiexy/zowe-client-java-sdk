package zowe.client.sdk.teamconfig;

import com.starxg.keytar.KeytarException;

public interface Ikey {

    public void processKey() throws KeytarException;

    public String getKeyValue() throws KeytarException;

}
