package org.example.nio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class AbstractNioServer {
    protected static void handleRequest(SocketChannel socket, ByteBuffer buffer) {
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
