package org.example.aio.handler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadCompletionHandler implements CompletionHandler<Integer, SocketBufferPair> {
    public static final ReadCompletionHandler instance = new ReadCompletionHandler();

    private ReadCompletionHandler() {
    }

    @Override
    public void completed(Integer result, SocketBufferPair pair) {
        AsynchronousSocketChannel socket = pair.socket();
        ByteBuffer buffer = pair.buffer();

        System.out.println("Read " + result + " bytes from " + socket + " on thread " + Thread.currentThread().getName());
        if (result == -1) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return;
        }

        if (result == 0) {
            socket.read(buffer, pair, this);
            return;
        }

        buffer.flip();

        toUpperCase(buffer);

        socket.write(buffer, pair, WriteCompletionHandler.instance);
    }

    @Override
    public void failed(Throwable exc, SocketBufferPair pair) {
        exc.printStackTrace();
    }

    private void toUpperCase(ByteBuffer buffer) {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) toUpperCase(buffer.get(i)));
        }
    }

    private int toUpperCase(int data) {
        return Character.isLetter(data) ? Character.toUpperCase(data) : data;
    }
}
