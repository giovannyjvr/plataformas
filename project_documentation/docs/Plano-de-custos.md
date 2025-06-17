## 1. Premissas da simulação

- **Cluster EKS**: 1 cluster, 730 h/mês @ US$ 0,10/h  
- **Nós de trabalho**: 2 × t3.small Linux (2 vCPU, 2 GiB) on-demand, 730 h/mês  
- **RDS PostgreSQL**: 1 × db.t3.micro Single-AZ, 20 GiB gp2, 730 h/mês  
- **ALB (Load Balancer)**: 1 × ALB, 730 h/mês  
- **ALB LCU**: 1 LCU ativo em média, 730 h/mês  
- **NAT Gateway**: 1 × NAT GW, 730 h/mês + 10 GiB de processamento  
- **Transferência de dados de saída**: 50 GiB/mês  
- **Horizonte**: 1 mês (730 h)

---

## 2. Tabela de custos mensais estimados

| Serviço / Item                                      | Unidade              | Qtde           | Preço unitário     | Custo mensal (US$) |
|-----------------------------------------------------|----------------------|---------------:|-------------------:|-------------------:|
| **EKS – Cluster management**                        | US$ por cluster-hora | 1 × 730 h      | US$ 0,10/h         | 73,00              |
| **EC2 – Worker node t3.small**                      | US$ por inst-hora    | 2 × 730 h      | US$ 0,0208/h       | 30,37              |
| **RDS – db.t3.micro (Single-AZ)**                   | US$ por inst-hora    | 1 × 730 h      | US$ 0,017/h        | 12,41              |
| **RDS Storage (gp2)**                               | US$ por GiB-mês      | 20 GiB         | US$ 0,10/GiB-mês   | 2,00               |
| **ALB – Load Balancer**                             | US$ por bal-hora     | 1 × 730 h      | US$ 0,0225/h       | 16,43              |
| **ALB – LCU (Load Balancing Capacity Unit)**        | US$ por LCU-hora     | 1 × 730 h      | US$ 0,008/h        | 5,84               |
| **NAT Gateway**                                     | US$ por nat-hora     | 1 × 730 h      | US$ 0,045/h        | 32,85              |
| **NAT Data Processing**                             | US$ por GiB          | 10 GiB         | US$ 0,045/GiB      | 0,45               |
| **Transferência de dados (saída)**                  | US$ por GiB          | 50 GiB         | US$ 0,09/GiB       | 4,50               |
|                                                     |                      |                |                    |                    |
| **Total Mensal Estimado**                           |                      |                |                    | **177,85 US$**     |


## 4. Análise de Custos

### Premissas
- Cluster EKS rodando 24×7 (730 h/mês)  
- 2 nós t3.small (2 vCPU, 2 GiB) on-demand  
- RDS PostgreSQL db.t3.micro Single-AZ + 20 GiB gp2  
- Application Load Balancer (ALB) + 1 LCU médio  
- NAT Gateway para saída de tráfego (10 GiB)  
- 50 GiB de transferência de dados de saída  

_Tarifas conforme AWS Pricing Calculator (https://calculator.aws/) em junho/2025._

### Custos Mensais Estimados

| Serviço / Item                                      | Qtde           | Preço unitário     | Custo mensal (US$) |
|-----------------------------------------------------|---------------:|-------------------:|-------------------:|
| **EKS – Cluster management**                        | 1 × 730 h      | US$ 0,10/h         | 73,00              |
| **EC2 – Worker node t3.small**                      | 2 × 730 h      | US$ 0,0208/h       | 30,37              |
| **RDS – db.t3.micro (Single-AZ)**                   | 1 × 730 h      | US$ 0,017/h        | 12,41              |
| **RDS Storage (gp2)**                               | 20 GiB         | US$ 0,10/GiB-mês   | 2,00               |
| **ALB – Load Balancer**                             | 1 × 730 h      | US$ 0,0225/h       | 16,43              |
| **ALB – LCU (Load Balancing Capacity Unit)**        | 1 × 730 h      | US$ 0,008/h        | 5,84               |
| **NAT Gateway**                                     | 1 × 730 h      | US$ 0,045/h        | 32,85              |
| **NAT Data Processing**                             | 10 GiB         | US$ 0,045/GiB      | 0,45               |
| **Transferência de dados (saída)**                  | 50 GiB         | US$ 0,09/GiB       | 4,50               |
|                                                     |                |                    |                    |
| **Total Mensal Estimado**                           |                |                    | **177,85 US$**     |
