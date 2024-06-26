package org.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PollingNioServer extends AbstractNioServer {
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
}
