# Projeto Microserviços – INSper

**Aluno:** Giovanny Russo

---

## Visão Geral

Neste projeto, foram desenvolvidos e orquestrados em Kubernetes (EKS) cinco microserviços que compõem uma aplicação de e-commerce:

* **account** — gerencia contas de usuário e autenticação
* **product** — catálogo de produtos
* **exchange** — serviço de processamento de pagamentos (pagamento simulado)
* **order** — registro e consulta de pedidos
* **gateway** — API Gateway unificado com autenticação JWT

Cada serviço foi empacotado em imagem Docker e publicado no Amazon ECR, implantado como Deployment no EKS e exposto via LoadBalancer.

---

## Infraestrutura e Orquestração

* **Kubernetes (EKS)**

  * Deployments e Services para cada microserviço
  * Recursos definidos (requests/limits de CPU e memória)
  * **Horizontal Pod Autoscaler** (HPA) configurado para `account` e `product`, escalando de 1 até 5 réplicas com base em CPU (50%)

* **Testes de Carga (k6)**

  * Scripts `loadtest-account.js` e `loadtest-product.js` para gerar alta carga e validar escalonamento automático
  * HPA observado escalonando réplicas durante a carga e desescalando ao término

---

## Simulação de Custos (mensal)

Total estimado: **177,85 US\$**

Detalhes em: [`Plano-de-custos.md`](Plano-de-custos.md)

---

## CI/CD e PASS

* **CI/CD**: Planejado pipeline Jenkins para build em Docker Hub e deploy automático no EKS
* **PASS**: Pontos de atenção esperados em disponibilidade e segurança

> *Obs.: CI/CD e PASS não implementados.*

---

## Documentação Complementar

* **Exercícios Individuais** (1–5): [`docs/exercises.md`](docs/exercises.md)
* **Detalhes Exe2**: [`docs/exe1.md`](docs/exe1.md)
* **Detalhes Exe2**: [`docs/exe2.md`](docs/exe2.md)
* **Detalhes Exe3**: [`docs/exe3.md`](docs/exe3.md)
* **Detalhes Exe4**: [`docs/exe4.md`](docs/exe4.md)
* **Detalhes Exe5**: [`docs/exe5.md`](docs/exe5.md)

---

*Giovanny Russo – Junho 2025*
