package ru.panov.taskmanagementsystem.security;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_STRING);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(TOKEN_PREFIX.length());
        String email = null;
        String msg = "";

        try {
            email = jwtService.extractEmail(jwt);
        } catch (SignatureException ex) {
            msg = "Invalid JWT signature";
        } catch (MalformedJwtException ex) {
            msg = "Invalid JWT token";
        } catch (ExpiredJwtException ex) {
            msg = "Expired JWT token";
        } catch (UnsupportedJwtException ex) {
            msg = "Unsupported JWT token";
        } catch (IllegalArgumentException ex) {
            msg = "JWT claims string is empty.";
        }
        if (!msg.isBlank()) {
            StringBuilder sb = new StringBuilder();
            sb.append("{ ");
            sb.append("\"error\": \"Unauthorized\",");
            sb.append("\"message\": \"Invalid Token.\",");
            sb.append("\"path\": \"")
                    .append(request.getRequestURL())
                    .append("\"");
            sb.append("} ");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(sb.toString());
            return;
        }

        if (StringUtils.isNotEmpty(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            } else {
                log.info("Validation fails !!");
            }
        }
        filterChain.doFilter(request, response);
    }
}