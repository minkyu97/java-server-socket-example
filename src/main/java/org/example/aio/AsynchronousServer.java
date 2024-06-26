package org.example.aio;

import org.example.aio.handler.AcceptCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class AsynchronousServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open();

        serverSocket.bind(new InetSocketAddress(8080));
        serverSocket.accept(serverSocket, AcceptCompletionHandler.instance);

        Thread.currentThread().join();
    }
}
