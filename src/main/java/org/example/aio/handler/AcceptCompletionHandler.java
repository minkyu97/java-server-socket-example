package org.example.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {
    public static final AcceptCompletionHandler instance = new AcceptCompletionHandler();

    private AcceptCompletionHandler() {
    }

    @Override
    public void completed(AsynchronousSocketChannel socket, AsynchronousServerSocketChannel serverSocket) {
        System.out.println("Accepted connection from " + socket + " on thread " + Thread.currentThread().getName());
        serverSocket.accept(serverSocket, this);
        handleRequest(socket);
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel serverSocket) {
        exc.printStackTrace();
    }

    private void handleRequest(AsynchronousSocketChannel socket) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketBufferPair pair = new SocketBufferPair(socket, buffer);
        socket.read(buffer, pair, ReadCompletionHandler.instance);
    }
}
