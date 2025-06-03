package com.insper.productservice.repository;

import com.insper.productservice.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepository {

    // Mapa thread-safe para guardar produtos em memória
    private final Map<UUID, Product> storage = new ConcurrentHashMap<>();

    /**
     * Cria um produto e devolve o objeto recém-criado (com ID gerado).
     */
    public Product create(Product prod) {
        storage.put(prod.getId(), prod);
        return prod;
    }

    /**
     * Lista todos os produtos.
     */
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Busca um produto pelo ID. Se não achar, retorna Optional.empty().
     */
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Remove um produto por ID. Retorna true se removeu, false se não existia.
     */
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }
}
