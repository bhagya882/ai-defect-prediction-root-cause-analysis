package com.bhagya.qa.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class DashboardServer {
    private HttpServer server;
    private int port;
    private Path root;

    public void start(Path projectRoot) throws IOException {
        root = projectRoot;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        port = server.getAddress().getPort();
        server.createContext("/", exchange -> {
            Path file = root.resolve("index.html").normalize();
            byte[] body = Files.readAllBytes(file);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    public void stop(){ if (server != null) server.stop(0); }
    public int getPort(){ return port; }
    public String getUrl(){ return "http://127.0.0.1:"+port+"/"; }
}
