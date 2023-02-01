package com.aspanta.emcsec.tools;

public class JsonRpcHelper {

    public static String createRpcRequest(int id, String method, String... params) {
        return "{\"jsonrpc\":\"2.0\",\"id\": " + id + ",\"method\":\"" + method + "\",\"params\":[\"" + params[0] + "\"]}";
    }
}
