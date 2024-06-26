package org.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class BlockingNioServer extends AbstractNioServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();

        serverSocket.bind(new InetSocketAddress(8080));

        while (true) {
            SocketChannel socket = serverSocket.accept();
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            handleRequest(socket, buffer);
        }
    }
}
