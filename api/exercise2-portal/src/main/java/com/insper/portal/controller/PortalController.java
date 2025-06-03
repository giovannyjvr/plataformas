package com.insper.portal.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insper.portal.model.ProductDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
public class PortalController {

    @Autowired
    private RestTemplate restTemplate;

    // Base URLs dos microsserviços (ajuste as portas conforme a sua configuração)
    private final String AUTH_URL = "http://localhost:8001";    // Auth Service
    private final String PRODUCT_URL = "http://localhost:8002"; // Product Service

    // Exibe a página de login
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // src/main/resources/templates/login.html
    }

    // Processa o formulário de login
    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        // Monta o JSON para chamar o Auth Service (exemplo de payload)
        String json = String.format(
          "{\"username\":\"%s\",\"password\":\"%s\"}",
          username, password
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate
                .postForEntity(AUTH_URL + "/login", request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String body = response.getBody();
                
                // ========================
                // Aqui fazemos o parsing JSON:
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(body);
                // Supondo que o Auth Service retorne algo como:
                // { "access_token":"ABC123", "token_type":"bearer", ... }
                String token = node.get("access_token").asText();
                // ========================

                session.setAttribute("TOKEN", token);
                return "redirect:/products";
            } else {
                model.addAttribute("error", "Usuário ou senha inválidos");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao autenticar: " + e.getMessage());
            return "login";
        }
    }

    // Lista todos os produtos (GET /products)
    @GetMapping("/products")
    public String listProducts(HttpSession session, Model model) {
        String token = (String) session.getAttribute("TOKEN");
        if (token == null) {
            return "redirect:/login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<ProductDto[]> response = restTemplate.exchange(
                PRODUCT_URL + "/product",
                HttpMethod.GET,
                request,
                ProductDto[].class
            );
            List<ProductDto> produtos = Arrays.asList(response.getBody());
            model.addAttribute("produtos", produtos);
            return "products"; // src/main/resources/templates/products.html
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao buscar produtos: " + e.getMessage());
            return "products";
        }
    }

    // Exibe formulário de criação (GET /products/new)
    @GetMapping("/products/new")
    public String newProductForm(HttpSession session) {
        if (session.getAttribute("TOKEN") == null) {
            return "redirect:/login";
        }
        return "createProduct"; // src/main/resources/templates/createProduct.html
    }

    // Processa o envio do formulário de criação (POST /products)
    @PostMapping("/products")
    public String createProduct(
            @RequestParam String name,
            @RequestParam double price,
            @RequestParam String unit,
            HttpSession session,
            Model model
    ) {
        String token = (String) session.getAttribute("TOKEN");
        if (token == null) {
            return "redirect:/login";
        }

        // Monta JSON
        String json = String.format(
          "{\"name\":\"%s\",\"price\":%.2f,\"unit\":\"%s\"}",
          name, price, unit
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        try {
            restTemplate.postForEntity(PRODUCT_URL + "/product", request, ProductDto.class);
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao criar produto: " + e.getMessage());
            return "createProduct";
        }
    }

    // Exclui um produto (GET /products/delete/{id})
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(
            @PathVariable("id") String id,
            HttpSession session,
            Model model
    ) {
        String token = (String) session.getAttribute("TOKEN");
        if (token == null) {
            return "redirect:/login";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                PRODUCT_URL + "/product/" + id,
                HttpMethod.DELETE,
                request,
                Void.class
            );
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao deletar produto: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
