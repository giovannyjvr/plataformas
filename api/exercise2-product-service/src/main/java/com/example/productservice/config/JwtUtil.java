package com.insper.productservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtUtil {

    // Lê do application.properties:
    @Value("${security.jwt.secret}")
    private String secretKeyPlain;

    @Value("${security.jwt.algorithm}")
    private String algorithm; // por exemplo "HS256"

    private Key secretKey;  

    @PostConstruct
    public void init() {
        // Convertemos a string simples em uma chave HMAC válida.
        // Se você salvou a chave em Base64 no Auth Service, decodifique antes. 
        // Neste exemplo, vamos usar plain-text como bytes UTF-8.
        byte[] keyBytes = secretKeyPlain.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Verifica o token (Bearer <token>). Se inválido ou expirado, 
     * lança RuntimeException. Se válido, retorna os Claims (payload).
     */
    public Claims validateToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (Exception e) {
            // Pode ser: ExpiredJwtException, MalformedJwtException, SignatureException, etc.
            throw new RuntimeException("Token inválido ou expirado");
        }
    }
}
