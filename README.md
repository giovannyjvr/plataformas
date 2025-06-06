# Exercise2 - Auth Service and Exercise3 - Order Service

## Exercise2: Auth Service

Endpoints:
- POST /api/auth/register
  - Body: { "username": "...", "password": "..." }
  - Response: { "token": "JWT_TOKEN" }

- POST /api/auth/login
  - Body: { "username": "...", "password": "..." }
  - Response: { "token": "JWT_TOKEN" }

Access H2 console at http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:authdb)

## Exercise3: Order Service

Endpoints (Require JWT in Authorization header):
- POST /api/orders
  - Header: Authorization: Bearer <JWT_TOKEN>
  - Body: { "product": "...", "quantity": number }
  - Response: Created Order object

- GET /api/orders
  - Header: Authorization: Bearer <JWT_TOKEN>
  - Response: List of Orders

Access H2 console at http://localhost:8081/h2-console (JDBC URL: jdbc:h2:mem:orderdb)

## Running

Each service is a separate Maven project.

- Auth Service (port 8080)
  ```
  cd exercise2-auth-service
  mvn spring-boot:run
  ```

- Order Service (port 8081)
  ```
  cd exercise3-order-service
  mvn spring-boot:run
  ```
