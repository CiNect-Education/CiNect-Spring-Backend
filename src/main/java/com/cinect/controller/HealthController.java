package com.cinect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, Object>> check() {
        String dbStatus = "UP";
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute("SELECT 1");
        } catch (Exception e) {
            dbStatus = "DOWN";
        }

        String status = "UP".equals(dbStatus) ? "UP" : "DOWN";

        Map<String, Object> components = new LinkedHashMap<>();
        components.put("database", Map.of("status", dbStatus));
        components.put("application", Map.of("status", "UP"));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", status);
        response.put("timestamp", Instant.now().toString());
        response.put("components", components);

        return ResponseEntity.ok(response);
    }
}
