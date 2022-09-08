package dev.nh7.javahttpserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public abstract class ServerController {

    private final HashMap<String, Method> methods;

    public ServerController() {
        this.methods = loadMethods();
    }

    private HashMap<String, Method> loadMethods() {
        HashMap<String, Method> methods = new HashMap<>();
        for (Method method : getClass().getDeclaredMethods()) {

            ServerPath path = method.getDeclaredAnnotation(ServerPath.class);
            if (path == null) {
                continue;
            }

            if (method.getReturnType() != ServerResponse.class) {
                continue;
            }

            methods.put(path.path(), method);
        }
        return methods;
    }


    public ServerResponse getResponse(String path, HashMap<String, String> queryParameters) {
        Method method = methods.get(path);
        if (method == null) {
            return null;
        }

        Parameter[] methodParameters = method.getParameters();
        int methodParameterLength = methodParameters.length;

        Object[] methodParameterValues = new Object[methodParameterLength];
        for (int i = 0; i < methodParameterLength; i++) {
            Parameter methodParameter = methodParameters[i];

            ServerQueryParameter queryParameter = methodParameter.getAnnotation(ServerQueryParameter.class);
            if (queryParameter == null) {
                return new ServerResponse(500, "query parameter annotation missing");
            }

            String queryParameterValue = queryParameters.get(queryParameter.parameter());
            if (queryParameterValue == null) {
                return new ServerResponse(400, "query parameter '" + queryParameter.parameter() + "' missing");
            }

            try {
                methodParameterValues[i] = methodParameter.getType().getConstructor(String.class).newInstance(queryParameterValue);
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException |
                     InvocationTargetException e) {
                return new ServerResponse(400, "query parameter '" + queryParameter.parameter() + "' in wrong format");
            }

        }

        try {
            return (ServerResponse) method.invoke(this, methodParameterValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace(); //TODO CHECK
            return new ServerResponse(400, "illegal access");
        } catch (InvocationTargetException e) {
            e.printStackTrace(); //TODO CHECK
            return new ServerResponse(500, "invocation target");
        }
    }
}
