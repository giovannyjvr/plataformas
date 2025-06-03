# Docker e Kubernetes (Orquestração)

**Peso no checkpoint:** 15% (Orquestração)

Nesta seção, detalhamos como gerar imagens Docker para cada serviço e como aplicar manifestos Kubernetes em um cluster (Minikube ou EKS).

---

## Pré-requisitos

- **Docker** instalado  
- **kubectl** configurado (para Minikube ou EKS)  
- **Minikube** (ou cluster EKS ativo)  
- (Opcional) **Helm**, se for usar charts

---

## 1. Criar Imagens Docker

### Auth Service

1. Acesse `exercise2-auth-service/`.  
2. Compile e empacote:
   ```bash
   cd exercise2-auth-service
   mvn clean package -DskipTests
   ```  
3. Gere a imagem:
   ```bash
   docker build -t meu-usuario/exercise2-auth-service:latest .
   ```  
4. Teste local:
   ```bash
   docker run -d -p 8001:8001      -e "security.jwt.secret=MINHA_CHAVE_SECRETA_EXEMPLO_MUITO_FORTE"      meu-usuario/exercise2-auth-service:latest
   ```

### Product Service

1. `cd exercise2-product-service`  
2. `mvn clean package -DskipTests`  
3. `docker build -t meu-usuario/exercise2-product-service:latest .`  
4. `docker run -d -p 8002:8002      -e "security.jwt.secret=MINHA_CHAVE_SECRETA_EXEMPLO_MUITO_FORTE"      meu-usuario/exercise2-product-service:latest`

### Portal

1. `cd exercise2-portal`  
2. `mvn clean package -DskipTests`  
3. `docker build -t meu-usuario/exercise2-portal:latest .`  
4. `docker run -d -p 8003:8003      -e "portal.auth.url=http://localhost:8001"      -e "portal.product.url=http://localhost:8002"      meu-usuario/exercise2-portal:latest`

### Order Service

1. `cd exercise3-order-service`  
2. `mvn clean package -DskipTests`  
3. `docker build -t meu-usuario/exercise3-order-service:latest .`  
4. `docker run -d -p 8004:8004 meu-usuario/exercise3-order-service:latest`

---

## 2. Manifestos Kubernetes

No diretório `exercise5-k8s/`, existe o arquivo `k8s.yaml` contendo:

- **Secrets** (Auth e Product compartilham `SECRET_KEY`)  
- **ConfigMaps**, se houver variáveis de configuração não sensíveis  
- **Deployments** para cada serviço (Auth, Product, Portal, Order)  
- **Services** (ClusterIP ou LoadBalancer) para expor cada Deployment

### Exemplo de `k8s.yaml`

```yaml
# Secret Auth
apiVersion: v1
kind: Secret
metadata:
  name: auth-secret
type: Opaque
data:
  SECRET_KEY: TUlOSElDSEFURA...           # Base64 da chave secreta

---
# Deployment Auth
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: auth
  template:
    metadata:
      labels:
        app: auth
    spec:
      containers:
        - name: auth-container
          image: meu-usuario/exercise2-auth-service:latest
          ports:
            - containerPort: 8001
          env:
            - name: SECURITY_JWT_SECRET
              valueFrom:
                secretKeyRef:
                  name: auth-secret
                  key: SECRET_KEY

---
# Service Auth
apiVersion: v1
kind: Service
metadata:
  name: auth-service
spec:
  selector:
    app: auth
  ports:
    - port: 80
      targetPort: 8001
  type: ClusterIP

---
# Secret Product
apiVersion: v1
kind: Secret
metadata:
  name: product-secret
type: Opaque
data:
  SECRET_KEY: TUlOSElDSEFURA...           # Base64 idêntica ao Auth

---
# Deployment Product
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: product
  template:
    metadata:
      labels:
        app: product
    spec:
      containers:
        - name: product-container
          image: meu-usuario/exercise2-product-service:latest
          ports:
            - containerPort: 8002
          env:
            - name: SECURITY_JWT_SECRET
              valueFrom:
                secretKeyRef:
                  name: product-secret
                  key: SECRET_KEY

---
# Service Product
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  selector:
    app: product
  ports:
    - port: 80
      targetPort: 8002
  type: ClusterIP

---
# Deployment Portal
apiVersion: apps/v1
kind: Deployment
metadata:
  name: portal-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: portal
  template:
    metadata:
      labels:
        app: portal
    spec:
      containers:
        - name: portal-container
          image: meu-usuario/exercise2-portal:latest
          ports:
            - containerPort: 8003
          env:
            - name: PORTAL_AUTH_URL
              value: "http://auth-service"
            - name: PORTAL_PRODUCT_URL
              value: "http://product-service"

---
# Service Portal
apiVersion: v1
kind: Service
metadata:
  name: portal-service
spec:
  selector:
    app: portal
  ports:
    - port: 80
      targetPort: 8003
  type: ClusterIP

---
# Deployment Order
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order
  template:
    metadata:
      labels:
        app: order
    spec:
      containers:
        - name: order-container
          image: meu-usuario/exercise3-order-service:latest
          ports:
            - containerPort: 8004
          env:
            - name: PRODUCT_URL
              value: "http://product-service"
            - name: AUTH_URL
              value: "http://auth-service"

---
# Service Order
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  selector:
    app: order
  ports:
    - port: 80
      targetPort: 8004
  type: ClusterIP
```

---

## 3. FAQs

Crie um arquivo de perguntas frequentes para dúvidas recorrentes:

```markdown
# FAQ e Observações Adicionais

### Como alterar a porta do Auth Service?
Edite `exercise2-auth-service/src/main/resources/application.properties`:
```properties
server.port=9001
```
Recompile e replique a imagem Docker.

### Por que o Product Service retorna 401 com token válido?
Verifique se em `application.properties` do Product Service a chave `security.jwt.secret` é exatamente a mesma usada no Auth Service. Se houver diferença de encoding (Base64 vs. plain), ajuste o `JwtUtil` para decodificar corretamente.

### Como debugar erros no Portal?
- Verifique logs no console (executando com `mvn spring-boot:run`).
- Use ferramentas de inspeção do navegador para analisar requisições HTTP.
- Certifique-se de que as URLs em `application.properties` do Portal (`portal.auth.url` e `portal.product.url`) estão corretas.

### Como testar localmente o Deployment Kubernetes?
1. Verifique se o Minikube ou EKS está ativo:  
   ```bash
   kubectl cluster-info
   ```
2. Aplique os manifests:
   ```bash
   kubectl apply -f exercise5-k8s/k8s.yaml
   ```
3. Confira pods:
   ```bash
   kubectl get pods
   ```
4. Teste acesso (via port-forward):
   ```bash
   kubectl port-forward service/product-service 8080:80
   curl http://localhost:8080/product
   ```

### Como gerar e publicar a documentação MkDocs?
1. Instale MkDocs:
   ```bash
   pip install mkdocs mkdocs-material
   ```
2. Gere e visualize local:
   ```bash
   mkdocs serve
   ```
3. Gere versão estática:
   ```bash
   mkdocs build
   ```
4. Publique no GitHub Pages:
   ```bash
   mkdocs gh-deploy
   ```

---

Obrigado por acompanhar esta documentação!
