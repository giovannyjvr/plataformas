# Exercícios Individuais

Este documento reúne um panorama dos **5 Exercícios Individuais** desenvolvidos ao longo do curso de Plataforma.  
Para cada exercício você encontra uma descrição do que foi implementado e um link para o detalhamento completo em um arquivo dedicado.

| Exercício | Tópico                     | Descrição resumida                                                                                              | Detalhes         |
|-----------|----------------------------|-----------------------------------------------------------------------------------------------------------------|------------------|
| 1         | Account Service            | Microserviço de conta de usuário, com endpoints de registro e autenticação via JWT.                              | [exe1.md](exe1.md) |
| 2         | Product Service            | Microserviço de produtos, com CRUD completo de itens e persistência em banco em memória (H2).                    | [exe2.md](exe2.md) |
| 3         | Order Service              | Microserviço de pedidos, com processamento de compras, relacionamento com Account e Product.                     | [exe3.md](exe3.md) |
| 4         | Authentication Gateway     | API Gateway com roteamento para os demais serviços e validação de tokens JWT antes de encaminhar as requisições. | [exe4.md](exe4.md) |
| 5         | Exchange Service           | Serviço de câmbio, que converte preços entre moedas diferentes usando taxa externa simulada.                    | [exe5.md](exe5.md) |

---

Além disso, você pode consultar:

- **Manifests Kubernetes**: `k8s/*.yaml` — Deployments, Services e HPAs para cada serviço.  
- **Testes de carga**: Scripts K6 em `k6/*.js` e resultados do HPA escalando sob carga.  
- **Relatório de custos**: [costs.md](costs.md) — estimativa de custos mensais no AWS.  
