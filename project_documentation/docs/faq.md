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
