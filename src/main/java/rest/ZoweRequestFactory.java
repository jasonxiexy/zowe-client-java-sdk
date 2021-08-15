/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package rest;

import core.ZOSConnection;

public class ZoweRequestFactory {

    public static ZoweRequest buildRequest(ZOSConnection connection, String url, String body,
                                           ZoweRequestType.RequestType type) throws Exception {
        ZoweRequest request;
        switch (type) {
            case GET_JSON:
                request = new JsonGetRequest(connection, url);
                break;
            case PUT_JSON:
                request = new JsonPutRequest(connection, url, body);
                break;
            case POST_JSON:
                request = new JsonPostRequest(connection, url, body);
                break;
            case DELETE_JSON:
                request = new JsonDeleteRequest(connection, url);
                break;
            case GET_TEXT:
                request = new TextGetRequest(connection, url);
                break;
            case PUT_TEXT:
                request = new TextPutRequest(connection, url, body);
                break;
            case GET_STREAM:
                request = new StreamGetRequest(connection, url);
                break;
            default:
                throw new Exception("no valid type specified");
        }
        return request;
    }

}
