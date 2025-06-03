# Gargalos e Como Resolvi

## Gargalo 1 – Propagação e Validação de JWT

**Problema:**  
- Tínhamos três microserviços (Auth, Product e Order) e o Portal precisava chamar cada um deles.  
- Como manter a mesma `SECRET_KEY` e algoritmo (HS256) em todos, para que o token gerado pelo Auth fosse aceito pelo Product e pelo Order?  
- Em que momento validar? Se a chave divergir, devolvia 401.

**Solução:**  
1. Defini em `exercise2-auth-service/src/main/resources/application.properties`:
   ```properties
   security.jwt.secret=MINHA_CHAVE_SECRETA_EXEMPLO_MUITO_FORTE
   security.jwt.algorithm=HS256
   ```
2. Copiei exata mesma string em `exercise2-product-service/src/main/resources/application.properties` e em `exercise3-order-service/src/main/resources/application.properties`.  
3. Criei a classe `JwtUtil.java` em cada projeto (Auth, Product e Order) que decodifica e valida via `Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)`.  
4. Nos controllers (ProductController, OrderController), chamei:
   ```java
   private void validateToken(HttpServletRequest req) {
       String header = req.getHeader("Authorization");
       if (header == null || !header.startsWith("Bearer ")) {
           throw new RuntimeException("Token ausente ou mal formatado");
       }
       jwtUtil.validateToken(header.substring(7));
   }
   ```
5. No Portal, armazenei o token em sessão HTTP (`session.setAttribute("TOKEN", token)`) e para cada chamada a `restTemplate` do Product/Order enviei:
   ```java
   headers.setBearerAuth((String) session.getAttribute("TOKEN"));
   ```
6. Testei localmente (via `curl`) para confirmar que, se eu mudasse a chave no Auth, o Product retornava 401.  
7. Incluí capturas de tela em `product_service.md` e `order_service.md` mostrando “401 Unauthorized” sem token e “200 OK” com token.

## Gargalo 2 – Configurar Secret no Kubernetes para `SECRET_KEY`

**Problema:**  
- O manifest do Kubernetes não podia expor a `SECRET_KEY` em texto simples (precisava mascarar em Base64).  
- Cada Deployment precisava dessa variável para validar JWT.

**Solução:**  
1. Gere a chave em Base64:
   ```bash
   echo -n "MINHA_CHAVE_SECRETA_EXEMPLO_MUITO_FORTE" | base64
   # resultado: TUlOSEhBVEJBU0U2NEFCQ0RFRkdISUpd
   ```
2. Em `exercise5_k8s/k8s.yaml`, adicionei o bloco Secret:
   ```yaml
   apiVersion: v1
   kind: Secret
   metadata:
     name: auth-secret
   type: Opaque
   data:
     SECRET_KEY: TUlOSEhBVEJBU0U2NEFCQ0RFRkdISUpd
   ```
3. No Deployment do Auth Service:
   ```yaml
   env:
     - name: SECURITY_JWT_SECRET
       valueFrom:
         secretKeyRef:
           name: auth-secret
           key: SECRET_KEY
   ```
4. Repeti esse mesmo Secret (mesmo valor em Base64) para `product-secret` e `order-secret` dentro do mesmo arquivo `k8s.yaml`, de modo que Auth, Product e Order compartilhassem a mesma `SECRET_KEY`.  
5. Validei com:
   ```bash
   kubectl get secret auth-secret -o yaml
   kubectl describe pod auth-deployment-xxxxx
   ```
   → Vi que a variável de ambiente `SECURITY_JWT_SECRET` estava corretamente injetada no container.  
6. Documentei todo esse processo em `docker_k8s.md` (há prints do Minikube Dashboard mostrando pods “Running”).

