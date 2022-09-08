package dev.nh7.javahttpserver;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestLimitConfiguration {

    private final int requests;

    private final long time;

    private final int maxCacheSize;

    //requests per time (10 per 60000 -> 10 requests per minute)
    public RequestLimitConfiguration(int requests, long time, int maxCacheSize) {
        this.requests = requests;
        this.time = time;
        this.maxCacheSize = maxCacheSize;
    }

    private final HashMap<String, ArrayList<Long>> ipRequests = new HashMap<>();

    private void cleanCache() {
        if (ipRequests.size() > maxCacheSize) {
            Utils.log("cleaning cache");
            long now = System.currentTimeMillis();
            ipRequests.entrySet().removeIf(entry -> {
                int last = entry.getValue().size() - 1;
                return now - entry.getValue().get(last) > time;
            });
        }
    }

    public boolean checkRequestLimitForIP(String ip) {
        cleanCache();

        long now = System.currentTimeMillis();

        ArrayList<Long> requestTimestamps = ipRequests.get(ip);
        if (requestTimestamps == null) {
            requestTimestamps = new ArrayList<>();
            requestTimestamps.add(now);
            ipRequests.put(ip, requestTimestamps);
            return true;
        }

        requestTimestamps.removeIf(timestamp -> now - timestamp > time);

        if (requestTimestamps.size() > requests) {
            return false;
        }

        requestTimestamps.add(now);
        return true;
    }

}
