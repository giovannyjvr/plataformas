# Exercício 1 – Account Service

## Objetivo

O primeiro exercício consistiu em desenvolver o microserviço **Account Service**, responsável pelo gerenciamento de clientes (contas) de forma simples, expondo endpoints REST para criação e consulta de contas.

## Tecnologias e Ferramentas

* **Java 21** e **Spring Boot 3.x**
* **Spring Data JPA** para persistência
* **PostgreSQL** (em Kubernetes) como banco de dados relacional
* **Flyway** para migrações de esquema
* **Docker** para empacotamento da imagem
* **Kubernetes** (manifests) para deploy
* **HPA** para autoescalonamento baseado em CPU
* **k6** para testes de carga

## Estrutura do Projeto

```
account-service/
├── src/main/java/store/account/          # Código-fonte Java
│   ├── AccountApplication.java         # Classe principal
│   ├── controller/AccountController.java
│   ├── model/Account.java               
│   ├── repository/AccountRepository.java
│   ├── service/AccountService.java     
│   └── exception/ResourceNotFoundException.java
├── src/main/resources/
│   ├── application.yml                  # Configurações (DB, Flyway)
│   └── db/migration/V1__create_account_table.sql
├── Dockerfile                           # Empacotamento da aplicação
└── k8s/
    ├── account.yaml                     # Deployment e Service
    └── hpa-account.yaml                 # HorizontalPodAutoscaler
```

## Principais Componentes

### Model (Account)

Entidade `Account` com os campos:

* `id` (Long, chave primária)
* `name` (String)
* `email` (String)

### Repository

Interface `AccountRepository` estende `JpaRepository<Account, Long>`, fornecendo CRUD básico.

### Service

Classe `AccountService` encapsula lógica de negócio:

* `findAll()` retorna todas as contas
* `findById(id)` retorna conta por ID ou lança `ResourceNotFoundException`
* `create(Account)` persiste nova conta

### Controller

Classe `AccountController` expõe endpoints:

| Método | Endpoint        | Descrição             |
| ------ | --------------- | --------------------- |
| GET    | `/account`      | Lista todas as contas |
| GET    | `/account/{id}` | Consulta conta por ID |
| POST   | `/account`      | Cria nova conta       |

As requisições e respostas usam JSON.

## Migrações de Banco (Flyway)

O script `V1__create_account_table.sql` cria a tabela `account` com colunas (`id`, `name`, `email`). Flyway executa a migração automaticamente na inicialização.

## Docker

O `Dockerfile` baseia-se em `openjdk:21-slim`, copia o JAR gerado pelo Maven e define o comando de execução:

```dockerfile
FROM openjdk:21-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Kubernetes

* **Deployment** (`account.yaml`): define 1 réplica inicialmente, requests/limits de CPU/memória e variáveis de ambiente para conexão com PostgreSQL.
* **Service**: tipo `LoadBalancer`, expõe a porta 8080.
* **HPA** (`hpa-account.yaml`): escala entre 1 e 5 pods com meta de 50% de uso de CPU.

## Testes de Carga (k6)

Script `loadtest.js` com:

```js
import http from 'k6/http';
import { check, sleep } from 'k6';
export let options = { vus: 20, duration: '30s'};
export default function() {
  let res = http.get('http://<ELB_URL>:8080/account');
  check(res, { 'status 200': (r) => r.status === 200 });
  sleep(1);
}
```

Executando `k6 run loadtest.js` mostrou que o serviço responde dentro do SLA (p95 < 500ms) e a HPA escalou conforme esperado.

---

<sub>Este documento faz parte da documentação geral dos Exercícios Individuais. Para detalhes dos demais exercícios, veja `docs/exercises.md`.</sub>
