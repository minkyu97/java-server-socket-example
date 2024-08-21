package org.example.nio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MultiplexingNioServer {
    private static final Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();

        serverSocket.bind(new InetSocketAddress(8080));
        serverSocket.configureBlocking(false);

        try (Selector selector = Selector.open()) {

            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                for (Iterator<SelectionKey> it = selectionKeys.iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();

                    handleEvent(key);

                    it.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleEvent(SelectionKey key) {
        try {
            if (key.isValid()) {
                if (key.isAcceptable()) {
                    handleAcceptEvent(key);
                }
                if (key.isReadable()) {
                    handleReadEvent(key);
                }
                if (key.isWritable()) {
                    handleWriteEvent(key);
                }
            }
        } catch (CancelledKeyException e) {
            closeSocket((SocketChannel) key.channel());
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void handleAcceptEvent(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel socket = serverSocket.accept();

        socket.configureBlocking(false);

        socket.register(key.selector(), SelectionKey.OP_READ);

        sockets.put(socket, ByteBuffer.allocate(1024));
    }

    private static void handleReadEvent(SelectionKey key) throws IOException {
        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer buffer = sockets.get(socket);

        int data = socket.read(buffer);

        if (data == -1) {
            closeSocket(socket);
            return;
        }

        buffer.flip();

        toUpperCase(buffer);

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static void handleWriteEvent(SelectionKey key) throws IOException {
        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer buffer = sockets.get(socket);

        while (buffer.hasRemaining()) {
            socket.write(buffer);
        }

        buffer.compact();

        key.interestOps(SelectionKey.OP_READ);
    }

    private static void closeSocket(SocketChannel socket) {
        try {
            socket.close();
            sockets.remove(socket);
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
