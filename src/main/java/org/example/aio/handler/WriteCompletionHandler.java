package org.example.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteCompletionHandler implements CompletionHandler<Integer, SocketBufferPair> {
    public static final WriteCompletionHandler instance = new WriteCompletionHandler();

    private WriteCompletionHandler() {
    }

    @Override
    public void completed(Integer result, SocketBufferPair pair) {
        AsynchronousSocketChannel socket = pair.socket();
        ByteBuffer buffer = pair.buffer();

        System.out.println("Wrote " + result + " bytes to " + socket + " on thread " + Thread.currentThread().getName());
        if (buffer.hasRemaining()) {
            socket.write(buffer, pair, this);
        } else {
            buffer.compact();
            socket.read(buffer, pair, ReadCompletionHandler.instance);
        }
    }

    @Override
    public void failed(Throwable exc, SocketBufferPair pair) {
        exc.printStackTrace();
    }
}
