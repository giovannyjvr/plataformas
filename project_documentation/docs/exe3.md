````markdown
# Exercício 3 – Exchange Service

**Objetivo:** Implementar um microserviço em Python que faça conversão de valores entre duas moedas, consultando uma API de câmbio pública.

---

## 1. Stack e Ferramentas

- **Linguagem & Framework**  
  - Python 3.x  
  - [FastAPI](https://fastapi.tiangolo.com/) para construção da API  
  - Uvicorn como ASGI server  
- **HTTP Client**  
  - Biblioteca `requests` para chamada a API externa de câmbio  
- **Contêiner**  
  - Docker + Dockerfile customizado  
- **Orquestração**  
  - Kubernetes Deployment & Service  
- **Testes de carga**  
  - k6 para gerar carga e validar thresholds (`p(95)<500ms`)

---

## 2. Endpoints

### 2.1. `GET /exchange-rate`

- **Query Parameters**  
  - `from` – sigla da moeda de origem (e.g. `USD`)  
  - `to`   – sigla da moeda de destino (e.g. `BRL`)  
- **Resposta de Sucesso**  
  ```json
  {
    "from": "USD",
    "to": "BRL",
    "rate": 5.24
  }
````

* **Erros**

  * 400 se parâmetros faltando
  * 502 se falha ao chamar API externa

### 2.2. `POST /exchange`

* **Body** (JSON)

  ```json
  {
    "from": "USD",
    "to": "BRL",
    "quantity": 100
  }
  ```
* **Resposta de Sucesso**

  ```json
  {
    "from": "USD",
    "to": "BRL",
    "rate": 5.24,
    "quantity": 100,
    "result": 524.0
  }
  ```
* **Erros**

  * 400 se body mal-formado
  * 502 se falha na API externa

---

## 3. Lógica de Implementação

1. **Validação de entrada**

   * Confirma existência de todos os campos necessários.
2. **Chamada à API**

   * Usa `requests.get("https://api.exchangerate.host/latest", params={…})`.
   * Extrai a taxa de câmbio desejada do JSON retornado.
3. **Cálculo**

   * Multiplica `quantity × rate` e inclui no payload de saída.
4. **Tratamento de erros**

   * Converte timeouts ou status !=200 em HTTP 502 para o cliente.
   * Validações incorretas retornam HTTP 400.

---

## 4. Docker

* **Base image:** `python:3.9-slim`
* **Passos principais no `Dockerfile`:**

  1. Copia `requirements.txt` e instala dependências.
  2. Copia código fonte (`exchange.py`, `main.py`, etc.).
  3. Expõe porta `8000` e configura `CMD ["uvicorn", "main:app", "--host", "0.0.0.0"]`.

---

## 5. Kubernetes

* **Deployment** (`k8s/exchange-deployment.yaml`):

  * ReplicaSet de **2 réplicas**
  * `resources.requests`/`limits` para CPU e memória
* **Service** (`k8s/exchange-service.yaml`):

  * `type: LoadBalancer`
  * Roteia porta `80` para `podPort: 8000`

---

## 6. Testes de Carga

* **Script k6** (`k6/loadtest-exchange.js`):

  ```js
  import http from 'k6/http';
  import { check, sleep } from 'k6';

  export const options = {
    vus: 20,
    duration: '30s',
    thresholds: { http_req_duration: ['p(95)<500'] },
  };

  const BASE = 'http://<EXTERNAL-IP>';

  export default function () {
    const res = http.get(`${BASE}/exchange-rate?from=USD&to=BRL`);
    check(res, {
      'rate 200': (r) => r.status === 200,
      'rate body': (r) => r.json('rate') > 0,
    });

    const r2 = http.post(
      `${BASE}/exchange`,
      JSON.stringify({ from: 'USD', to: 'BRL', quantity: 10 }),
      { headers: { 'Content-Type': 'application/json' } }
    );
    check(r2, {
      'exchange 200': (r) => r.status === 200,
      'exchange result': (r) => r.json('result') > 0,
    });

    sleep(1);
  }
  ```
* **Execução**:

  ```bash
  k6 run k6/loadtest-exchange.js
  ```
* **Resultado esperado**:

  * 95% das requisições < 500 ms
  * 0% de erros após endpoints subirem

---

> **Próximo:**
>
> * Verifique o `docs/exercises.md` e adicione o link para este `docs/exe3.md`.
> * Passe para o Exercise 4 em `docs/exe4.md`.

````

**Como usar**  
1. Salve esse arquivo como `docs/exe3.md`.  
2. No seu `docs/exercises.md`, crie uma seção:

   ```markdown
   - **Exchange Service (Exercício 3):** detalhes de endpoints, Docker, K8s e testes de carga.  [📝 ver exe3.md](exe3.md)
````

