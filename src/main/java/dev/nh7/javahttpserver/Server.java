package dev.nh7.javahttpserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.SSLContext;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class Server {

    private final int port;

    private ArrayList<ServerController> controllers = new ArrayList<>();

    private final HashMap<String, String> headers = new HashMap<>();

    private HttpsConfigurator httpsConfiguration;

    private RequestLimitConfiguration requestLimitConfiguration;

    private boolean debug = true;

    private HttpServer server;

    public Server(int port) {
        this.port = port;
    }

    public void setControllers(ArrayList<ServerController> controllers) {
        this.controllers = controllers;
    }

    public void addController(ServerController controller) {
        controllers.add(controller);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setHttps(SSLContext ssl) {
        this.httpsConfiguration = new HttpsConfigurator(ssl);
    }

    public void setRequestLimit(RequestLimitConfiguration requestLimitConfiguration) {
        this.requestLimitConfiguration = requestLimitConfiguration;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void start() throws Exception {
        if (httpsConfiguration == null) {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } else {
            server = HttpsServer.create(new InetSocketAddress(port), 0);
            ((HttpsServer) server).setHttpsConfigurator(httpsConfiguration);
        }

        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/", getHTTPHandler());

        server.start();
    }

    public void stop(int timeoutSeconds) {
        server.stop(timeoutSeconds);
    }

    private HttpHandler getHTTPHandler() {
        return exchange -> {
            try {

                String method = exchange.getRequestMethod();
                String ip = exchange.getRemoteAddress().getHostString();
                URI url = exchange.getRequestURI();
                String urlPath = url.getPath();
                String urlQuery = url.getQuery();

                if (debug) {
                    Utils.log("request " + method + " " + url + " from " + ip);
                }

                ServerResponse response;
                if (urlPath.endsWith("favicon.ico")) {
                    response = new ServerResponse(400, "no favicon");
                } else if (requestLimitConfiguration != null && !requestLimitConfiguration.checkRequestLimitForIP(ip)) {
                    response = new ServerResponse(400, "too many requests from your ip, try again in a minute");
                } else {
                    response = findServerResponse(urlPath, urlQuery);
                }

                int status = response.getStatus();
                byte[] messageBytes = response.getContent().getBytes();
                for (Map.Entry<String, String> header : headers.entrySet()) { //general headers
                    exchange.getResponseHeaders().add(header.getKey(), header.getValue());
                }
                for (Map.Entry<String, String> header : response.getHeaders().entrySet()) { //individual headers
                    exchange.getResponseHeaders().add(header.getKey(), header.getValue());
                }

                exchange.sendResponseHeaders(status, messageBytes.length);

                OutputStream out = exchange.getResponseBody();
                out.write(messageBytes);

                exchange.close();

                /*if (debug) {
                    Utils.log("response " + method + " " + url + " to " + ip);
                }*/

            } catch (Exception e) {
                Utils.log("an error occurred: " + e.getMessage());
                exchange.close();
            }
        };
    }

    private ServerResponse findServerResponse(String path, String query) {
        HashMap<String, String> queryParameters = getQueryParameters(query);
        for (ServerController controller : controllers) {
            ServerResponse response = controller.getResponse(path, queryParameters);
            if (response != null) {
                return response;
            }
        }
        return new ServerResponse(400, "path not found");
    }

    private HashMap<String, String> getQueryParameters(String query) {
        HashMap<String, String> queryParameters = new HashMap<>();

        if (query == null) {
            return queryParameters;
        }

        for (String queryParameter : query.split("&")) {
            String[] data = queryParameter.split("=");
            if (data.length != 2) {
                continue;
            }
            queryParameters.put(data[0], data[1]);
        }
        return queryParameters;
    }
}
