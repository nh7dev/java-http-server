package dev.nh7.javahttpserver.example;

import dev.nh7.javahttpserver.Server;
import dev.nh7.javahttpserver.Utils;

public class ExampleServerApplication {

    public static void main(String[] args) {
        Utils.log("starting");

        int port = 1111;

        try {
            ExampleServerController controller = new ExampleServerController();
            Server server = new Server(port);
            server.addController(controller);
            server.addHeader("Access-Control-Allow-Origin", "*"); //enable cors
            //server.setHttps(SSLContext.getDefault());
            //server.setRequestLimit(new RequestLimitConfiguration(10, 60000, 50)); 10 requests per 60 seconds per ip
            //server.setDebug(false);
            server.start();
            Utils.log("example server started: http://localhost:" + port);
        } catch (Exception e) {
            Utils.log("error while starting server: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
