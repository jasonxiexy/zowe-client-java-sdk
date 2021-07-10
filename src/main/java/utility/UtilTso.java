/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import zostso.TsoConstants;
import zostso.zosmf.TsoMessage;
import zostso.zosmf.TsoMessages;
import zostso.zosmf.TsoPromptMessage;
import zostso.zosmf.ZosmfTsoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UtilTso {

    private static final Logger LOG = LogManager.getLogger(UtilTso.class);

    /*
    following json parsing is being constructed (currently partial) to conform to the following
    format: https://www.ibm.com/docs/en/zos/2.1.0?topic=services-tsoe-address-space
    */

    public static ZosmfTsoResponse parseJsonTsoResponse(JSONObject result) {
        Util.checkNullParameter(result == null, "No results for tso command.");

        ZosmfTsoResponse response = new ZosmfTsoResponse.Builder().queueId((String) result.get("queueID"))
                .ver((String) result.get("ver")).servletKey((String) result.get("servletKey"))
                .reused((boolean) result.get("reused")).timeout((boolean) result.get("timeout")).build();

        List<TsoMessages> tsoMessagesLst = new ArrayList<>();
        JSONArray tsoData = (JSONArray) result.get("tsoData");
        tsoData.forEach(item -> {
            JSONObject obj = (JSONObject) item;
            TsoMessages tsoMessages = new TsoMessages();
            parseJsonTsoMessage(tsoMessagesLst, obj, tsoMessages);
            parseJsonTsoPrompt(tsoMessagesLst, obj, tsoMessages);
        });
        response.setTsoData(tsoMessagesLst);

        return response;
    }

    public static boolean parseJsonTsoMessage(List<TsoMessages> tsoMessagesLst, JSONObject obj, TsoMessages tsoMessages) {
        Map tsoMessageMap = ((Map) obj.get(TsoConstants.TSO_MESSAGE));
        if (tsoMessageMap != null) {
            TsoMessage tsoMessage = new TsoMessage();
            tsoMessageMap.forEach((key, value) -> {
                if ("DATA".equals(key))
                    tsoMessage.setData(Optional.of((String) value));
                if ("VERSION".equals(key))
                    tsoMessage.setVersion(Optional.of((String) value));
            });
            tsoMessages.setTsoMessage(Optional.of(tsoMessage));
            tsoMessagesLst.add(tsoMessages);
            return true;
        }
        return false;
    }

    public static boolean parseJsonTsoPrompt(List<TsoMessages> tsoMessagesLst, JSONObject obj, TsoMessages tsoMessages) {
        Map tsoPromptMap = ((Map) obj.get(TsoConstants.TSO_PROMPT));
        if (tsoPromptMap != null) {
            TsoPromptMessage tsoPromptMessage = new TsoPromptMessage();
            tsoPromptMap.forEach((key, value) -> {
                if ("VERSION".equals(key))
                    tsoPromptMessage.setVersion(Optional.of((String) value));
                if ("HIDDEN".equals(key))
                    tsoPromptMessage.setHidden(Optional.of((String) value));
            });
            tsoMessages.setTsoPrompt(Optional.of(tsoPromptMessage));
            tsoMessagesLst.add(tsoMessages);
            return true;
        }
        return false;
    }

    public static ZosmfTsoResponse parseJsonStopResponse(JSONObject obj) {
        return new ZosmfTsoResponse.Builder().ver((String) obj.get("ver")).servletKey((String) obj.get("servletKey"))
                .reused((boolean) obj.get("reused")).timeout((boolean) obj.get("timeout")).build();
    }

    // TODO - parseJsonTsoResponse ?

}
