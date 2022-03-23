package WalleServer;

import WalleServer.controller.PersonController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    private static final int PORT = 9000;
    private static final int BACKLOG = 0;

    public static void main(String[] args) throws IOException {
        var inetSocketAddress = new InetSocketAddress(PORT);
        var httpServer = HttpServer.create(inetSocketAddress, BACKLOG);

        httpServer.createContext("/persons", new PersonController());
        httpServer.setExecutor(null);
        httpServer.start();

    }
}
