````markdown
# Exercicio 5 – Orquestração Kubernetes com HPA e Testes de Carga

**Objetivo**  
Orquestrar seus microserviços no Kubernetes (EKS), expondo-os via LoadBalancer, configurando **Horizontal Pod Autoscaler (HPA)** com base em métricas de CPU, e validando o escalonamento automático por meio de testes de carga (k6).

---

## 1. Pré-requisitos no Cluster

1. **metrics-server** instalado e funcionando no namespace `kube-system`  
   ```bash
   kubectl get deployment metrics-server -n kube-system
   # metrics-server   2/2     2            2           2d15h
````

2. Contexto apontando para seu cluster EKS.

---

## 2. Manifestos Kubernetes

### 2.1 account.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: account
  labels:
    app: account
spec:
  replicas: 1
  selector:
    matchLabels:
      app: account
  template:
    metadata:
      labels:
        app: account
    spec:
      containers:
      - name: account
        image: 767397705738.dkr.ecr.sa-east-1.amazonaws.com/plataforma-account-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: DATABASE_HOST
          value: postgres
        # ... outras variáveis de ambiente ...
        resources:
          requests:
            cpu: "50m"
            memory: "200Mi"
          limits:
            cpu: "200m"
            memory: "300Mi"
---
apiVersion: v1
kind: Service
metadata:
  name: account
  labels:
    app: account
spec:
  type: LoadBalancer
  selector:
    app: account
  ports:
  - port: 8080
    targetPort: 8080
```

### 2.2 product.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product
  labels:
    app: product
spec:
  replicas: 2
  selector:
    matchLabels:
      app: product
  template:
    metadata:
      labels:
        app: product
    spec:
      containers:
      - name: product
        image: 767397705738.dkr.ecr.sa-east-1.amazonaws.com/plataforma-product-service:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: "50m"
            memory: "200Mi"
          limits:
            cpu: "200m"
            memory: "300Mi"
---
apiVersion: v1
kind: Service
metadata:
  name: product-service
  labels:
    app: product
spec:
  type: LoadBalancer
  selector:
    app: product
  ports:
  - name: http
    port: 8080
    targetPort: 8080
```

---

## 3. Deploy e Exposição

```bash
kubectl apply -f account.yaml
kubectl apply -f product.yaml

# Verificar serviços e EXTERNAL-IP
kubectl get svc -l app=account,app=product
```

---

## 4. Configuração do HPA

Criar HPA apontando para uso de CPU em 50%, entre 1 e 5 réplicas:

```bash
kubectl autoscale deployment account \
  --cpu-percent=50 \
  --min=1 --max=5

kubectl autoscale deployment product \
  --cpu-percent=50 \
  --min=1 --max=5
```

Verificar status do HPA:

```bash
kubectl get hpa
# NAME      REFERENCE            TARGETS     MINPODS  MAXPODS  REPLICAS
# account   Deployment/account   cpu: 4%/50% 1        5        1
# product   Deployment/product   cpu: 2%/50% 1        5        1
```

---

## 5. Testes de Carga e Validação de Escalonamento

1. **Script k6** (`loadtest.js`), simulando 50 VUs por 1 minuto:

   ```js
   import http from 'k6/http'
   import { sleep } from 'k6'

   export const options = {
     vus: 50,
     duration: '1m',
   }

   const ACCOUNT = 'http://<ACCOUNT_ELB>:8080/account'
   export default () => {
     http.get(ACCOUNT)
     sleep(1)
   }
   ```

2. **Rodar o teste**:

   ```bash
   k6 run loadtest.js
   ```

3. **Observar HPA** em paralelo:

   ```bash
   kubectl get hpa account product -w
   ```

   * Durante a carga, o HPA escalou **account** de 1 até 5 réplicas conforme CPU ultrapassou 50%.
   * Após cessar a carga, a contagem de réplicas voltou a 1.

---

## 6. Conclusão

* Você possui **Deployments** e **Services** para `account` e `product`.
* Implementou **requests/limits** de CPU/Memória para cada container.
* Configurou **HPA** para escalonamento automático com base em métrica de CPU.
* Validou o escalonamento via **testes de carga** (k6).

> **Próximo passo:** documentar CI/CD (exercício 6) e entrega de vídeo de demonstração.

---

