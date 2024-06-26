package org.example.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ThreadPoolBlockingServer extends AbstractBlockingServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        ExecutorService threadPool = java.util.concurrent.Executors.newFixedThreadPool(3);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection from " + socket + " on thread " + Thread.currentThread().getName());

            threadPool.execute(() -> handleRequest(socket));
        }
    }
}
