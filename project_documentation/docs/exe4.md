```markdown
# Exercicio 4 – Serviço de Autenticação e Integração JWT no API Gateway

**Objetivo**  
Implementar um microserviço de autenticação (p-auth-service) que emita e valide JWTs, e configurar o API Gateway (gateway-service) para proteger os demais endpoints usando esses tokens.

---

## 1. Visão Geral

1. **p-auth-service**  
   - Spring Boot + Spring Security  
   - Emissão de JWT no endpoint `/auth/login`  
   - Persistência em H2 (dev) / PostgreSQL (prod)  
   - Endpoints:
     - `POST /auth/register` – cria novo usuário  
     - `POST /auth/login` – autentica e retorna `{ accessToken, username }`  
2. **gateway-service**  
   - Spring Cloud Gateway  
   - Roteia e protege chamadas para `/account/**`, `/product/**`, etc.  
   - Extrai e valida JWT de `Authorization: Bearer <token>`  
   - Forwarding das requisições autenticadas aos microserviços de negócio  

---

## 2. Tecnologias e Dependências

- **Spring Boot 3.x**  
- **Spring Security**  
- **jjwt** (io.jsonwebtoken) para geração/validação de tokens  
- **Spring Data JPA** + **H2** (dev) / **PostgreSQL** (prod)  
- **Spring Cloud Gateway**  
- **Lombok**, **MapStruct** (opcionais)  

---

## 3. Estrutura do p-auth-service

```

p-auth-service/
├── src/main/java/.../auth/
│   ├── controller/AuthController.java
│   ├── dto/LoginRequest.java
│   ├── dto/LoginResponse.java
│   ├── dto/RegisterRequest.java
│   ├── model/User.java
│   ├── repository/UserRepository.java
│   ├── security/JwtUtil.java
│   └── security/SecurityConfig.java
└── src/main/resources/application.yml

````

### 3.1 Modelo de Usuário

```java
@Entity
public class User {
  @Id @GeneratedValue
  private Long id;
  @Column(unique=true)
  private String username;
  private String password;                // BCrypt-hashed
  private String roles;                   // e.g. "ROLE_USER"
}
````

### 3.2 Repositório

```java
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
```

---

## 4. Fluxo de Autenticação (JWT)

1. **Registro (`/auth/register`)**

   * Recebe `{ username, password }`,
   * Hasheia senha com `BCryptPasswordEncoder`,
   * Persiste novo `User` no banco.

2. **Login (`/auth/login`)**

   * Recebe `{ username, password }`,
   * Autentica com `AuthenticationManager`,
   * Gera JWT:

     ```java
     String token = Jwts.builder()
         .setSubject(username)
         .claim("roles", roles)
         .setIssuedAt(now)
         .setExpiration(expiry)
         .signWith(secretKey)
         .compact();
     ```
   * Retorna `{ accessToken: token, username }`.

3. **Validação de JWT**

   * `JwtUtil.validateToken(token)` verifica assinatura e expiração,
   * Filtra cada requisição protegida em `JwtAuthenticationFilter` — injeta `UsernamePasswordAuthenticationToken` no `SecurityContext`.

---

## 5. Configuração de Segurança

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .authorizeRequests()
        .antMatchers("/auth/**").permitAll()
        .anyRequest().authenticated()
      .and()
      .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtUtil))
      .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtUtil));
  }
}
```

* **Permissão** para `/auth/register` e `/auth/login`.
* **Protegidos** todos os demais endpoints.

---

## 6. Integração no Gateway (gateway-service)

Em `application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth
          uri: lb://p-auth-service
          predicates:
            - Path=/auth/**

        - id: account
          uri: lb://account-service
          predicates:
            - Path=/account/**

        - id: product
          uri: lb://product-service
          predicates:
            - Path=/product/**

      default-filters:
        - name: RemoveRequestHeader
          args:
            headerName: Cookie
```

E um filtro global para JWT:

```java
@Component
public class JwtGlobalFilter implements GlobalFilter {
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !jwtUtil.validateToken(authHeader.substring(7))) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }
    return chain.filter(exchange);
  }
}
```

---

## 7. Testes e Verificação

* **Registro / Login** via `curl` ou Postman:

  ```bash
  curl -X POST http://<ELB>/auth/register \
    -H "Content-Type: application/json" \
    -d '{"username":"store","password":"store"}'

  curl -X POST http://<ELB>/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"store","password":"store"}'
  ```
* **Acesso Protegido**:

  ```bash
  TOKEN=$(curl …/auth/login … | jq -r .accessToken)
  curl http://<ELB>/account \
    -H "Authorization: Bearer $TOKEN"
  ```
* **Gateway** só deixa passar chamadas com JWT válido; retorna **401** caso contrário.

---

## 8. Conclusão

Com o **Exercício 4**, você:

* Criou um serviço de autenticação com emissão e validação de JWT.
* Protegeu os microserviços atrás do Gateway, garantindo que apenas requisições autenticadas sejam encaminhadas.
* Validou via testes manuais (curl/Postman) e integrou no ambiente Kubernetes (Deployment + Service + HPA).

Para detalhes de configuração de Kubernetes, consulte o [docs/k8s.md](/docs/k8s.md).

---

*Giovanny Russo – Junho 2025*

```

