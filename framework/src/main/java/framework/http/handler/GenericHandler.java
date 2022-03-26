package framework.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import framework.http.HttpMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class GenericHandler<T> implements HttpHandler {

    private final T controller;

    public GenericHandler(T controller) {
        this.controller = controller;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        OutputStream os = exchange.getResponseBody();
        switch (requestMethod) {
            case HttpMethod.GET -> {

            }
            case HttpMethod.POST -> {

            }
            case HttpMethod.DELETE -> {

            }
            default -> {

            }
        }
    }

    private void scanForAnnotationMethod(Class<?> annotation) {
        Method[] methods = controller.getClass().getDeclaredMethods();

    }

}
