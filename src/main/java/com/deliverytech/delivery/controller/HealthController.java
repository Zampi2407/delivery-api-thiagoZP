package com.deliverytech.delivery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * HealthController - Endpoints de health e info usando Java 21 (Records).
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "service", "Delivery API",
            "javaVersion", System.getProperty("java.version")
        );
    }

    @GetMapping("/info")
    public AppInfo info() {
        // Exemplo simples usando record (Java 14+)
        return new AppInfo(
            "Delivery Tech API",
            "1.0.0",
            "Thiago",
            "JDK " + System.getProperty("java.version"),
            "Spring Boot 3.2.x"
        );
    }

    // Record para demonstrar recurso moderno do Java
    public record AppInfo(
        String application,
        String version,
        String developer,
        String javaVersion,
        String framework
    ) {}
}

