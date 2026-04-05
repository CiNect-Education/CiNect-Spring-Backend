package com.cinect.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(8082); // Mở port 8082 riêng cho Socket.IO
        config.setOrigin("*"); // Cho phép Frontend kết nối
        return new SocketIOServer(config);
    }
}