package dev.nh7.javahttpserver.example;

import dev.nh7.javahttpserver.ServerController;
import dev.nh7.javahttpserver.ServerPath;
import dev.nh7.javahttpserver.ServerQueryParameter;
import dev.nh7.javahttpserver.ServerResponse;

import java.util.Map;

public class ExampleServerController extends ServerController {

    @ServerPath(path = "/")
    public ServerResponse getHome() {
        String text = "Open http://localhost:1111/hello?name=world";
        return new ServerResponse(200, text);
    }

    @ServerPath(path = "/hello")
    public ServerResponse getHello(
            @ServerQueryParameter(parameter = "name") String name
    ) {
        String text = "Hello " + name;
        Map<String, String> headers = Map.of(
                "Some-Header", "somevalue"
        );
        return new ServerResponse(200, text, headers);
    }

}
