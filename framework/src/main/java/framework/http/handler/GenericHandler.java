package framework.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import framework.exception.NotImplementedHttpMethod;
import framework.http.HttpMethod;
import framework.http.HttpStatus;
import framework.http.annotations.DELETE;
import framework.http.annotations.GET;
import framework.http.annotations.POST;
import framework.http.annotations.PathParam;
import framework.http.annotations.RequestBody;
import framework.serializer.JsonSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenericHandler<T> implements HttpHandler {

    private final T controller;
    private static final String regexParam = "/\\{.*\\}";

    public GenericHandler(T controller) {
        this.controller = controller;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-type", "application/json");
        String requestMethod = exchange.getRequestMethod();
        OutputStream os = exchange.getResponseBody();
        String response = switch (requestMethod) {
            case HttpMethod.GET -> responseGET(exchange);
            case HttpMethod.POST -> responsePOST(exchange);
            case HttpMethod.DELETE -> responseDELETE(exchange);
            default -> throw new NotImplementedHttpMethod("Not implemented http method");
        };

        os.write(response.getBytes());
        os.close();

    }

    private String responseDELETE(HttpExchange exchange) throws IOException {
        List<Method> deleteMethods = getAnnotatedMethod(DELETE.class);
        if (!deleteMethods.isEmpty()) {
            String[] splittedPath = exchange.getRequestURI().getPath()
                    .replaceFirst("/", "")
                    .split("/");
            if (splittedPath.length == 2) {
                Method deleteMethod = deleteMethods.get(0);
                DELETE annotation = deleteMethod.getAnnotation(DELETE.class);
                String pathParamValue = splittedPath[1];
                boolean isValidPathParam = annotation.value().matches(regexParam);
                if (isValidPathParam) {
                    String pathParamName = annotation.value()
                            .replace("/{", "")
                            .replace("}", "");
                    Optional<Parameter> pathParamParameter = Arrays.stream(deleteMethod.getParameters())
                            .filter(parameter -> parameter.getAnnotation(PathParam.class).value().equals(pathParamName))
                            .findFirst();
                    if (pathParamParameter.isPresent()) {
                        try {
                            deleteMethod.invoke(controller, pathParamParameter.get().getType().cast(pathParamValue));
                            exchange.sendResponseHeaders(HttpStatus.OK, 0);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            exchange.sendResponseHeaders(HttpStatus.INTERNAL_SERVER_ERROR, 0);
                        }
                    }
                }
            } else {
                exchange.sendResponseHeaders(HttpStatus.BAD_REQUEST, 0);
            }
        } else {
            exchange.sendResponseHeaders(HttpStatus.NOT_IMPLEMENTED, 0);
        }
        return "";
    }

    private String responsePOST(HttpExchange exchange) throws IOException {
        String response = "";
        List<Method> postMethods = getAnnotatedMethod(POST.class);

        if (!postMethods.isEmpty()) {
            Method postMethod = postMethods.get(0);
            Optional<Parameter> requestBodyParameter = Arrays.stream(postMethod.getParameters())
                    .filter(parameter -> parameter.getAnnotation(RequestBody.class) != null)
                    .findFirst();
            if (requestBodyParameter.isPresent()) {
                String requestBodyJson = readRequestBody(exchange.getRequestBody());
                try {
                    Class<?> returnType = postMethod.getReturnType();
                    if (returnType == Void.class || returnType.getName().equals("void")) {
                        postMethod.invoke(controller, JsonSerializer.deserialize(requestBodyJson, requestBodyParameter.get().getType()));
                        exchange.sendResponseHeaders(HttpStatus.CREATED, 0);
                    } else {
                        Object invoke = postMethod.invoke(controller, JsonSerializer.deserialize(requestBodyJson, requestBodyParameter.get().getType()));
                        response = JsonSerializer.serialize(invoke);
                        exchange.sendResponseHeaders(HttpStatus.CREATED, response.length());
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException |NoSuchFieldException e) {
                    exchange.sendResponseHeaders(HttpStatus.BAD_REQUEST, 0);
                }
            }
        } else {
            exchange.sendResponseHeaders(HttpStatus.NOT_IMPLEMENTED, 0);
        }
        return response;
    }

    private String responseGET(HttpExchange exchange) throws IOException {
        String response = "";
        String[] splittedPath = exchange.getRequestURI().getPath()
                .replaceFirst("/", "")
                .split("/");
        List<Method> getMethods = getAnnotatedMethod(GET.class);

        if (getMethods.isEmpty()) {
            exchange.sendResponseHeaders(HttpStatus.NOT_IMPLEMENTED, 0);
        } else if (splittedPath.length == 1) {
            Optional<Method> getMethod = getMethods.stream()
                    .filter(method -> method.getAnnotation(GET.class).value().equals(""))
                    .findFirst();
            if (getMethod.isPresent()) {
                try {
                    List<?> elements = (List<?>) getMethod.get().getReturnType().cast(getMethod.get().invoke(controller));
                    response = JsonSerializer.serializeList(elements);
                    exchange.sendResponseHeaders(HttpStatus.OK, response.length());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    exchange.sendResponseHeaders(HttpStatus.INTERNAL_SERVER_ERROR, 0);
                }
            }
        } else if (splittedPath.length == 2) {
            String pathParamValue = splittedPath[1];
            Optional<Method> getMethod = getMethods.stream()
                    .filter(method -> method.getAnnotation(GET.class).value().matches(regexParam))
                    .findFirst();
            if (getMethod.isPresent()) {
                String pathParamName  = getMethod.get().getAnnotation(GET.class).value()
                        .replace("/{", "")
                        .replace("}", "");
                Optional<Parameter> pathParamParameter = Arrays.stream(getMethod.get().getParameters())
                        .filter(parameter -> parameter.getAnnotation(PathParam.class).value().equals(pathParamName))
                        .findFirst();
                if (pathParamParameter.isPresent()) {
                    try {
                        Object invoke = getMethod.get().invoke(controller, pathParamParameter.get().getType().cast(pathParamValue));
                        if (invoke == null) {
                            exchange.sendResponseHeaders(HttpStatus.NOT_FOUND, response.length());
                        }else {
                            response = JsonSerializer.serialize(getMethod.get().getReturnType().cast(invoke));
                            exchange.sendResponseHeaders(HttpStatus.OK, response.length());
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        exchange.sendResponseHeaders(HttpStatus.INTERNAL_SERVER_ERROR, 0);
                    }
                }
            } else {
                exchange.sendResponseHeaders(HttpStatus.NOT_IMPLEMENTED, 0);
            }
        } else {
            exchange.sendResponseHeaders(HttpStatus.BAD_REQUEST, 0);
        }
        return response;
    }

    private List<Method> getAnnotatedMethod(Class annotation) {
        return Arrays.stream(controller.getClass().getDeclaredMethods())
                .filter(method -> method.getAnnotation(annotation) != null)
                .collect(Collectors.toList());
    }

    private String readRequestBody(InputStream body) throws IOException {
        var inputStreamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
        var bufferedReader = new BufferedReader(inputStreamReader);
        var stringBuilder = new StringBuilder();
        int character;
        while ((character = bufferedReader.read()) != -1) {
            stringBuilder.append((char) character);
        }
        body.close();
        return stringBuilder.toString();
    }

}
