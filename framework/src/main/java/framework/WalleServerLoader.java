package framework;

import com.sun.net.httpserver.HttpServer;
import framework.core.ApplicationContext;
import framework.http.annotations.RestController;
import framework.http.handler.GenericHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class WalleServerLoader {

    public static void loadServer(List<Class<?>> controllers, Integer port, String contextPath)  {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            HttpServer server = HttpServer.create(inetSocketAddress, 0);
            for (Class<?> controller: controllers) {
                RestController annotation = controller.getAnnotation(RestController.class);
                String path = contextPath + annotation.value();
                server.createContext(path, new GenericHandler<>(ApplicationContext.getBean(controller)));
            }
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
