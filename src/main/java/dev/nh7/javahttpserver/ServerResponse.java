package dev.nh7.javahttpserver;

import java.util.HashMap;
import java.util.Map;

public class ServerResponse {

    private final int status;

    private final String content;

    private final Map<String, String> headers;

    public ServerResponse(int status, String content, Map<String, String> headers) {
        this.status = status;
        this.content = content;
        this.headers = headers;
    }

    public ServerResponse(int status, String message) {
        this(status, message, new HashMap<>());
    }

    public int getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
