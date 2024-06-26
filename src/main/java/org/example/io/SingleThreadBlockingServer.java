package org.example.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadBlockingServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection from " + socket + " on thread " + Thread.currentThread().getName());

            handleRequest(socket);
        }
    }

    private static void handleRequest(Socket socket) {
        System.out.println("Handling request from " + socket + " on thread " + Thread.currentThread().getName());
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {
            int data;

            while ((data = in.read()) != -1) {
                data = Character.isLetter(data) ? toUpperCase(data) : data;
                out.write(data);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        System.out.println("Complete request from " + socket + " on thread " + Thread.currentThread().getName());
    }

    private static int toUpperCase(int data) {
        return Character.toUpperCase(data);
    }
}
