package org.example.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadBlockingServer extends AbstractBlockingServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection from " + socket + " on thread " + Thread.currentThread().getName());

            new Thread(() -> handleRequest(socket)).start();
        }
    }
}
