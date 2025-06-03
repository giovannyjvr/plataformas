# Order Service

**Peso no checkpoint:**  (Exercício 3)

O Order Service gerencia pedidos (orders) e possui endpoints para criar, listar, buscar e excluir pedidos.

---

## Descrição Geral

- **Linguagem/Framework:** Java 17 + Spring Boot  
- **Ponto de entrada:**  
  `exercise3-order-service/src/main/java/com/insper/orderservice/OrderServiceApplication.java`  
- **Persistência:** In-Memory ou banco configurado  
- **Porta:** 8004 (ou conforme `application.properties`)

---

## Tecnologias e Dependências

- Spring Boot Starter Web  
- Jackson Databind  
- Maven

Trecho relevante do `pom.xml`:
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
</dependencies>
```

---

## Endpoints Disponíveis

### POST /order

- **Descrição:** cria um novo pedido.  
- **URL:** `http://localhost:8004/order`  
- **Headers:**  
  ```
  Content-Type: application/json
  ```  
- **Body (JSON):**
  ```json
  {
    "productId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "quantity": 3,
    "userId": "fulano"
  }
  ```  
- **Respostas possíveis:**  
  - **201 Created** (pedido criado)  
  - **400 Bad Request** (dados inválidos)

### GET /order

- **URL:** `http://localhost:8004/order`  
- **Descrição:** lista todos os pedidos.  
- **Resposta (200 OK):** Lista de objetos JSON com todos os pedidos.

### GET /order/{id}

- **URL:** `http://localhost:8004/order/{id}`  
- **Descrição:** retorna um pedido específico.  
- **Respostas:**  
  - **200 OK** com JSON do pedido  
  - **404 Not Found** se não existir

### DELETE /order/{id}

- **URL:** `http://localhost:8004/order/{id}`  
- **Descrição:** exclui um pedido pelo ID.  
- **Respostas:**  
  - **204 No Content** (pedido excluído)  
  - **404 Not Found**  

---

## Exemplos de Uso

```bash
# Criar pedido
curl -X POST http://localhost:8004/order   -H "Content-Type: application/json"   -d '{"productId":"f47ac10b-58cc-4372-a567-0e02b2c3d479","quantity":3,"userId":"fulano"}'

# Listar pedidos
curl -X GET http://localhost:8004/order

# Buscar pedido por ID
curl -X GET http://localhost:8004/order/abc123-def456

# Excluir pedido
curl -X DELETE http://localhost:8004/order/abc123-def456
```
