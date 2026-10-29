package com.ayush.expense_tracker_system.security;


import com.ayush.expense_tracker_system.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("fetching authorization header");
        String authorization = request.getHeader("Authorization");
        SecurityContextHolder.clearContext();

        if(authorization != null && authorization.startsWith("Bearer")){
            logger.info("fetching jwt token");
            String jwtToken = authorization.substring(7);
            try {
                logger.info("extracting claims from jwt token");
                Claims claims = jwtService.extractClaims(jwtToken);
                logger.info("extracting username");
                String username = claims.getSubject(), role = claims.get("role", String.class);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        List.of(new SimpleGrantedAuthority(role)));

                logger.info("setting authentication object in security context holder");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (JwtException | IllegalArgumentException exception) {
                logger.warn("jwt or illegal argument exception");
                SecurityContextHolder.clearContext();
                throw new JwtException("jwt or illegal argument exception");
            }

        }
        logger.info("forward request in filter chain");
        filterChain.doFilter(request, response);
    }
}
