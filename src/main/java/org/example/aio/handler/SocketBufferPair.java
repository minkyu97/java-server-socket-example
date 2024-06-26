package org.example.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public record SocketBufferPair(AsynchronousSocketChannel socket, ByteBuffer buffer) {
}
