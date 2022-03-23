package WalleServer.controller;

import WalleServer.http.exceptions.BadRequestException;
import WalleServer.http.exceptions.DuplicateKeyException;
import WalleServer.http.exceptions.NotFoundException;
import WalleServer.http.HttpStatus;
import WalleServer.persistence.Database;
import WalleServer.persistence.Person;
import WalleServer.http.HttpMethod;
import WalleServer.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PersonController implements HttpHandler {

    private final Database database;

    public PersonController() {
        database = new Database();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        OutputStream os = exchange.getResponseBody();
        HttpResponse<?> response;
        switch (requestMethod) {
            case HttpMethod.GET -> {

                var splittedPath = exchange.getRequestURI().getPath()
                        .replaceFirst("/", "")
                        .split("/");

                if (splittedPath.length == 1) {
                    response = new HttpResponse<>(database.getAll(), HttpStatus.OK);
                } else if (splittedPath.length == 2) {

                    var curp = splittedPath[1];
                    var optionalPerson = database.get(curp);

                    if (optionalPerson.isPresent()) {
                        response = new HttpResponse<>(optionalPerson.get(), HttpStatus.OK);
                    } else {
                        var error = new NotFoundException("Record not found");
                        response = new HttpResponse<>(error, error.getStatus());
                    }

                } else {
                    var error = new BadRequestException("Bad request");
                    response = new HttpResponse<>(error, error.getStatus());
                }

            }
            case HttpMethod.POST -> {

                var requestBody = readRequestBody(exchange.getRequestBody());
                Person personCreated = null;

                try {
                    personCreated = database.create(Person.JSONToPerson(requestBody));
                } catch (DuplicateKeyException ex) {
                    sendResponse(new HttpResponse<>(ex, ex.getStatus()), exchange, os);
                }

                response = new HttpResponse<>(personCreated, HttpStatus.CREATED);

            }
            case HttpMethod.PUT -> {

                var requestBody = readRequestBody(exchange.getRequestBody());
                Person personUpdated = null;

                try {
                    personUpdated = database.update(Person.JSONToPerson(requestBody));
                } catch (NotFoundException ex) {
                    sendResponse(new HttpResponse<>(ex, ex.getStatus()), exchange, os);
                }

                response = new HttpResponse<>(personUpdated, HttpStatus.OK);

            }
            case HttpMethod.DELETE -> {

                var splittedPath = exchange.getRequestURI().getPath()
                        .replaceFirst("/", "")
                        .split("/");

                if (splittedPath.length == 2) {
                    var curp = splittedPath[1];
                    database.delete(curp);
                    response = new HttpResponse<>("", HttpStatus.NO_CONTENT);
                } else {
                    var error = new BadRequestException("Bad request");
                    response = new HttpResponse<>(error, error.getStatus());
                }

            }
            default -> {

                var error = new BadRequestException("Bad request");
                response = new HttpResponse<>(error, error.getStatus());

            }
        }
        sendResponse(response, exchange, os);
    }

    private void sendResponse(HttpResponse<?> response, HttpExchange exchange, OutputStream os) throws IOException {
        exchange.getResponseHeaders().add("Content-type", "application/json");
        exchange.sendResponseHeaders(response.getStatus(), response.toString().length());
        os.write(response.toString().getBytes());
        os.close();
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
