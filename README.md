# 🚀 Delivery Tech API

> Sistema de delivery backend desenvolvido com **Spring Boot 3.2.x** e **Java 21 LTS**, utilizando H2 Database em memória e recursos modernos do JDK 21, ideal para aprendizado, testes e demonstrações.

---

## 🧩 Tecnologias

- ☕ **Java 21 LTS**  
- 🌱 **Spring Boot 3.2.x**  
- 🕸️ **Spring Web**  
- 🗄️ **Spring Data JPA**  
- 🧠 **H2 Database**  
- 🧩 **Maven**  
- ♻️ **Spring Boot DevTools** (hot reload)

---

## ⚡ Recursos Modernos do Java 21

- ✅ **Records** (Java 14+)  
- ✅ **Text Blocks** (Java 15+)  
- ✅ **Pattern Matching** (Java 17+)  
- ✅ **Virtual Threads** (Java 21)

---

## 🏃‍♂️ Como Executar

1. **Pré-requisitos:**  
   - JDK 21 instalado  
   - Maven configurado (ou usar wrapper `mvnw` incluso)

2. **Clonar o repositório:**
```bash
git clone https://github.com/Zampi2407/delivery-api-thiagoZP.git
cd delivery-api-thiago


*Executar a aplicação:*

./mvnw spring-boot:run

(Windows: .\mvnw.cmd spring-boot:run)

Acessar no navegador:

http://localhost:8080/health
http://localhost:8080/info
http://localhost:8080/h2-console

**Endpoints Disponíveis**

| Método | Endpoint      | Descrição                                                           |
| ------ | ------------- | ------------------------------------------------------------------- |
| GET    | `/health`     | Retorna o status da aplicação, timestamp e versão do Java           |
| GET    | `/info`       | Retorna informações da aplicação, versão, desenvolvedor e framework |
| GET    | `/h2-console` | Acessa o console do banco de dados H2                               |

*Código do HealthController.java*

package com.example.deliveryapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

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
        return new AppInfo(
            "Delivery Tech API",
            "1.0.0",
            "Thiago Zampieri",
            "JDK " + System.getProperty("java.version"),
            "Spring Boot 3.2.x"
        );
    }

    public record AppInfo(
        String application,
        String version,
        String developer,
        String javaVersion,
        String framework
    ) {}
}


*Recursos Modernos Utilizados*

✅ Records (Java 14+)
✅ Text Blocks (Java 15+)
✅ Pattern Matching (Java 17+)
✅ Virtual Threads (Java 21)

*Desenvolvedor*

Thiago Zampieri
Curso: Análise e Desenvolvimento de Sistemas - Universidade São Judas Tadeu
Desenvolvido com *JDK 21* e Spring Boot 3.2.x