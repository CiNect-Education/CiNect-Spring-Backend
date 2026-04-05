package com.cinect.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
@RequiredArgsConstructor
public class SocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer server;

    @Override
    public void run(String... args) {
        server.start();
        System.out.println("Socket.IO Server started on port 8082");
    }

    @PreDestroy
    public void stop() {
        server.stop();
    }
}
