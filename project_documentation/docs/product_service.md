# Product Service

**Peso no checkpoint:** 10% (Microserviço CRUD)

O Product Service gerencia (criar, listar, buscar e deletar) produtos em memória, protegido por JWT emitido pelo Auth Service.

---

## Descrição Geral

- **Linguagem/Framework:** Java 17 + Spring Boot  
- **Ponto de entrada:**  
  `exercise2-product-service/src/main/java/com/insper/productservice/ProductServiceApplication.java`  
- **Persistência:** In-Memory (ConcurrentHashMap)  
- **Porta:** 8002 (configurado em `application.properties`)

---

## Tecnologias e Dependências

- Spring Boot Starter Web  
- JJWT (io.jsonwebtoken)  
- Spring Boot Starter Validation (opcional)  
- Maven (build e dependências)

Trecho relevante do `pom.xml`:
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
</dependencies>
```

---

## Configuração e Variáveis de Ambiente

Em `exercise2-product-service/src/main/resources/application.properties`:
```properties
server.port=8002

# Mesma SECRET_KEY usada no Auth Service
security.jwt.secret=MINHA_CHAVE_SECRETA_EXEMPLO_MUITO_FORTE
security.jwt.algorithm=HS256
```

> Se o Auth Service usar Base64 para a chave, ajuste `JwtUtil` para decodificar via Base64.

---

## Modelos de Dados

Em `com.insper.productservice.model.Product.java`:
```java
public class Product {
    private UUID id;
    private String name;
    private double price;
    private String unit;
    // Construtores, getters e setters
}
```

Em `com.insper.productservice.repository.ProductRepository.java`, usamos `ConcurrentHashMap<UUID, Product>`.

---

## Endpoints Disponíveis

Todas as rotas requerem:
```
Authorization: Bearer <token>
```

### POST /product

- **URL:** `http://localhost:8002/product`  
- **Headers:**  
  ```
  Content-Type: application/json
  Authorization: Bearer <token>
  ```  
- **Body (JSON):**
  ```json
  {
    "name": "Banana",
    "price": 2.5,
    "unit": "kg"
  }
  ```  
- **Respostas possíveis:**  
  - **201 Created**  
    ```json
    {
      "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      "name": "Banana",
      "price": 2.5,
      "unit": "kg"
    }
    ```  
  - **401 Unauthorized** (token inválido)  
  - **400 Bad Request** (dados inválidos)

### GET /product

- **URL:** `http://localhost:8002/product`  
- **Headers:**  
  ```
  Authorization: Bearer <token>
  ```  
- **Resposta (200 OK):**  
  ```json
  [
    {
      "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      "name": "Banana",
      "price": 2.5,
      "unit": "kg"
    }
  ]
  ```

### GET /product/{id}

- **URL:** `http://localhost:8002/product/{uuid}`  
- **Headers:**  
  ```
  Authorization: Bearer <token>
  ```  
- **Respostas possíveis:**  
  - **200 OK**  
    ```json
    {
      "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      "name": "Banana",
      "price": 2.5,
      "unit": "kg"
    }
    ```  
  - **404 Not Found**  
  - **401 Unauthorized**

### DELETE /product/{id}

- **URL:** `http://localhost:8002/product/{uuid}`  
- **Headers:**  
  ```
  Authorization: Bearer <token>
  ```  
- **Respostas possíveis:**  
  - **204 No Content**  
  - **404 Not Found**  
  - **401 Unauthorized**

---

## Exemplos de Uso

```bash
TOKEN=$(curl -s -X POST http://localhost:8001/login   -H "Content-Type: application/json"   -d '{"username":"fulano","password":"senha"}'   | jq -r .access_token)

# Criar produto
curl -X POST http://localhost:8002/product   -H "Authorization: Bearer $TOKEN"   -H "Content-Type: application/json"   -d '{"name":"Banana","price":2.5,"unit":"kg"}'

# Listar produtos
curl -X GET http://localhost:8002/product   -H "Authorization: Bearer $TOKEN"
```
