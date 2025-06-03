package com.insper.productservice.controller;

import com.insper.productservice.config.JwtUtil;
import com.insper.productservice.model.Product;
import com.insper.productservice.repository.ProductRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductRepository repository;

    /**
     * Extrai o token do header Authorization e já valida. 
     * Se inválido, jwtUtil.validateToken lançará RuntimeException.
     * O payload decodificado fica em Claims (pode usar para checar roles, sub, etc).
     */
    private Claims validateRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Cabeçalho Authorization faltando ou mal formatado");
        }
        return jwtUtil.validateToken(authHeader);
    }

    /**
     * POST /product
     * Cria um novo produto. JSON esperado: { "name":"...", "price":10.5, "unit":"kg" }
     * Retorna 201 CREATED e o JSON completo com id, name, price, unit.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product requestBody,
            HttpServletRequest request
    ) {
        // 1) Verifica o token (lança 401 se inválido)
        validateRequest(request);

        // 2) Gera UUID e salva
        UUID newId = UUID.randomUUID();
        Product novo = new Product(newId,
                                   requestBody.getName(),
                                   requestBody.getPrice(),
                                   requestBody.getUnit());
        repository.create(novo);

        return new ResponseEntity<>(novo, HttpStatus.CREATED);
    }

    /**
     * GET /product
     * Retorna lista de todos os produtos. 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Product>> listProducts(HttpServletRequest request) {
        validateRequest(request);
        List<Product> todos = repository.findAll();
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    /**
     * GET /product/{productId}
     * Retorna o produto com aquele ID, ou 404 se não achar.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(
            @PathVariable("productId") UUID productId,
            HttpServletRequest request
    ) {
        validateRequest(request);

        return repository.findById(productId)
                .map(prod -> new ResponseEntity<>(prod, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE /product/{productId}
     * Deleta o produto com aquele ID. Se existir, retorna 204. Se não, retorna 404.
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("productId") UUID productId,
            HttpServletRequest request
    ) {
        validateRequest(request);

        boolean removed = repository.deleteById(productId);
        if (!removed) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
