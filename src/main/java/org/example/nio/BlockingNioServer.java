package org.example.nio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class BlockingNioServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();

        serverSocket.bind(new InetSocketAddress(8080));

        while (true) {
            SocketChannel socket = serverSocket.accept();

            handleRequest(socket);
        }
    }

    private static void handleRequest(SocketChannel socket) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            while (socket.read(buffer) != -1) {
                buffer.flip();

                toUpperCase(buffer);

                while (buffer.hasRemaining()) {
                    socket.write(buffer);
                }

                buffer.compact();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void toUpperCase(ByteBuffer buffer) {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) toUpperCase(buffer.get(i)));
        }
    }

    private static int toUpperCase(int data) {
        return Character.isLetter(data) ? Character.toUpperCase(data) : data;
    }
}
