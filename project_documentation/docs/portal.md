# Portal

**Peso no checkpoint:** 15% (Portal)

O Portal é a interface web que consome o Auth Service e o Product Service, permitindo:
- Login
- Listar produtos
- Criar novo produto
- Excluir produto

---

## Descrição Geral

- **Linguagem/Framework:** Java 17 + Spring Boot + Thymeleaf  
- **Ponto de entrada:**  
  `exercise2-portal/src/main/java/com/insper/portal/PortalApplication.java`  
- **Porta:** 8003 (configurado em `application.properties`)

---

## Tecnologias e Dependências

- Spring Boot Starter Web  
- Spring Boot Starter Thymeleaf  
- RestTemplate (para chamadas HTTP)  
- Jackson Databind

Trecho relevante do `pom.xml`:
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
  </dependency>
  <dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
  </dependency>
</dependencies>
```

---

## Configuração e Variáveis de Ambiente

Em `exercise2-portal/src/main/resources/application.properties`:
```properties
server.port=8003

portal.auth.url=http://localhost:8001
portal.product.url=http://localhost:8002
```

> Ajuste as URLs caso Auth ou Product rodem em portas diferentes.

---

## Estrutura de Pastas

```
exercise2-portal/
└── src
    └── main
        ├── java
        │   └── com.insper.portal
        │       ├── PortalApplication.java
        │       ├── config
        │       │   └── RestTemplateConfig.java
        │       ├── controller
        │       │   └── PortalController.java
        │       └── model
        │           └── ProductDto.java
        └── resources
            ├── application.properties
            └── templates
                ├── login.html
                ├── products.html
                └── createProduct.html
```

---

## Funcionamento do Portal

1. **GET /login**  
   - Exibe formulário de login (`login.html`).

2. **POST /login**  
   - Envia `username` e `password` para `Auth Service (POST /login)`.  
   - Recebe token e armazena em sessão HTTP.  
   - Se bem-sucedido, redireciona para `/products`.

3. **GET /products**  
   - Lê token da sessão.  
   - Chama `GET /product` do Product Service com header `Authorization: Bearer <token>`.  
   - Exibe lista de produtos em `products.html`.

4. **GET /products/new**  
   - Exibe formulário para criar produto (`createProduct.html`).

5. **POST /products**  
   - Envia JSON com `name`, `price`, `unit` para `POST /product` (Product Service).  
   - Redireciona para `/products`.

6. **GET /products/delete/{id}**  
   - Chama `DELETE /product/{id}` do Product Service.  
   - Redireciona para `/products`.

---

## Exemplos de Uso

1. Abra o navegador em:  
   ```
   http://localhost:8003/login
   ```

2. Faça login e navegue para `/products`.  
3. Crie um novo produto e veja-o listado.  
4. Para excluir, clique em “Excluir” na tabela de `products`.

