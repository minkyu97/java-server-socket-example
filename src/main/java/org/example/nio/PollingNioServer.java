package org.example.nio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PollingNioServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();

        serverSocket.bind(new InetSocketAddress(8080));
        serverSocket.configureBlocking(false);

        Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

        while (true) {
            SocketChannel socket = serverSocket.accept();

            if (socket != null) {
                socket.configureBlocking(false);

                sockets.put(socket, ByteBuffer.allocate(1024));
            }

            sockets.keySet().removeIf(sc -> !sc.isOpen());

            sockets.forEach((sc, buffer) -> {
                if (sc.isConnected()) {
                    handleRequest(sc, buffer);
                }
            });
        }
    }

    protected static void handleRequest(SocketChannel socket, ByteBuffer buffer) {
        try {
            int readBytes = socket.read(buffer);
            if (readBytes == -1) {
                System.out.println("Client disconnected: " + socket);
                socket.close();
            } else if (readBytes != 0) {
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

    protected static void toUpperCase(ByteBuffer buffer) {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) toUpperCase(buffer.get(i)));
        }
    }

    private static int toUpperCase(int data) {
        return Character.isLetter(data) ? Character.toUpperCase(data) : data;
    }
}
