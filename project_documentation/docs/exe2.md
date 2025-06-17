```markdown
# Exercício 2 – Product Service

**Aluno:** Giovanny Russo  
**Status:** Completo até Orquestração e Testes de Carga

---

## 1. Objetivo

Implementar um microserviço de catálogo de produtos com:

- Endpoints CRUD (Create, Read, Update, Delete)  
- Banco em memória (H2)  
- Containerização Docker  
- Deploy em Kubernetes  
- Testes de carga com K6  
- Horizontal Pod Autoscaler (HPA)  

---

## 2. Estrutura do Código

```

product-service/
├── src/
│   ├── main/
│   │   ├── java/com/insper/product/
│   │   │   ├── model/Product.java
│   │   │   ├── repository/ProductRepository.java
│   │   │   └── controller/ProductController.java
│   │   └── resources/
│   │       └── application.properties
├── Dockerfile
└── pom.xml

````

---

## 3. Modelo e Persistência

- **`Product.java`**: entidade JPA com campos `id`, `name`, `description`, `price`.  
- **H2**: banco em memória configurado em `application.properties`:
  ```properties
  spring.h2.console.enabled=true
  spring.datasource.url=jdbc:h2:mem:productdb
````

* **`ProductRepository`**: estende `JpaRepository<Product, Long>` para CRUD automático.

---

## 4. API REST

No controlador `ProductController.java` foram implementados:

| Método HTTP | Endpoint        | Ação                        |
| ----------- | --------------- | --------------------------- |
| GET         | `/product`      | Listar todos os produtos    |
| GET         | `/product/{id}` | Buscar produto por ID       |
| POST        | `/product`      | Criar novo produto (201)    |
| PUT         | `/product/{id}` | Atualizar produto existente |
| DELETE      | `/product/{id}` | Remover produto (204)       |

Resposta com códigos HTTP adequados (200, 201, 404, 204).

---

## 5. Dockerização

**`Dockerfile`** multistage:

```dockerfile
# build
FROM maven:3.8.8-jdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# runtime
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/product-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

Gera imagem **plataforma-product-service\:latest**.

---

## 6. Deploy em Kubernetes

Arquivo `k8s/product.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product
  labels: { app: product }
spec:
  replicas: 2
  selector: { matchLabels: { app: product } }
  template:
    metadata: { labels: { app: product } }
    spec:
      containers:
        - name: product
          image: 767397705738.dkr.ecr.sa-east-1.amazonaws.com/plataforma-product-service:latest
          ports: [{ containerPort: 8080 }]
          resources:
            requests: { cpu: "100m", memory: "128Mi" }
            limits:   { cpu: "200m", memory: "256Mi" }
---
apiVersion: v1
kind: Service
metadata:
  name: product-service
  labels: { app: product }
spec:
  type: LoadBalancer
  selector: { app: product }
  ports: [{ port: 8080, targetPort: 8080 }]
```

* **Deployment**: 2 réplicas
* **Service**: LoadBalancer expondo porta 8080

---

## 7. Testes de Carga com K6

Script `k6/loadtest-product.js`:

```js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 20,
  duration: '30s',
  thresholds: { http_req_duration: ['p(95)<500'] },
};

const BASE = 'http://<SEU_ELB_PRODUCT>:8080/product';

export default function () {
  const res = http.get(BASE);
  check(res, {
    'status 200': r => r.status === 200,
    'body não vazio': r => r.body && r.body.length > 0,
  });
  sleep(1);
}
```

* Executado com `k6 run loadtest-product.js`
* Métricas de latência dentro de p(95)<500 ms

---

## 8. Autoscaling (HPA)

Criado HPA para Deployment `product`:

```bash
kubectl autoscale deployment product \
  --cpu-percent=50 \
  --min=1 --max=5

kubectl get hpa product
```

* HPA monitora CPU e escala de 1 até 5 réplicas conforme necessidade.

 