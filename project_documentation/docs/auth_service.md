# Auth Service

**Peso no checkpoint:** 15% (Segurança)

O Auth Service é responsável por autenticar usuários e emitir tokens JWT (JSON Web Tokens).

---

## Descrição Geral

- **Linguagem/Framework:** Java 17 + Spring Boot  
- **Ponto de entrada:**  
  `exercise2-auth-service/src/main/java/com/insper/authservice/AuthServiceApplication.java`  
- **Responsabilidade:**  
  - Receber credenciais (`username` e `password`) via `POST /login`  
  - Validar credenciais (hard-coded ou em memória)  
  - Gerar JWT assinado com `HS256` e `SECRET_KEY`  
  - Retornar JSON com:  
    ```json
    {
      "access_token": "<token>",
      "token_type": "bearer"
    }
    ```

---

## Tecnologias e Dependências

- Spring Boot Starter Web  
- JJWT (io.jsonwebtoken)  
- Spring Security (se aplicável)  
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
</dependencies>
```

---

## Configuração e Variáveis de Ambiente

Em `exercise2-auth-service/src/main/resources/application.properties`:
```properties
server.port=8001

# Chave secreta (deve ser idêntica à do Product Service)
security.jwt.secret=MINHA_CHAVE_SECRETA_EXEMPLO_MUITO_FORTE

# Algoritmo usado (HS256)
security.jwt.algorithm=HS256

# Tempo de expiração do JWT (ms). Ex: 3600000 = 1 hora
security.jwt.expiration=3600000
```

---

## Endpoints Disponíveis

### POST /login

- **Descrição:** Valida usuário e senha, retorna JWT.  
- **URL:** `http://localhost:8001/login`  
- **Headers:**  
  ```
  Content-Type: application/json
  ```  
- **Body (JSON):**
  ```json
  {
    "username": "usuario",
    "password": "senha"
  }
  ```  
- **Respostas possíveis:**  
  - **200 OK**  
    ```json
    {
      "access_token": "eyJhbGciOiJIUzI1NiJ9…",
      "token_type": "bearer"
    }
    ```  
  - **401 Unauthorized**  
    ```json
    {
      "error": "Credenciais inválidas"
    }
    ```

---

## Exemplo de Uso

```bash
curl -X POST http://localhost:8001/login   -H "Content-Type: application/json"   -d '{"username":"fulano","password":"senha"}'
```
Retorna:
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9…",
  "token_type": "bearer"
}
```

---

## Boas Práticas

- Use HTTPS em produção.  
- Nunca exponha `SECRET_KEY` em repositórios públicos.  
- Para persistência real de usuários, substitua a validação estática por um banco ou serviço de identidade.  
- Adicione testes unitários (JUnit) para validar `/login` tanto para credenciais válidas quanto inválidas.  
