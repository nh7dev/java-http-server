package dev.nh7.javahttpserver.example;

import dev.nh7.javahttpserver.Server;

public class ExampleServerApplication {

    public static void main(String[] args) {
        System.out.println("starting");

        try {
            ExampleServerController controller = new ExampleServerController();
            Server server = new Server(1111);
            server.addController(controller);
            //server.setHttps(SSLContext.getDefault());
            //server.setRequestLimit(new RequestLimitConfiguration(10, 60000, 50)); 10 requests per 60 seconds per ip
            //server.setCors("*");
            //server.setDebug(false);
            server.start();
            System.out.println("example server started: http://localhost:1111");
        } catch (Exception e) {
            System.out.println("error while starting server: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
