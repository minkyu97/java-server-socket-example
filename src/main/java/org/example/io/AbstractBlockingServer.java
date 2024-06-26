package org.example.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

public abstract class AbstractBlockingServer {
    protected static void handleRequest(Socket socket) {
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
