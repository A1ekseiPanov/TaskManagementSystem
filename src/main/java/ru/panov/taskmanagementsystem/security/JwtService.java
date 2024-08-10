package ru.panov.taskmanagementsystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.panov.taskmanagementsystem.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с JSON Web Token (JWT).
 * Этот класс предоставляет методы для создания, проверки и извлечения информации из JWT.
 */
@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private String expiration;

    /**
     * Извлекает email из JWT.
     *
     * @param token JWT токен.
     * @return email, содержащийся в токене.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерирует JWT для заданного пользователя.
     *
     * @param userDetails детали пользователя.
     * @return сгенерированный JWT токен.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getUsername());
            claims.put("roles", customUserDetails.getRoles());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Проверяет, действителен ли JWT токен для заданного пользователя.
     *
     * @param token       JWT токен.
     * @param userDetails детали пользователя.
     * @return {@code true}, если токен действителен, {@code false} в противном случае.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлекает указанное требование (claim) из JWT.
     *
     * @param token           JWT токен.
     * @param claimsResolvers функция для извлечения требуемого значения из Claims.
     * @param <T>             тип извлеченного значения.
     * @return извлеченное значение.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерирует JWT с заданными дополнительными требованиями и деталями пользователя.
     *
     * @param extraClaims дополнительные требования.
     * @param userDetails детали пользователя.
     * @return сгенерированный JWT токен.
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + Integer.parseInt(expiration)))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Проверяет, истек ли срок действия JWT.
     *
     * @param token JWT токен.
     * @return {@code true}, если токен истек, {@code false} в противном случае.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения срока действия JWT.
     *
     * @param token JWT токен.
     * @return дата истечения срока действия токена.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает все требования (claims) из JWT.
     *
     * @param token JWT токен.
     * @return объект {@link Claims}, содержащий все требования токена.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Получает ключ подписи для JWT из секретного ключа.
     *
     * @return объект {@link SecretKey}, используемый для подписи JWT.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}