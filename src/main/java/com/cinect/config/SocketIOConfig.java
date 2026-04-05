package com.cinect.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        // Lắng nghe trên mọi IP (0.0.0.0)
        config.setHostname("0.0.0.0");
        config.setPort(8082); // Mở port 8082 riêng cho Socket.IO
        config.setOrigin(null); // Sửa thành null để Netty không chặn bất kỳ Origin nào (tránh lỗi CORS)
        config.setAllowCustomRequests(true); // Cực kỳ quan trọng để không bị chặn Upgrade lên WebSocket
        return new SocketIOServer(config);
    }
}