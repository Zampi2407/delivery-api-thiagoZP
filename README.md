## 🚀 Delivery Tech API
Sistema de delivery backend desenvolvido com Spring Boot 3.2.x e Java 21 LTS, utilizando banco H2 em memória, ideal para aprendizado, testes e demonstrações. O projeto cobre desde o cadastro de clientes, restaurantes e produtos até pedidos, relatórios e validações completas.

## 🧩 Tecnologias Utilizadas
- ☕ Java 21 LTS
- 🌱 Spring Boot 3.2.x
- 🕸️ Spring Web
- 🗄️ Spring Data JPA
- 🧠 H2 Database (em memória)
- 🧩 Maven
- ♻️ Spring Boot DevTools (hot reload)
- 🔍 Lombok
- 🧪 Jackson (JSON parsing)
- 🧠 JPA Projections e DTOs

## ⚡ Recursos Modernos do Java 21
- ✅ Records (AppInfo)
- ✅ Text Blocks
- ✅ Pattern Matching
- ✅ Virtual Threads (pronto para uso em produção)

## 🏃‍♂️ Como Executar
Pré-requisitos
- JDK 21 instalado
- Maven configurado (ou usar wrapper mvnw incluso)

Clonar o repositório
git clone https://github.com/Zampi2407/delivery-api-thiagoZP.git
cd delivery-api-thiagoZP


Executar a aplicação
./mvnw spring-boot:run


Windows:

.\mvnw.cmd spring-boot:run



## 🌐 Acessar no navegador
- http://localhost:8080/health
- http://localhost:8080/info
- http://localhost:8080/h2-console

## 📦 Endpoints Disponíveis
|  |  |  | 
|  | /health |  | 
|  | /info |  | 
|  | /h2-console |  | 
|  | /clientes |  | 
|  | /clientes |  | 
|  | /restaurantes |  | 
|  | /restaurantes |  | 
|  | /produtos |  | 
|  | /produtos |  | 
|  | /produtos/restaurante/{id} |  | 
|  | /pedidos |  | 
|  | /pedidos/cliente/{id} |  | 
|  | /pedidos/{id}/{status} |  | 
|  | /pedidos/relatorio-vendas |  | 
|  | /pedidos/acima-de/{valor} |  | 
|  | /pedidos/por-periodo-e-status |  | 
|  | /pedidos/resumo-por-periodo |  | 
|  | /pedidos/produtos-mais-vendidos |  | 
|  | /pedidos/ranking-clientes |  | 



## 🧪 Validações Implementadas
- Cliente e restaurante devem estar ativos para criar pedidos
- Produto deve ter nome, descrição, preço > 0 e categoria
- Pedido não pode ser atualizado se já estiver entregue
- Conversão segura de String para List<Long> usando Jackson
- Relacionamentos JPA validados com integridade referencial

## 🛠️ Configuração
- Porta: 8080
- Banco: H2 em memória
- Profile: development
- DevTools: ativo para hot reload
- Logs: configurados para nível DEBUG com SQL e parâmetros visíveis

## 💻 HealthController.java
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



## 👨‍💻 Desenvolvedor
Thiago Zampieri
Curso: Análise e Desenvolvimento de Sistemas
Universidade São Judas Tadeu
Desenvolvido com JDK 21 e Spring Boot 3.2.x



